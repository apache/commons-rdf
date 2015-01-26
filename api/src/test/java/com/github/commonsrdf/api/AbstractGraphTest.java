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

import java.util.Optional;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Graph implementation
 * <p>
 * To add to your implementation's tests, create a subclass with a name ending
 * in <code>Test</code> and provide {@link #createFactory()} which minimally
 * must support {@link RDFTermFactory#createGraph()} and
 * {@link RDFTermFactory#createIRI(String)}, but ideally support all operations.
 *
 * @see Graph
 * @see RDFTermFactory
 * @see com.github.commonsrdf.simple.SimpleGraphTest
 */
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
	private Literal aliceName;
	private Literal bobName;
	private Literal secretClubName;
	private Literal companyName;
	private Triple bobNameTriple;

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
		try {
			org1 = factory.createBlankNode("org1");
			org2 = factory.createBlankNode("org2");
		} catch (UnsupportedOperationException ex) {
			// leave as null
		}

		try {
			secretClubName = factory.createLiteral("The Secret Club");
			companyName = factory.createLiteral("A company");
			aliceName = factory.createLiteral("Alice");
			bobName = factory.createLiteral("Bob", "en-US");
		} catch (UnsupportedOperationException ex) {
			// leave as null
		}

		if (aliceName != null) {
			graph.add(alice, name, aliceName);
		}
		graph.add(alice, knows, bob);

		if (org1 != null) {
			graph.add(alice, member, org1);
		}

		if (bobName != null) {
			try {
				bobNameTriple = factory.createTriple(bob, name, bobName);
			} catch (UnsupportedOperationException ex) {
				// leave as null
			}
			if (bobNameTriple != null) {
				graph.add(bobNameTriple);
			}
		}
		if (org1 != null) {
			graph.add(factory.createTriple(bob, member, org1));
			graph.add(factory.createTriple(bob, member, org2));
			if (secretClubName != null) {
				graph.add(org1, name, secretClubName);
				graph.add(org2, name, companyName);
			}
		}
	}

	@Test
	public void graphToString() {
		Assume.assumeNotNull(aliceName, companyName);
		System.out.println(graph);
		assertTrue(graph
				.toString()
				.startsWith(
						"<http://example.com/alice> <http://xmlns.com/foaf/0.1/name> \"Alice\" ."));
		assertTrue(graph.toString().endsWith(
				"_:org2 <http://xmlns.com/foaf/0.1/name> \"A company\" ."));

	}

	@Test
	public void size() throws Exception {
		assertTrue(graph.size() > 0);
		Assume.assumeNotNull(org1, org2, aliceName, bobName, secretClubName,
				companyName, bobNameTriple);
		// Can only reliably predict size if we could create all triples
		assertEquals(8, graph.size());
	}

	@Test
	public void contains() throws Exception {
		assertFalse(graph.contains(bob, knows, alice)); // or so he claims..

		assertTrue(graph.contains(alice, knows, bob));

		Optional<? extends Triple> first = graph.getTriples().skip(4)
				.findFirst();
		Assume.assumeTrue(first.isPresent());
		Triple existingTriple = first.get();
		assertTrue(graph.contains(existingTriple));

		Triple nonExistingTriple = factory.createTriple(bob, knows, alice);
		assertFalse(graph.contains(nonExistingTriple));

		Triple triple = null;
		try {
			triple = factory.createTriple(alice, knows, bob);
		} catch (UnsupportedOperationException ex) {
		}
		if (triple != null) {
			// FIXME: Should not this always be true?
			// assertTrue(graph.contains(triple));
		}
	}

	@Test
	public void remove() throws Exception {
		long fullSize = graph.size();
		graph.remove(alice, knows, bob);
		long shrunkSize = graph.size();
		assertEquals(1, fullSize - shrunkSize);

		graph.remove(alice, knows, bob);
		assertEquals(shrunkSize, graph.size()); // unchanged

		graph.add(alice, knows, bob);
		graph.add(alice, knows, bob);
		graph.add(alice, knows, bob);
		// Undetermined size at this point -- but at least it
		// should be bigger
		assertTrue(graph.size() > shrunkSize);

		// and after a single remove they should all be gone
		graph.remove(alice, knows, bob);
		assertEquals(shrunkSize, graph.size());

		Optional<? extends Triple> anyTriple = graph.getTriples().findAny();
		Assume.assumeTrue(anyTriple.isPresent());

		Triple otherTriple = anyTriple.get();
		graph.remove(otherTriple);
		assertEquals(shrunkSize - 1, graph.size());
		graph.remove(otherTriple);
		assertEquals(shrunkSize - 1, graph.size()); // no change
		graph.add(otherTriple);
		assertEquals(shrunkSize, graph.size());
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

		long tripleCount = graph.getTriples().count();
		assertTrue(tripleCount > 0);
		assertTrue(graph.getTriples().allMatch(t -> graph.contains(t)));
		// Check exact count
		Assume.assumeNotNull(org1, org2, aliceName, bobName, secretClubName,
				companyName, bobNameTriple);
		assertEquals(8, tripleCount);
	}

	@Test
	public void getTriplesQuery() throws Exception {

		long aliceCount = graph.getTriples(alice, null, null).count();
		assertTrue(aliceCount > 0);
		Assume.assumeNotNull(aliceName);
		assertEquals(3, aliceCount);

		Assume.assumeNotNull(org1, org2, bobName, companyName, secretClubName);
		assertEquals(4, graph.getTriples(null, name, null).count());
		Assume.assumeNotNull(org1);
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
		Assume.assumeNotNull(org1, org2, secretClubName);
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
