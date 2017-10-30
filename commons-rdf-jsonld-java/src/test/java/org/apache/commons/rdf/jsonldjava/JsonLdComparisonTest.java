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

import static org.junit.Assert.*;

import java.util.Optional;

import org.apache.commons.rdf.simple.Types;
import org.junit.Test;

/**
 * COMMONSRDF-56: Test Literal comparisons with JSONLD-Java
 */
public class JsonLdComparisonTest {
    
    JsonLdRDF rdf = new JsonLdRDF();
    
    @Test
    public void literalEqual() throws Exception {
        JsonLdLiteral lit1 = rdf.createLiteral("Hello");
        JsonLdLiteral lit2 = rdf.createLiteral("Hello");
        JsonLdLiteral lit3 = rdf.createLiteral("Hello", Types.XSD_STRING);
        assertEquals(lit1, lit2);
        assertEquals(lit1, lit3);
    }

    @Test
    public void literalNotEqual() throws Exception {
        JsonLdLiteral lit1 = rdf.createLiteral("Hello");
        JsonLdLiteral lit2 = rdf.createLiteral("Hello there");
        assertNotEquals(lit1, lit2);
    }

    @Test
    public void literalEqualLang() throws Exception {
        JsonLdLiteral lit1 = rdf.createLiteral("Allo Allo", "fr");
        JsonLdLiteral lit2 = rdf.createLiteral("Allo Allo", "fr");
        assertEquals(lit1, lit2);
    }
    
    @Test
    public void literalNotEqualLang() throws Exception {
        JsonLdLiteral lit1 = rdf.createLiteral("Hello", "en");
        JsonLdLiteral lit2 = rdf.createLiteral("Hello", "en-us");
        assertNotEquals(lit1, lit2);
    }

    @Test
    public void literalEqualType() throws Exception {
        JsonLdLiteral lit1 = rdf.createLiteral("1", Types.XSD_INTEGER);
        JsonLdLiteral lit2 = rdf.createLiteral("1", Types.XSD_INTEGER);
        assertEquals(lit1, lit2);
    }

    
    @Test
    public void literalNotEqualType() throws Exception {
        JsonLdLiteral lit1 = rdf.createLiteral("1", Types.XSD_INTEGER);
        JsonLdLiteral lit2 = rdf.createLiteral("2", Types.XSD_INTEGER);
        JsonLdLiteral lit3 = rdf.createLiteral("1", Types.XSD_STRING);

        assertNotEquals(lit1, lit2);
        assertNotEquals(lit1, lit3);
    }


    @Test
    public void grahContains() throws Exception {
        JsonLdGraph graph = rdf.createGraph();
        JsonLdIRI s = rdf.createIRI("http://example.com/s");
        JsonLdIRI p = rdf.createIRI("http://example.com/p");
        JsonLdLiteral lit1 = rdf.createLiteral("Hello");
        
        graph.add(s, p, lit1);
        assertTrue(graph.contains(s, p, rdf.createLiteral("Hello")));
        assertTrue(graph.contains(s, p, rdf.createLiteral("Hello", Types.XSD_STRING)));
        assertFalse(graph.contains(s, p, rdf.createLiteral("Hello", Types.XSD_NORMALIZEDSTRING)));
        assertFalse(graph.contains(s, p, rdf.createLiteral("Hello", "en")));
        assertFalse(graph.contains(s, p, rdf.createLiteral("Other")));
    }
    
    @Test
    public void datasetContains() throws Exception {
        JsonLdDataset dataset = rdf.createDataset();
        JsonLdIRI s = rdf.createIRI("http://example.com/s");
        JsonLdIRI p = rdf.createIRI("http://example.com/p");
        JsonLdLiteral lit1 = rdf.createLiteral("Hello");
        
        dataset.add(null, s, p, lit1);
        assertTrue(dataset.contains(Optional.empty(), s, p, rdf.createLiteral("Hello")));
        assertTrue(dataset.contains(Optional.empty(), s, p, rdf.createLiteral("Hello", Types.XSD_STRING)));
        assertFalse(dataset.contains(Optional.empty(), s, p, rdf.createLiteral("Hello", Types.XSD_NORMALIZEDSTRING)));
        assertFalse(dataset.contains(Optional.empty(), s, p, rdf.createLiteral("Hello", "en")));
        assertFalse(dataset.contains(Optional.empty(), s, p, rdf.createLiteral("Other")));
    }
    
    @Test
    public void datasetRemove() throws Exception {
        JsonLdDataset dataset = rdf.createDataset();
        JsonLdIRI s = rdf.createIRI("http://example.com/s");
        JsonLdIRI p = rdf.createIRI("http://example.com/p");
        JsonLdLiteral lit1 = rdf.createLiteral("Hello");
        
        dataset.add(null, s, p, lit1);
        assertTrue(dataset.contains(Optional.empty(), s, p, lit1));
        dataset.remove(null, null, null, rdf.createLiteral("Other")); // should NOT match
        assertTrue(dataset.contains(Optional.empty(), s, p, lit1));
        dataset.remove(null, null, null, rdf.createLiteral("Hello", Types.XSD_STRING)); // SHOULD  match
        assertFalse(dataset.contains(Optional.empty(), s, p, lit1));
    }

    @Test
    public void datasetStream() throws Exception {
        JsonLdDataset dataset = rdf.createDataset();
        JsonLdIRI s = rdf.createIRI("http://example.com/s");
        JsonLdIRI p = rdf.createIRI("http://example.com/p");
        JsonLdLiteral lit1 = rdf.createLiteral("Hello");
        JsonLdLiteral lit2 = rdf.createLiteral("Other");
        
        dataset.add(null, s, p, lit1);
        assertTrue(dataset.stream(Optional.empty(), s, p, lit1).findAny().isPresent());        
        assertFalse(dataset.stream(Optional.empty(), s, p, lit2).findAny().isPresent());        
    }
    
}
