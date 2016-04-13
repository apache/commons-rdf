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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFParserBuilder;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class AbstractRDFParserBuilderTest {

	private RDFTermFactory factory = new SimpleRDFTermFactory();
	
	private DummyRDFParserBuilder dummyParser = new DummyRDFParserBuilder();
	private Path testNt;
	private Path testTtl;
	private Path testXml;

	@Before
	public void createTempFile() throws IOException {
		testNt = Files.createTempFile("test", ".nt");
		testTtl = Files.createTempFile("test", ".ttl");
		testXml = Files.createTempFile("test", ".xml");

		// No need to populate the files as the dummy parser
		// doesn't actually read anything
	}

	@After
	public void deleteTempFiles() throws IOException {
		Files.deleteIfExists(testNt);
		Files.deleteIfExists(testTtl);
		Files.deleteIfExists(testXml);
	}

	@Test
	public void guessRDFSyntax() throws Exception {
		assertEquals(RDFSyntax.NTRIPLES, AbstractRDFParserBuilder.guessRDFSyntax(testNt).get());
		assertEquals(RDFSyntax.TURTLE, AbstractRDFParserBuilder.guessRDFSyntax(testTtl).get());
		assertFalse(AbstractRDFParserBuilder.guessRDFSyntax(testXml).isPresent());
	}

	private void checkGraph(Graph g) throws Exception {				
		assertTrue(g.size() > 0);		
		IRI greeting = factory.createIRI("http://example.com/greeting");	
		// Should only have parsed once!
		assertEquals(1, g.getTriples(null, greeting, null).count());
		Triple triple = g.getTriples(null, greeting, null).findAny().get();
		assertTrue(triple.getSubject() instanceof IRI);
		IRI parsing = (IRI) triple.getSubject();
		assertTrue(parsing.getIRIString().startsWith("urn:uuid:"));

		assertEquals("http://example.com/greeting", triple.getPredicate().getIRIString());

		assertTrue(triple.getObject() instanceof Literal);
		Literal literal = (Literal) triple.getObject();
		assertEquals("Hello world", literal.getLexicalForm());
		assertFalse(literal.getLanguageTag().isPresent());
		assertEquals(Types.XSD_STRING, literal.getDatatype());
		
		// Check uniqueness of properties that are always present
		assertEquals(1, 
				g.getTriples(null, factory.createIRI("http://example.com/source"), null).count());
		
		// Check optional properties that are unique
		assertTrue(2 > g.getTriples(null, factory.createIRI("http://example.com/base"), null).count());
		assertTrue(2 > g.getTriples(null, factory.createIRI("http://example.com/contentType"), null).count());
		assertTrue(2 > g.getTriples(null, factory.createIRI("http://example.com/contentTypeSyntax"), null).count());
	}
	
	@Test
	public void parseFile() throws Exception {	
		Graph g = factory.createGraph();
		RDFParserBuilder parser = dummyParser.source(testNt).target(g);
		parser.parse().get(5, TimeUnit.SECONDS);
		checkGraph(g);
		// FIXME: this could potentially break if the equivalent of /tmp includes
		// international characters
		assertEquals("<" + testNt.toUri().toString() + ">", firstPredicate(g, "source"));
		// Should be set to the file path
		assertEquals("<" + testNt.toUri().toString() + ">", firstPredicate(g, "base"));		

		// Should NOT have guessed the content type
		assertNull(firstPredicate(g, "contentType"));
		assertNull(firstPredicate(g, "contentTypeSyntax"));
	}


	@Test
	public void parseNoSource() throws Exception {
		thrown.expect(IllegalStateException.class);
		dummyParser.parse();		
	}
	
	@Test
	public void parseBaseAndContentTypeNoSource() throws Exception {
		// Can set the other options, even without source()
		IRI base = dummyParser.createRDFTermFactory().createIRI("http://www.example.org/test.rdf");
		RDFParserBuilder parser = dummyParser.base(base).contentType(RDFSyntax.RDFXML);
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("No source has been set");
		// but .parse() should fail
		parser.parse();		
	}
	
	@Test
	public void parseFileMissing() throws Exception {
		Files.delete(testNt);
		// This should not fail yet
		RDFParserBuilder parser = dummyParser.source(testNt);
		// but here:
		thrown.expect(IOException.class);
		parser.parse();		
	}

	
	@Test
	public void parseFileContentType() throws Exception {
		Graph g = factory.createGraph();
		RDFParserBuilder parser = dummyParser
				.source(testNt)
				.contentType(RDFSyntax.NTRIPLES)
				.target(g);
		parser.parse().get(5, TimeUnit.SECONDS);
		checkGraph(g);
		// FIXME: this could potentially break if the equivalent of /tmp includes
		// international characters
		assertEquals("<" + testNt.toUri().toString() + ">", firstPredicate(g, "source"));
		assertEquals("<" + testNt.toUri().toString() + ">", firstPredicate(g, "base"));		
		assertEquals("\"NTRIPLES\"", firstPredicate(g, "contentTypeSyntax"));
		assertEquals("\"application/n-triples\"", firstPredicate(g, "contentType"));
	}

	private String firstPredicate(Graph g, String pred) {
		return g.getTriples(null, factory.createIRI("http://example.com/" + pred), null)
				.map(Triple::getObject).map(RDFTerm::ntriplesString).findAny().orElse(null);
	}


	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void parseInputStreamFailsIfBaseMissing() throws Exception {
		InputStream inputStream = new ByteArrayInputStream(new byte[0]);
		// Should not fail at this point
		RDFParserBuilder parser = dummyParser.source(inputStream);
		// but here:
		thrown.expect(IllegalStateException.class);
		thrown.expectMessage("base iri required for inputstream source");
		parser.parse();
	}

	@Test
	public void parseInputStreamWithBase() throws Exception {
		InputStream inputStream = new ByteArrayInputStream(new byte[0]);
		IRI base = dummyParser.createRDFTermFactory().createIRI("http://www.example.org/test.rdf");
		Graph g = factory.createGraph();
		RDFParserBuilder parser = dummyParser.source(inputStream).base(base).target(g);		
		parser.parse().get(5, TimeUnit.SECONDS);
		checkGraph(g);
		assertEquals("<http://www.example.org/test.rdf>", firstPredicate(g, "base"));
		// in our particular debug output, 
		// bnode source indicates InputStream 
		assertTrue(firstPredicate(g, "source").startsWith("_:"));
		assertNull(firstPredicate(g, "contentType"));
		assertNull(firstPredicate(g, "contentTypeSyntax"));
	}
	
	@Test
	public void parseInputStreamWithNQuads() throws Exception {
		InputStream inputStream = new ByteArrayInputStream(new byte[0]);
		Graph g = factory.createGraph();
		RDFParserBuilder parser = dummyParser.source(inputStream).contentType(RDFSyntax.NQUADS).target(g);		
		parser.parse().get(5, TimeUnit.SECONDS);
		checkGraph(g);
		assertNull(firstPredicate(g, "base"));
		// in our particular debug output, 
		// bnode source indicates InputStream 
		assertTrue(firstPredicate(g, "source").startsWith("_:"));
		assertEquals("\"application/n-quads\"", firstPredicate(g, "contentType"));
		assertEquals("\"NQUADS\"", firstPredicate(g, "contentTypeSyntax"));
	}	

	@Test
	public void parseIRI() throws Exception {
		IRI iri = dummyParser.createRDFTermFactory().createIRI("http://www.example.net/test.ttl");
		Graph g = factory.createGraph();
		RDFParserBuilder parser = dummyParser.source(iri).target(g);		
		parser.parse().get(5, TimeUnit.SECONDS);
		checkGraph(g);
		assertEquals("<http://www.example.net/test.ttl>", firstPredicate(g, "source"));
		// No base - assuming the above IRI is always 
		// the base would break server-supplied base from 
		// any HTTP Location redirects and  Content-Location header
		assertNull(firstPredicate(g, "base"));
		// ".ttl" in IRI string does not imply any content type
		assertNull(firstPredicate(g, "contentType"));
		assertNull(firstPredicate(g, "contentTypeSyntax"));
		
	}
	
	@Test
	public void parseIRIBaseContentType() throws Exception {
		IRI iri = dummyParser.createRDFTermFactory().createIRI("http://www.example.net/test.ttl");
		Graph g = factory.createGraph();
		RDFParserBuilder parser = dummyParser.source(iri).base(iri).contentType(RDFSyntax.TURTLE).target(g);
		parser.parse().get(5, TimeUnit.SECONDS);
		checkGraph(g);
		assertEquals("<http://www.example.net/test.ttl>", firstPredicate(g, "source"));
		assertEquals("<http://www.example.net/test.ttl>", firstPredicate(g, "base"));
		assertEquals("\"TURTLE\"", firstPredicate(g, "contentTypeSyntax"));
		assertEquals("\"text/turtle\"", firstPredicate(g, "contentType"));
	}

	
}
