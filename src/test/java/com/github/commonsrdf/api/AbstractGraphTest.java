package com.github.commonsrdf.api;

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
		alice = factory.createIRI("http://example.com/alice");
		bob = factory.createIRI("http://example.com/bob");
		name = factory.createIRI("http://xmlns.com/foaf/0.1/name");
		knows = factory.createIRI("http://xmlns.com/foaf/0.1/knows");
		member = factory.createIRI("http://xmlns.com/foaf/0.1/member");
		org1 = factory.createBlankNode("org1");
		org2 = factory.createBlankNode("org2");
		aliceLit = factory.createLiteral("Alice");
		bobLit = factory.createLiteral("Bob", "en-US");
		
		graph.add(alice, name, aliceLit);
		graph.add(alice, knows, bob);
		graph.add(alice, member, org1);
		graph.add(bob, name, bobLit);
		graph.add(bob, knows, alice);
		graph.add(bob, member, org1);
		graph.add(bob, member, org2);
		System.out.println(graph);
	}
	
	@Test
	public void testName() throws Exception {
		
		
	}
	
	
}
