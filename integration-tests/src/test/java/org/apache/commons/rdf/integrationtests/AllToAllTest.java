package org.apache.commons.rdf.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaRDFTermFactory;
import org.apache.commons.rdf.jsonldjava.JsonLdRDFTermFactory;
import org.apache.commons.rdf.rdf4j.RDF4JTermFactory;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AllToAllTest {

	private RDFTermFactory nodeFactory;
	private RDFTermFactory graphFactory;


	public AllToAllTest(
			Class<? extends RDFTermFactory> from, 
			Class<? extends RDFTermFactory> to) throws InstantiationException, IllegalAccessException {
		this.nodeFactory = from.newInstance();
		this.graphFactory = to.newInstance();		
	}
	
	@SuppressWarnings("rawtypes")
	@Parameters(name = "{index}: {0}->{1}")
	public static Collection<Object[]> data() { 
		List<Class> factories = Arrays.asList(
						SimpleRDFTermFactory.class, 
						JenaRDFTermFactory.class, 
						RDF4JTermFactory.class,
						JsonLdRDFTermFactory.class);
		Collection<Object[]>  allToAll = new ArrayList<>();
		for (Class from : factories) {
			for (Class to : factories) {
				// NOTE: we deliberately include self-to-self here
				// to test two instances of the same implementation
				allToAll.add(new Object[]{from, to});
			}
		}
		return allToAll;
	}
	
		/**
	 * This test creates a {@link Graph} with the first
	 * {@link RDFTermFactory}, then inserts/queries with
	 * triples using {@link RDFTerm}s created with the second factory.
	 * 
	 * @param nodeFactory Factory to create {@link RDFTerm} instances
	 * @param graphFactory Factory to create {@link Graph}
	 * @throws Exception
	 */
	@Test
	public void addTermsFromOtherFactory() throws Exception {
		Graph g = graphFactory.createGraph();
		BlankNode s = nodeFactory.createBlankNode();
		IRI p = nodeFactory.createIRI("http://example.com/p");
		Literal o = nodeFactory.createLiteral("Hello");

		g.add(s, p, o);

		// blankNode should still work with g.contains()
		assertTrue(g.contains(s, p, o));
		Triple t1 = g.stream().findAny().get();

		// Can't make assumptions about BlankNode equality - it might
		// have been mapped to a different BlankNode.uniqueReference()
		// assertEquals(s, t.getSubject());
		
		assertEquals(p, t1.getPredicate());
		assertEquals(o, t1.getObject());

		IRI s2 = nodeFactory.createIRI("http://example.com/s2");
		g.add(s2, p, s);
		assertTrue(g.contains(s2, p, s));

		// This should be mapped to the same BlankNode
		// (even if it has a different identifier), e.g.
		// we should be able to do:

		Triple t2 = g.stream(s2, p, null).findAny().get();

		BlankNode bnode = (BlankNode) t2.getObject();
		// And that (possibly adapted) BlankNode object should
		// match the subject of t1 statement
		assertEquals(bnode, t1.getSubject());
		// And can be used as a key:
		Triple t3 = g.stream(bnode, p, null).findAny().get();
		assertEquals(t1, t3);		
	}
	

	/**
	 * This is a variation of {@link #addTermsFromOtherFactory(RDFTermFactory, RDFTermFactory)}, 
	 * but here {@link Triple} is created in the "foreign" nodeFactory before adding to the graph.
	 * 
	 * @param nodeFactory Factory to create {@link RDFTerm} and {@link Triple}s
	 * @param graphFactory Factory to create {@link Graph}
	 * @throws Exception
	 */
	@Test
	public void addTriplesFromOtherFactory() throws Exception {
		Graph g = graphFactory.createGraph();
		BlankNode s = nodeFactory.createBlankNode();
		IRI p = nodeFactory.createIRI("http://example.com/p");
		Literal o = nodeFactory.createLiteral("Hello");
		
		Triple srcT1 = nodeFactory.createTriple(s, p, o);
		// This should work even with BlankNode as they are from the same factory
		assertEquals(s, srcT1.getSubject());
		assertEquals(p, srcT1.getPredicate());
		assertEquals(o, srcT1.getObject());
		g.add(srcT1);
		
		// what about the blankNode within?
		assertTrue(g.contains(srcT1));
		Triple t1 = g.stream().findAny().get();

		// Can't make assumptions about BlankNode equality - it might
		// have been mapped to a different BlankNode.uniqueReference()
		//assertEquals(srcT1, t1);
		//assertEquals(s, t1.getSubject());
		assertEquals(p, t1.getPredicate());
		assertEquals(o, t1.getObject());

		IRI s2 = nodeFactory.createIRI("http://example.com/s2");
		Triple srcT2 = nodeFactory.createTriple(s2, p, s);
		g.add(srcT2);
		assertTrue(g.contains(srcT2));

		// This should be mapped to the same BlankNode
		// (even if it has a different identifier), e.g.
		// we should be able to do:

		Triple t2 = g.stream(s2, p, null).findAny().get();

		BlankNode bnode = (BlankNode) t2.getObject();
		// And that (possibly adapted) BlankNode object should
		// match the subject of t1 statement
		assertEquals(bnode, t1.getSubject());
		// And can be used as a key:
		Triple t3 = g.stream(bnode, p, null).findAny().get();
		assertEquals(t1, t3);		
	}	
}
