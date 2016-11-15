/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rdf.jsonldjava.experimental;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.function.Predicate;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.jsonldjava.JsonLdDataset;
import org.apache.commons.rdf.jsonldjava.JsonLdGraph;
import org.apache.commons.rdf.jsonldjava.JsonLdRDF;
import org.apache.commons.rdf.simple.experimental.AbstractRDFParser;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.utils.JsonUtils;

public class JsonLdParser extends AbstractRDFParser<JsonLdParser> {

    @Override
    protected JsonLdRDF createRDFTermFactory() {
        return new JsonLdRDF();
    }

    @Override
    public JsonLdParser contentType(final RDFSyntax rdfSyntax) throws IllegalArgumentException {
        if (rdfSyntax != null && rdfSyntax != RDFSyntax.JSONLD) {
            throw new IllegalArgumentException("Unsupported contentType: " + rdfSyntax);
        }
        return super.contentType(rdfSyntax);
    }

    @Override
    public JsonLdParser contentType(final String contentType) throws IllegalArgumentException {
        final JsonLdParser c = super.contentType(contentType);
        if (c.getContentType().filter(Predicate.isEqual(RDFSyntax.JSONLD).negate()).isPresent()) {
            throw new IllegalArgumentException("Unsupported contentType: " + contentType);
        }
        return c;
    }

    private static URL asURL(final IRI iri) throws IllegalStateException {
        try {
            return new URI(iri.getIRIString()).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new IllegalStateException("Invalid URL: " + iri.getIRIString());
        }
    }

    @Override
    protected void checkSource() throws IOException {
        super.checkSource();
        // Might throw IllegalStateException if invalid
        getSourceIri().map(JsonLdParser::asURL);
    }

    @Override
    protected void parseSynchronusly() throws IOException {
        final Object json = readSource();
        final JsonLdOptions options = new JsonLdOptions();
        getBase().map(IRI::getIRIString).ifPresent(options::setBase);
        // TODO: base from readSource() (after redirection and Content-Location
        // header)
        // should be forwarded

        // TODO: Modify JsonLdProcessor to accept the target RDFDataset
        RDFDataset rdfDataset;
        try {
            rdfDataset = (RDFDataset) JsonLdProcessor.toRDF(json, options);
        } catch (final JsonLdError e) {
            throw new IOException("Could not parse Json-LD", e);
        }
        if (getTargetGraph().isPresent()) {
            final Graph intoGraph = getTargetGraph().get();
            if (intoGraph instanceof JsonLdGraph && !intoGraph.contains(null, null, null)) {
                // Empty graph, we can just move over the map content directly:
                final JsonLdGraph jsonLdGraph = (JsonLdGraph) intoGraph;
                jsonLdGraph.getRdfDataSet().putAll(rdfDataset);
                return;
                // otherwise we have to merge as normal
            }
            // TODO: Modify JsonLdProcessor to have an actual triple callback
            final Graph parsedGraph = getJsonLdFactory().asGraph(rdfDataset);
            // sequential() as we don't know if destination is thread safe :-/
            parsedGraph.stream().sequential().forEach(intoGraph::add);
        } else if (getTargetDataset().isPresent()) {
            final Dataset intoDataset = getTargetDataset().get();
            if (intoDataset instanceof JsonLdDataset && !intoDataset.contains(null, null, null, null)) {
                final JsonLdDataset jsonLdDataset = (JsonLdDataset) intoDataset;
                // Empty - we can just do a brave replace!
                jsonLdDataset.getRdfDataSet().putAll(rdfDataset);
                return;
                // otherwise we have to merge.. but also avoid duplicate
                // triples,
                // map blank nodes etc, so we'll fall back to normal Dataset
                // appending.
            }
            final Dataset fromDataset = getJsonLdFactory().asDataset(rdfDataset);
            // .sequential() as we don't know if destination is thread-safe :-/
            fromDataset.stream().sequential().forEach(intoDataset::add);
        } else {
            final Dataset fromDataset = getJsonLdFactory().asDataset(rdfDataset);
            // No need for .sequential() here
            fromDataset.stream().forEach(getTarget());
        }
    }

    private JsonLdRDF getJsonLdFactory() {
        if (getRdfTermFactory().isPresent() && getRdfTermFactory().get() instanceof JsonLdRDF) {
            return (JsonLdRDF) getRdfTermFactory().get();
        }
        return createRDFTermFactory();
    }

    private Object readSource() throws IOException {
        // Due to checked IOException we can't easily
        // do this with .map and .orElseGet()

        if (getSourceInputStream().isPresent()) {
            return JsonUtils.fromInputStream(getSourceInputStream().get());
        }
        if (getSourceIri().isPresent()) {
            // TODO: propagate @base from content
            return JsonUtils.fromURL(asURL(getSourceIri().get()), JsonUtils.getDefaultHttpClient());
        }
        if (getSourceFile().isPresent()) {
            try (InputStream inputStream = Files.newInputStream(getSourceFile().get())) {
                return JsonUtils.fromInputStream(inputStream);
            }
        }
        throw new IllegalStateException("No known source found");
    }

}
