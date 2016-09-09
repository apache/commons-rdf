package org.apache.commons.rdf.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaRDFTermFactory;
import org.apache.commons.rdf.jsonldjava.JsonLdRDFTermFactory;
import org.apache.commons.rdf.rdf4j.RDF4JTermFactory;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;
import org.junit.Test;

public class AllToAllTest {

	private RDFTermFactory simpleFactory = new SimpleRDFTermFactory();
	private RDFTermFactory jenaFactory = new JenaRDFTermFactory();
	private RDFTermFactory rdf4jFactory = new RDF4JTermFactory();
	private RDFTermFactory jsonldFactory = new JsonLdRDFTermFactory();

	@Test
	public void jenaToRdf4j() throws Exception {
		nodesIntoOther(jenaFactory, rdf4jFactory);
	}
	@Test
	public void jenaToJsonLd() throws Exception {
		nodesIntoOther(jenaFactory, jsonldFactory);
	}
	@Test
	public void jenaToSimple() throws Exception {
		nodesIntoOther(jenaFactory, simpleFactory);
	}
	
	@Test
	public void rdf4jToJena() throws Exception {
		nodesIntoOther(rdf4jFactory, jenaFactory);
	}
	@Test
	public void rdf4jToJsonLd() throws Exception {
		nodesIntoOther(rdf4jFactory, jsonldFactory);
	}	
	@Test
	public void rdf4jToSimple() throws Exception {
		nodesIntoOther(rdf4jFactory, simpleFactory);
	}
	
	@Test
	public void simpletoJena() throws Exception {
		nodesIntoOther(simpleFactory, jenaFactory);
	}
	@Test
	public void simpleToJsonLd() throws Exception {
		nodesIntoOther(simpleFactory, jsonldFactory);
	}
	@Test
	public void simpleToRdf4j() throws Exception {
		nodesIntoOther(simpleFactory, rdf4jFactory);
	}
	
	public void nodesIntoOther(RDFTermFactory fromFactory, RDFTermFactory toFactory) throws Exception {
		Graph g = fromFactory.createGraph();
		BlankNode s = toFactory.createBlankNode();
		IRI p = toFactory.createIRI("http://example.com/p");
		Literal o = toFactory.createLiteral("Hello");

		g.add(s, p, o);

		// blankNode should still work with g.contains()
		assertTrue(g.contains(s, p, o));
		Triple t1 = g.stream().findAny().get();

		// Can't make assumptions about mappegetPredicated BlankNode equality
		// assertEquals(s, t.getSubject());
		assertEquals(p, t1.getPredicate());
		assertEquals(o, t1.getObject());

		IRI s2 = toFactory.createIRI("http://example.com/s2");
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
}
