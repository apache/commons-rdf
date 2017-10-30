/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
package org.apache.commons.rdf.integrationtests;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.jena.JenaDataset;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.commons.rdf.jsonldjava.JsonLdGraph;
import org.apache.commons.rdf.jsonldjava.JsonLdRDF;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.apache.commons.rdf.rdf4j.RDF4JGraph;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.apache.jena.riot.RDFDataMgr;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.junit.Before;
import org.junit.Test;

import com.github.jsonldjava.core.JsonLdOptions;
import com.github.jsonldjava.core.JsonLdProcessor;
import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.utils.JsonUtils;

/**
 * COMMONSRDF-57 etc: For upgrades, ensure JSONLD-Java parses well in all
 * implementations even if they might have slightly incompatible versions of
 * their dependencies.
 * <p>
 * The <code>*Embedded</code> tests parse <code>alice-embedded.jsonld</code>
 * from the test classpath through Jena, RDF4J and JSONLD-Java and verifies it
 * contains the expected triples using {@link #checkGraph(Graph)}. This ensures
 * that the versions of JSONLD-Java and Jackson are compatible with Jena and
 * RDF4J.
 * <p>
 * The <code>*Cached</code> tests parse <code>alice-cached.jsonld</code>, which
 * references an external <code>@context</code> of http://example.com/context -
 * but using the <a href=
 * "https://github.com/jsonld-java/jsonld-java#loading-contexts-from-classpathjar">jarcache.json</a>
 * mechanism of JSONLD-Java, this context will be loaded from
 * <code>contexts/example.jsonld</code> on the test classpath instead. This
 * ensures that the versions of HTTPClient is compatible with JSONLD-Java
 * (however it does not check that it is compatible with Jena and
 * RDF4J's external fetching of RDF documents).
 *
 */
public class JSONLDParsingTest {

    static RDF rdf = new SimpleRDF();
    static IRI alice = rdf.createIRI("http://example.com/Alice");
    static IRI name = rdf.createIRI("http://schema.org/name");
    static IRI type = rdf.createIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    static IRI person = rdf.createIRI("http://schema.org/Person");
    static Literal aliceWLand = rdf.createLiteral("Alice W. Land");

    URL aliceCached = getClass().getResource("/alice-cached.jsonld");
    URL aliceEmbedded = getClass().getResource("/alice-embedded.jsonld");

    /**
     * Pre-test that src/test/resources files are on the classpath
     *
     */
    @Before
    public void checkTestResources() throws Exception {
        aliceCached.openStream().close();
        aliceEmbedded.openStream().close();
        // Used by JSONLD-Java to avoid external dependencies. See
        // https://github.com/jsonld-java/jsonld-java#loading-contexts-from-classpathjar
        getClass().getResourceAsStream("/jarcache.json").close();
        getClass().getResourceAsStream("/contexts/example.jsonld").close();
        // (We'll use these to ensure our HTTPClient dependency works)
    }

    private void checkGraph(final Graph g) {
        assertTrue(g.contains(alice, name, aliceWLand));
        assertTrue(g.contains(alice, type, person));
    }

    @Test
    public void jenaParseEmbedded() throws Exception {
        jenaParse(aliceEmbedded);
    }

    @Test
    public void jenaParseCached() throws Exception {
        // Check if HTTPClient cache is used from
        // jarcache.json
        jenaParse(aliceCached);
    }

    private void jenaParse(final URL url) throws Exception {
        try (final JenaDataset dataset = new JenaRDF().createDataset()) {
            RDFDataMgr.read(dataset.asJenaDatasetGraph(), url.toExternalForm());
            checkGraph(dataset.getGraph());
        }
    }

    @Test
    public void rdf4jParseEmbedded() throws Exception {
        rdf4jParse(aliceEmbedded);
    }

    @Test
    public void rdf4jParseCached() throws Exception {
        // Check if HTTPClient cache is used from
        // jarcache.json
        rdf4jParse(aliceCached);
    }

    private void rdf4jParse(final URL url) throws Exception {
        Model model;
        try (InputStream in = url.openStream()) {
            model = Rio.parse(in, url.toExternalForm(), RDFFormat.JSONLD);
        }
        try (final RDF4JGraph graph = new RDF4J().asGraph(model)) {
            checkGraph(graph);
        }
    }

    @Test
    public void jsonldParseEmbedded() throws Exception {
        jsonldParse(aliceEmbedded);
    }

    @Test
    public void jsonldParseCached() throws Exception {
        // Check if HTTPClient cache is used from
        // jarcache.json
        jsonldParse(aliceCached);
    }

    private void jsonldParse(final URL url) throws Exception {
        final Object aliceJson = JsonUtils.fromURL(url, JsonUtils.getDefaultHttpClient());
        final JsonLdOptions options = new JsonLdOptions();
        options.setBase(url.toExternalForm());
        final RDFDataset ds = (RDFDataset) JsonLdProcessor.toRDF(aliceJson);
        try (final JsonLdGraph graph = new JsonLdRDF().asGraph(ds)) {
            checkGraph(graph);
        }
    }
}
