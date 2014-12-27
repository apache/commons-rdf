package com.github.commonsrdf.api;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

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
	private Literal aliceLit;
	private Literal bobLit;

	@Before
	public void getFactory() {
		factory = createFactory();
	}

	public abstract RDFTermFactory createFactory();

	@Before
	public void createGraph() {
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
		graph.add(factory.createTriple(bob, name, factory.createLiteral("Bob", "en-US"))); 
		graph.add(factory.createTriple(bob, member, org1));
		graph.add(factory.createTriple(bob, member, org2));
		
		graph.add(org1, name, factory.createLiteral("The Secret Club"));
		graph.add(org1, name, factory.createLiteral("A company"));
	}
	
	
	@Test
	public void graphToString() {
		
		System.out.println(graph);
		assertTrue(graph.toString().startsWith(
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
		assertTrue(graph.contains(alice, knows, bob));
		assertFalse(graph.contains(bob, knows, alice)); // or so he claims..
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
		//assertEquals(8, graph.size());
		// but at least after remove they should all be gone
		graph.remove(alice, knows, bob);
		assertEquals(7, graph.size());
	}
	
	@Test
	public void something() throws Exception {
	}		
	}
	
	
}
