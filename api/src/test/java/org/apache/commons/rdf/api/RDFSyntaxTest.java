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
package org.apache.commons.rdf.api;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

public class RDFSyntaxTest {

    @Test
    public void byFileExtension() throws Exception {
        assertEquals(RDFSyntax.JSONLD, RDFSyntax.byFileExtension(".jsonld").get());
        assertEquals(RDFSyntax.NQUADS, RDFSyntax.byFileExtension(".nq").get());
        assertEquals(RDFSyntax.NTRIPLES, RDFSyntax.byFileExtension(".nt").get());
        assertEquals(RDFSyntax.RDFA, RDFSyntax.byFileExtension(".html").get());
        assertEquals(RDFSyntax.RDFXML, RDFSyntax.byFileExtension(".rdf").get());
        assertEquals(RDFSyntax.TRIG, RDFSyntax.byFileExtension(".trig").get());
        assertEquals(RDFSyntax.TURTLE, RDFSyntax.byFileExtension(".ttl").get());
    }

    @Test
    public void byFileExtensionFailsWithoutDot() throws Exception {
        assertEquals(Optional.empty(), RDFSyntax.byFileExtension("rdf"));
    }

    @Test
    public void byFileExtensionLowerCase() throws Exception {
        assertEquals(RDFSyntax.TURTLE, RDFSyntax.byFileExtension(".TtL").get());
    }

    @Test
    public void byFileExtensionUnknown() throws Exception {
        assertEquals(Optional.empty(), RDFSyntax.byFileExtension(".tar"));
    }

    @Test
    public void byMediaType() throws Exception {
        assertEquals(RDFSyntax.JSONLD, RDFSyntax.byMediaType("application/ld+json").get());
        assertEquals(RDFSyntax.NQUADS, RDFSyntax.byMediaType("application/n-quads").get());
        assertEquals(RDFSyntax.NTRIPLES, RDFSyntax.byMediaType("application/n-triples").get());
        assertEquals(RDFSyntax.RDFA, RDFSyntax.byMediaType("text/html").get());
        assertEquals(RDFSyntax.RDFA, RDFSyntax.byMediaType("application/xhtml+xml").get());
        assertEquals(RDFSyntax.RDFXML, RDFSyntax.byMediaType("application/rdf+xml").get());
        assertEquals(RDFSyntax.TRIG, RDFSyntax.byMediaType("application/trig").get());
        assertEquals(RDFSyntax.TURTLE, RDFSyntax.byMediaType("text/turtle").get());
    }

    @Test
    public void byMediaTypeContentType() throws Exception {
        assertEquals(RDFSyntax.TURTLE, RDFSyntax.byMediaType("text/turtle; charset=\"UTF-8\"").get());
        assertEquals(RDFSyntax.TURTLE, RDFSyntax.byMediaType("text/turtle ; charset=\"UTF-8\"").get());
        // That's a Content-Type, not media type; we won't split by ","
        assertEquals(Optional.empty(), RDFSyntax.byMediaType("text/turtle, text/plain"));
        // no trimming will be done
        assertEquals(Optional.empty(), RDFSyntax.byMediaType(" text/turtle"));
    }

    @Test
    public void byMediaTypeLowerCase() throws Exception {
        assertEquals(RDFSyntax.JSONLD, RDFSyntax.byMediaType("APPLICATION/ld+JSON").get());
    }

    @Test
    public void byMediaTypeUnknown() throws Exception {
        assertEquals(Optional.empty(), RDFSyntax.byMediaType("application/octet-stream"));
    }

    @Test
    public void fileExtension() throws Exception {
        assertEquals(".jsonld", RDFSyntax.JSONLD.getfileExtension());
        assertEquals(".nq", RDFSyntax.NQUADS.getfileExtension());
        assertEquals(".nt", RDFSyntax.NTRIPLES.getfileExtension());
        assertEquals(".html", RDFSyntax.RDFA.getfileExtension());
        assertEquals(".rdf", RDFSyntax.RDFXML.getfileExtension());
        assertEquals(".trig", RDFSyntax.TRIG.getfileExtension());
        assertEquals(".ttl", RDFSyntax.TURTLE.getfileExtension());
    }

    @Test
    public void fileExtensions() throws Exception {
        assertTrue(RDFSyntax.JSONLD.fileExtensions().contains(".jsonld"));
        assertTrue(RDFSyntax.NQUADS.fileExtensions().contains(".nq"));
        assertTrue(RDFSyntax.NTRIPLES.fileExtensions().contains(".nt"));
        assertTrue(RDFSyntax.RDFA.fileExtensions().contains(".html"));
        assertTrue(RDFSyntax.RDFA.fileExtensions().contains(".xhtml"));
        assertTrue(RDFSyntax.RDFXML.fileExtensions().contains(".rdf"));
        assertTrue(RDFSyntax.TRIG.fileExtensions().contains(".trig"));
        assertTrue(RDFSyntax.TURTLE.fileExtensions().contains(".ttl"));
    }
    
    @Test
    public void mediaType() throws Exception {
        assertEquals("application/ld+json", RDFSyntax.JSONLD.getmediaType());
        assertEquals("application/n-quads", RDFSyntax.NQUADS.getmediaType());
        assertEquals("application/n-triples", RDFSyntax.NTRIPLES.getmediaType());
        assertEquals("text/html", RDFSyntax.RDFA.getmediaType());
        assertEquals("application/rdf+xml", RDFSyntax.RDFXML.getmediaType());
        assertEquals("application/trig", RDFSyntax.TRIG.getmediaType());
        assertEquals("text/turtle", RDFSyntax.TURTLE.getmediaType());
    }


    @Test
    public void mediaTypes() throws Exception {
        assertTrue(RDFSyntax.JSONLD.mediaTypes().contains("application/ld+json"));
        assertTrue(RDFSyntax.NQUADS.mediaTypes().contains("application/n-quads"));
        assertTrue(RDFSyntax.NTRIPLES.mediaTypes().contains("application/n-triples"));
        assertTrue(RDFSyntax.RDFA.mediaTypes().contains("text/html"));
        assertTrue(RDFSyntax.RDFA.mediaTypes().contains("application/xhtml+xml"));
        assertTrue(RDFSyntax.RDFXML.mediaTypes().contains("application/rdf+xml"));
        assertTrue(RDFSyntax.TRIG.mediaTypes().contains("application/trig"));
        assertTrue(RDFSyntax.TURTLE.mediaTypes().contains("text/turtle"));
    }
    
    @Test
    public void string() throws Exception {
        assertEquals("JSON-LD 1.0", RDFSyntax.JSONLD.toString());
        assertEquals("RDF 1.1 Turtle", RDFSyntax.TURTLE.toString());
    }

    @Test
    public void byName() throws Exception {
        for (RDFSyntax s : RDFSyntax.w3cSyntaxes()) {
            assertEquals(s, RDFSyntax.byName(s.getname()).get());
        }
    }
    
}
