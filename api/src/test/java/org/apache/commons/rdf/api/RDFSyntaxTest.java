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
	public void mediaType() throws Exception {
		assertEquals("application/ld+json", RDFSyntax.JSONLD.mediaType);
		assertEquals("application/n-quads", RDFSyntax.NQUADS.mediaType);
		assertEquals("application/n-triples", RDFSyntax.NTRIPLES.mediaType);
		assertEquals("text/html", RDFSyntax.RDFA_HTML.mediaType);
		assertEquals("application/xhtml+xml", RDFSyntax.RDFA_XHTML.mediaType);
		assertEquals("application/rdf+xml", RDFSyntax.RDFXML.mediaType);
		assertEquals("application/trig", RDFSyntax.TRIG.mediaType);
		assertEquals("text/turtle", RDFSyntax.TURTLE.mediaType);
	}

	@Test
	public void byMediaType() throws Exception {
		assertEquals(RDFSyntax.JSONLD, RDFSyntax.byMediaType("application/ld+json").get());
		assertEquals(RDFSyntax.NQUADS, RDFSyntax.byMediaType("application/n-quads").get());
		assertEquals(RDFSyntax.NTRIPLES, RDFSyntax.byMediaType("application/n-triples").get());
		assertEquals(RDFSyntax.RDFA_HTML, RDFSyntax.byMediaType("text/html").get());
		assertEquals(RDFSyntax.RDFA_XHTML, RDFSyntax.byMediaType("application/xhtml+xml").get());
		assertEquals(RDFSyntax.RDFXML, RDFSyntax.byMediaType("application/rdf+xml").get());
		assertEquals(RDFSyntax.TRIG, RDFSyntax.byMediaType("application/trig").get());
		assertEquals(RDFSyntax.TURTLE, RDFSyntax.byMediaType("text/turtle").get());
	}

	@Test
	public void name() throws Exception {
		assertEquals("JSON-LD 1.0", RDFSyntax.JSONLD.toString());
		assertEquals("RDF 1.1 Turtle", RDFSyntax.TURTLE.toString());
	}

	@Test
	public void byMediaTypeUnknown() throws Exception {
		assertEquals(Optional.empty(), RDFSyntax.byMediaType("application/octet-stream"));
	}

	@Test
	public void byMediaTypeLowerCase() throws Exception {
		assertEquals(RDFSyntax.JSONLD, RDFSyntax.byMediaType("APPLICATION/ld+JSON").get());
	}

	@Test
	public void byMediaTypeContentType() throws Exception {
		assertEquals(RDFSyntax.TURTLE, RDFSyntax.byMediaType("text/turtle; charset=\"UTF-8\"").get());
		assertEquals(RDFSyntax.TURTLE, RDFSyntax.byMediaType("text/turtle ; charset=\"UTF-8\"").get());
		assertEquals(RDFSyntax.TURTLE, RDFSyntax.byMediaType("text/turtle, text/plain").get());
		assertEquals(Optional.empty(), RDFSyntax.byMediaType(" text/turtle"));
	}

}
