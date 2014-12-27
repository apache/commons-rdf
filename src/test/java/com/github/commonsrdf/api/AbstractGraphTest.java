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
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractGraphTest {

	private RDFTermFactory factory;
	private Graph graph;
	private IRI alice;
	private IRI bob;
	private IRI name;
	private IRI knows;
	private IRI member;
	private BlankNode org1;
	private BlankNode org2;

	public abstract RDFTermFactory createFactory();

	@Before
	public void createGraphAndAdd() {
		factory = createFactory();
		graph = factory.createGraph();
		assertEquals(0, graph.size());

		alice = factory.createIRI("http://example.com/alice");
		bob = factory.createIRI("http://example.com/bob");
		name = factory.createIRI("http://xmlns.com/foaf/0.1/name");
		knows = factory.createIRI("http://xmlns.com/foaf/0.1/knows");
		member = factory.createIRI("http://xmlns.com/foaf/0.1/member");
		org1 = factory.createBlankNode("org1");
		org2 = factory.createBlankNode("org2");

		graph.add(alice, name, factory.createLiteral("Alice"));
		graph.add(alice, knows, bob);
		graph.add(alice, member, org1);
		// and for Bob we'll try as Triples
		graph.add(factory.createTriple(bob, name,
				factory.createLiteral("Bob", "en-US")));
		graph.add(factory.createTriple(bob, member, org1));
		graph.add(factory.createTriple(bob, member, org2));

		graph.add(org1, name, factory.createLiteral("The Secret Club"));
		graph.add(org1, name, factory.createLiteral("A company"));
	}

	@Test
	public void graphToString() {

		System.out.println(graph);
		assertTrue(graph
				.toString()
				.startsWith(
						"<http://example.com/alice> <http://xmlns.com/foaf/0.1/name> \"Alice\" ."));
		assertTrue(graph.toString().endsWith(
				"_:org1 <http://xmlns.com/foaf/0.1/name> \"A company\" ."));

	}

	@Test
	public void size() throws Exception {
		assertEquals(8, graph.size());
	}

	@Test
	public void contains() throws Exception {
		assertFalse(graph.contains(bob, knows, alice)); // or so he claims..

		assertTrue(graph.contains(alice, knows, bob));

		Triple triple = factory.createTriple(alice, knows, bob);
		// FIXME: Should not this be true?
		// assertTrue(graph.contains(triple));

		Triple existingTriple = graph.getTriples().skip(4).findFirst().get();
		assertTrue(graph.contains(existingTriple));

		Triple nonExistingTriple = factory.createTriple(bob, knows, alice);
		assertFalse(graph.contains(nonExistingTriple));
	}

	@Test
	public void remove() throws Exception {
		graph.remove(alice, knows, bob);
		assertEquals(7, graph.size());
		graph.remove(alice, knows, bob);
		assertEquals(7, graph.size());
		graph.add(alice, knows, bob);
		graph.add(alice, knows, bob);
		graph.add(alice, knows, bob);
		// Undetermined size at this point
		// assertEquals(8, graph.size());
		// but at least after remove they should all be gone
		graph.remove(alice, knows, bob);
		assertEquals(7, graph.size());

		Triple otherTriple = graph.getTriples().findAny().get();
		graph.remove(otherTriple);
		assertEquals(6, graph.size());
		graph.remove(otherTriple);
		assertEquals(6, graph.size());
		graph.add(otherTriple);
		assertEquals(7, graph.size());
	}

	@Test
	public void clear() throws Exception {
		graph.clear();
		assertFalse(graph.contains(alice, knows, bob));
		assertEquals(0, graph.size());
		graph.clear(); // no-op
		assertEquals(0, graph.size());
	}

	@Test
	public void getTriples() throws Exception {
		assertEquals(8, graph.getTriples().count());
		assertTrue(graph.getTriples().allMatch(t -> graph.contains(t)));
	}

	@Test
	public void getTriplesQuery() throws Exception {
		assertEquals(3, graph.getTriples(alice, null, null).count());
		assertEquals(4, graph.getTriples(null, name, null).count());
		assertEquals(2, graph.getTriples(null, member, org1).count());
	}

	/**
	 * An attempt to use the Java 8 streams to look up a more complicated query.
	 * 
	 * FYI, the equivalent SPARQL version (untested):
	 * 
	 * <pre>
	 * 	SELECT ?orgName WHERE { 
	 * 			?org foaf:name ?orgName .
	 * 			?alice foaf:member ?org .
	 * 			?bob foaf:member ?org .
	 * 			?alice foaf:knows ?bob .
	 * 		  FILTER NOT EXIST { ?bob foaf:knows ?alice }
	 * 	}
	 * </pre>
	 * 
	 * @throws Exception
	 */
	@Test
	public void whyJavaStreamsMightNotTakeOverFromSparql() throws Exception {

		// Find a secret organizations
		assertEquals(
				"\"The Secret Club\"",
				graph.getTriples(null, knows, null)
						// Find One-way "knows"
						.filter(t -> !graph.contains(
								(BlankNodeOrIRI) t.getObject(), knows,
								t.getSubject()))
						.map(knowsTriple -> graph
								// and those they know, what are they member of?
								.getTriples(
										(BlankNodeOrIRI) knowsTriple
												.getObject(), member, null)
								// keep those which first-guy is a member of
								.filter(memberTriple -> graph.contains(
										knowsTriple.getSubject(), member,
										// First hit is good enough
										memberTriple.getObject())).findFirst()
								.get().getObject())
						// then look up the name of that org
						.map(org -> graph
								.getTriples((BlankNodeOrIRI) org, name, null)
								.findFirst().get().getObject().ntriplesString())
						.findFirst().get());

	}
}
