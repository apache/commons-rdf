package com.github.commonsrdf.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public abstract class AbstractCommonsRDFTest {

	private RDFTermFactory factory;

	@Before
	public void getFactory() {
		factory = createFactory();
	}

	public abstract RDFTermFactory createFactory();

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
}
