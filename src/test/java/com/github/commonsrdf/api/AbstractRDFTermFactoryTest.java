/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.commonsrdf.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractRDFTermFactoryTest {

	private RDFTermFactory factory;

	@Test
	public void createBlankNode() throws Exception {
		BlankNode bnode = factory.createBlankNode();
		String ntriplesString = bnode.ntriplesString();
		assertTrue("ntriples must start with _:",
				ntriplesString.startsWith("_:"));
		assertTrue("Internal identifier can't be empty", bnode
				.internalIdentifier().length() > 0);
		assertEquals("ntriples does not correspond with internal identifier",
				bnode.internalIdentifier(),
				ntriplesString.substring(2, ntriplesString.length()));

		BlankNode bnode2 = factory.createBlankNode();
		assertNotEquals(
				"Second blank node has not got a unique internal identifier",
				bnode.internalIdentifier(), bnode2.internalIdentifier());
	}

	@Test
	public void createBlankNodeIdentifier() throws Exception {
		BlankNode bnode = factory.createBlankNode("example1");
		assertEquals("example1", bnode.internalIdentifier());
		assertEquals("_:example1", bnode.ntriplesString());
	}

	public abstract RDFTermFactory createFactory();

	@Test
	public void createGraph() {
		Graph graph = factory.createGraph();
		assertEquals("Graph was not empty", 0, graph.size());
		graph.add(factory.createBlankNode(),
				factory.createIRI("http://example.com/"),
				factory.createBlankNode());

		Graph graph2 = factory.createGraph();
		assertNotSame(graph, graph2);
		assertEquals("Graph was empty after adding", 1, graph.size());
		assertEquals("New graph was not empty", 0, graph2.size());
	}

	@Test
	public void createIRI() throws Exception {
		IRI example = factory.createIRI("http://example.com/");
		assertEquals("http://example.com/", example.getIRIString());
		assertEquals("<http://example.com/>", example.ntriplesString());

		IRI term = factory.createIRI("http://example.com/vocab#term");
		assertEquals("http://example.com/vocab#term", term.getIRIString());
		assertEquals("<http://example.com/vocab#term>", term.ntriplesString());

		// Although relative IRIs are defined in 
		// http://www.w3.org/TR/rdf11-concepts/#section-IRIs
		// it is not a requirement for an implementation to support
		// it (all instances of an relative IRI should eventually
		// be possible to resolve to an absolute IRI)

		// Therefore the below is disabled as a required test:

		/*
		IRI relative = factory.createIRI("../relative");
		assertEquals("../relative", relative.getIRIString());
		assertEquals("<../relative>", relative.ntriplesString());

		IRI relativeTerm = factory.createIRI("../relative#term");
		assertEquals("../relative#term", relativeTerm.getIRIString());
		assertEquals("<../relative#term>", relativeTerm.ntriplesString());

		IRI emptyRelative = factory.createIRI(""); // <> equals the base URI
		assertEquals("", emptyRelative.getIRIString());
		assertEquals("<>", emptyRelative.ntriplesString());
		*/

		// and now for the international fun!

		IRI latin1 = factory.createIRI("http://accént.example.com/première");
		assertEquals("http://accént.example.com/première",
				latin1.getIRIString());
		assertEquals("<http://accént.example.com/première>",
				latin1.ntriplesString());

		IRI cyrillic = factory.createIRI("http://example.испытание/Кириллица");
		assertEquals("http://example.испытание/Кириллица",
				cyrillic.getIRIString());
		assertEquals("<http://example.испытание/Кириллица>",
				cyrillic.ntriplesString());

		IRI deseret = factory.createIRI("http://𐐀.example.com/𐐀");
		assertEquals("http://𐐀.example.com/𐐀", deseret.getIRIString());
		assertEquals("<http://𐐀.example.com/𐐀>", deseret.ntriplesString());
	}

	@Test
	public void createLiteral() throws Exception {
		Literal example = factory.createLiteral("Example");
		assertEquals("Example", example.getLexicalForm());
		assertFalse(example.getLanguageTag().isPresent());
		assertEquals("http://www.w3.org/2001/XMLSchema#string", example
				.getDatatype().getIRIString());
		// http://lists.w3.org/Archives/Public/public-rdf-comments/2014Dec/0004.html
		assertEquals("\"Example\"", example.ntriplesString());
	}

	@Test
	public void createLiteralDateTime() throws Exception {
		Literal dateTime = factory.createLiteral("2014-12-27T00:50:00T-0600",
				factory.createIRI("http://www.w3.org/2001/XMLSchema#dateTime"));
		assertEquals("2014-12-27T00:50:00T-0600", dateTime.getLexicalForm());
		assertFalse(dateTime.getLanguageTag().isPresent());
		assertEquals("http://www.w3.org/2001/XMLSchema#dateTime", dateTime
				.getDatatype().getIRIString());
		assertEquals(
				"\"2014-12-27T00:50:00T-0600\"^^<http://www.w3.org/2001/XMLSchema#dateTime>",
				dateTime.ntriplesString());
	}

	@Test
	public void createLiteralLang() throws Exception {
		Literal example = factory.createLiteral("Example", "en");
		assertEquals("Example", example.getLexicalForm());
		assertEquals("en", example.getLanguageTag().get());
		assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString",
				example.getDatatype().getIRIString());
		assertEquals("\"Example\"@en", example.ntriplesString());
	}

	@Test
	public void createLiteralString() throws Exception {
		Literal example = factory.createLiteral("Example",
				factory.createIRI("http://www.w3.org/2001/XMLSchema#string"));
		assertEquals("Example", example.getLexicalForm());
		assertFalse(example.getLanguageTag().isPresent());
		assertEquals("http://www.w3.org/2001/XMLSchema#string", example
				.getDatatype().getIRIString());
		// http://lists.w3.org/Archives/Public/public-rdf-comments/2014Dec/0004.html
		assertEquals("\"Example\"", example.ntriplesString());
	}

	@Test
	public void createTripleBnodeBnode() {
		BlankNode subject = factory.createBlankNode("b1");
		IRI predicate = factory.createIRI("http://example.com/pred");
		BlankNode object = factory.createBlankNode("b2");
		Triple triple = factory.createTriple(subject, predicate, object);

		// NOTE: We do not require object equivalence after insertion,
		// but the ntriples should match
		assertEquals(subject.ntriplesString(), triple.getSubject()
				.ntriplesString());
		assertEquals(predicate.ntriplesString(), triple.getPredicate()
				.ntriplesString());
		assertEquals(object.ntriplesString(), triple.getObject()
				.ntriplesString());

	}

	@Test
	public void createTripleBnodeIRI() {
		BlankNode subject = factory.createBlankNode("b1");
		IRI predicate = factory.createIRI("http://example.com/pred");
		IRI object = factory.createIRI("http://example.com/obj");
		Triple triple = factory.createTriple(subject, predicate, object);

		// NOTE: We do not require object equivalence after insertion,
		// but the ntriples should match
		assertEquals(subject.ntriplesString(), triple.getSubject()
				.ntriplesString());
		assertEquals(predicate.ntriplesString(), triple.getPredicate()
				.ntriplesString());
		assertEquals(object.ntriplesString(), triple.getObject()
				.ntriplesString());
	}

	@Test
	public void createTripleBnodeTriple() {
		BlankNode subject = factory.createBlankNode();
		IRI predicate = factory.createIRI("http://example.com/pred");
		Literal object = factory.createLiteral("Example", "en");
		Triple triple = factory.createTriple(subject, predicate, object);

		// NOTE: We do not require object equivalence after insertion,
		// but the ntriples should match
		assertEquals(subject.ntriplesString(), triple.getSubject()
				.ntriplesString());
		assertEquals(predicate.ntriplesString(), triple.getPredicate()
				.ntriplesString());
		assertEquals(object.ntriplesString(), triple.getObject()
				.ntriplesString());
	}

	@Before
	public void getFactory() {
		factory = createFactory();
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidBlankNode() throws Exception {
		factory.createBlankNode("with:colon");
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidIRI() throws Exception {
		factory.createIRI("<no_brackets>");
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidLiteralLang() throws Exception {
		factory.createLiteral("Example", "");
	}

	@Test(expected = Exception.class)
	public void invalidTriplePredicate() {
		BlankNode subject = factory.createBlankNode("b1");
		BlankNode predicate = factory.createBlankNode("b2");
		BlankNode object = factory.createBlankNode("b3");
		factory.createTriple(subject, (IRI) predicate, object);
	}
	
}
