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
package org.apache.commons.rdf.jsonldjava;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.function.Predicate;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFParserBuilder;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.simple.AbstractRDFParserBuilder;

import com.github.jsonldjava.core.JsonLdError;
import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.utils.JsonUtils;

public class JsonLdParserBuilder extends AbstractRDFParserBuilder {

	@Override
	protected RDFTermFactory createRDFTermFactory() {
		if (getIntoGraph().map(x -> x instanceof JsonLdGraph).orElse(true)) {
			JsonLdGraph graph = (JsonLdGraph) getIntoGraph().get();
			return new JsonLdRDFTermFactory(graph.bnodePrefix());
		} else {
			// other kind of intoGraph - we'll let SimpleRDFTermFactory
			// do the job slightly more efficiently instead
			return super.createRDFTermFactory();
		}
	}

	@Override
	public RDFParserBuilder contentType(RDFSyntax rdfSyntax) throws IllegalArgumentException {
		if (rdfSyntax != null && rdfSyntax != RDFSyntax.JSONLD) { 
			throw new IllegalArgumentException("Unsupported contentType: " + rdfSyntax);
		}
		return super.contentType(rdfSyntax);
	}
	
	@Override
	public RDFParserBuilder contentType(String contentType) throws IllegalArgumentException {
		JsonLdParserBuilder c = (JsonLdParserBuilder) super.contentType(contentType);
		if (c.getContentType().filter(Predicate.isEqual(RDFSyntax.JSONLD).negate()).isPresent()) {
			throw new IllegalArgumentException("Unsupported contentType: " + contentType);
		}
		return c;		
	}

	private static URL asURL(IRI iri) throws IllegalStateException {
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
		getSourceIri().map(JsonLdParserBuilder::asURL);
	}
	
	@Override
	protected void parseSynchronusly() throws IOException, RDFParseException {		
		Object json = readSource();
		JsonLdOptions options = new JsonLdOptions();
		getBase().map(IRI::getIRIString).ifPresent(options::setBase);
		// TODO: base from readSource() (after redirection and Content-Location header) 
		// should be forwarded		

		// TODO: Modify JsonLdProcessor to accept the target RDFDataset
		RDFDataset rdfDataset;
		try {
			rdfDataset = (RDFDataset) JsonLdProcessor.toRDF(json, options);
		} catch (JsonLdError e) {
			throw new RDFParseException(e);
		}

		Graph intoGraph = getIntoGraph().get();
		if (intoGraph instanceof JsonLdGraph) {
			// We can just move over the map content directly:
			JsonLdGraph jsonLdGraph = (JsonLdGraph) intoGraph;
			jsonLdGraph.getRdfDataSet().putAll(rdfDataset);				
		} else {
			// TODO: Modify JsonLdProcessor to have an actual triple callback
			try (JsonLdGraph parsedGraph = new JsonLdGraph(rdfDataset)) {
				// sequential() as we don't know if intoGraph is thread safe :-/
				parsedGraph.getTriples().sequential().forEach(intoGraph::add);
			}
		}
	}
	
	private Object readSource() throws IOException, RDFParseException {
		// Due to checked IOException we can't easily 
		// do this with .map and .orElseGet()
		
		if (getSourceInputStream().isPresent()) {
			return JsonUtils.fromInputStream(getSourceInputStream().get());
		}
		if (getSourceIri().isPresent()) {
			// TODO: propagate @base from content
			return JsonUtils.fromURL(asURL(getSourceIri().get()), 
					JsonUtils.getDefaultHttpClient());			
		}
		if (getSourceFile().isPresent()) {
			try (InputStream inputStream = Files.newInputStream(getSourceFile().get())){
				return JsonUtils.fromInputStream(inputStream);
			} 			
		}
		throw new IllegalStateException("No known source found");
	}

}

