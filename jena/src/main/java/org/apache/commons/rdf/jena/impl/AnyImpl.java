package org.apache.commons.rdf.jena.impl;

import org.apache.commons.rdf.jena.JenaAny;
import org.apache.commons.rdf.jena.JenaRDFTerm;
import org.apache.jena.graph.Node;

public class AnyImpl implements JenaRDFTerm, JenaAny {

	static class Singleton {
		static AnyImpl instance = new AnyImpl();
	}
	
	/**
	 * Private constructor
	 * 
	 * @see {@link Singleton#instance}
	 */
	private AnyImpl() {
	}
	
	@Override
	public String ntriplesString() {
		return "[]";
	}

	@Override
	public Node asJenaNode() {
		return Node.ANY;
	}

}
