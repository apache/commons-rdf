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
package org.apache.commons.rdf.simple;

import static org.junit.Assert.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFParserBuilder;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.Triple;
import org.junit.Test;

public class AbstractRDFParserBuilderTest {

	/**
	 * Test a basic parsing of an N-Triples-file
	 * 
	 * @throws Exception
	 */
	@Test
	public void parseFile() throws Exception {		
		Path file = Files.createTempFile("test", ".nt");
		// No need to populate the file as the dummy parser
		// doesn't actually read anything
		
		RDFParserBuilder parser = new DummyRDFParserBuilder()
				.source(file).contentType(RDFSyntax.NTRIPLES);
		Future<Graph> f = parser.parse();
		Graph g = f.get(5, TimeUnit.SECONDS);
		
		assertEquals(1, g.size());
		Triple triple = g.getTriples().findAny().get();
		assertTrue(triple.getSubject() instanceof IRI);
		IRI iri = (IRI)triple.getSubject();
		assertEquals("http://example.com/test1", iri.getIRIString());
		
		assertEquals("http://example.com/greeting", triple.getPredicate().getIRIString());
		
		assertTrue(triple.getObject() instanceof Literal);
		Literal literal = (Literal)triple.getObject();
		assertEquals("Hello world", literal.getLexicalForm());
		assertFalse(literal.getLanguageTag().isPresent());
		assertEquals(Types.XSD_STRING, literal.getDatatype());
	}
}
