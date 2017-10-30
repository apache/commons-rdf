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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.jsonldjava.experimental.JsonLdParser;
import org.apache.commons.rdf.simple.Types;
import org.junit.Test;

public class JsonLdParserBuilderTest {
    private static final String TEST_JSONLD = "/test.jsonld";

    static JsonLdRDF factory = new JsonLdRDF();
    IRI test = factory.createIRI("http://example.com/test");
    IRI Type = factory.createIRI("http://example.com/Type");
    IRI type = factory.createIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    IRI pred1 = factory.createIRI("http://example.com/pred1");
    IRI pred2 = factory.createIRI("http://example.com/pred2");
    IRI pred3 = factory.createIRI("http://example.com/pred3");
    IRI pred4 = factory.createIRI("http://example.com/pred4");
    IRI other = factory.createIRI("http://example.com/other");
    IRI graph = factory.createIRI("http://example.com/graph");

    @Test
    public void parseByUrl() throws Exception {
        final URL url = getClass().getResource(TEST_JSONLD);
        assertNotNull("Test resource not found: " + TEST_JSONLD, url);
        final IRI iri = factory.createIRI(url.toString());
        try (final Graph g = factory.createGraph()) {
            new JsonLdParser().contentType(RDFSyntax.JSONLD).source(iri).target(g).parse().get(10, TimeUnit.SECONDS);
            checkGraph(g);
        }
    }

    @Test
    public void parseByPath() throws Exception {
        final Path path = Files.createTempFile("test", ".jsonld");
        path.toFile().deleteOnExit();
        try (InputStream is = getClass().getResourceAsStream(TEST_JSONLD)) {
            assertNotNull("Test resource not found: " + TEST_JSONLD, is);
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        }
        try (final Graph g = factory.createGraph()) {
            new JsonLdParser().contentType(RDFSyntax.JSONLD).source(path).target(g).parse().get(10, TimeUnit.SECONDS);
            checkGraph(g);
        }
    }

    @Test
    public void parseByStream() throws Exception {
        try (final Graph g = factory.createGraph()) {
            try (InputStream is = getClass().getResourceAsStream(TEST_JSONLD)) {
                assertNotNull("Test resource not found: " + TEST_JSONLD, is);
                new JsonLdParser().base("http://example.com/base/").contentType(RDFSyntax.JSONLD).source(is).target(g)
                        .parse().get(10, TimeUnit.SECONDS);
            }
            checkGraph(g);
        }
    }

    private void checkGraph(final Graph g) {
        assertTrue(g.contains(test, type, Type));
        // Should not include statements from the named graph

        assertEquals(1, g.stream(test, pred1, null).count());
        assertEquals(1, g.stream(test, pred2, null).count());

        assertEquals("Hello", ((Literal) g.stream(test, pred1, null).findFirst().get().getObject()).getLexicalForm());
        assertTrue(g.contains(test, pred2, other));

        assertEquals("1337", ((Literal) g.stream(test, pred3, null).findFirst().get().getObject()).getLexicalForm());
        assertEquals(Types.XSD_INTEGER,
                ((Literal) g.stream(test, pred3, null).findFirst().get().getObject()).getDatatype());

        // While the named graph 'graph' is not included, the relationship
        // to that @id is included.
        assertTrue(g.contains(test, pred4, graph));
    }
}
