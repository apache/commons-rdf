package org.apache.commons.rdf.jena.impl;

import org.apache.commons.rdf.jena.JenaRDFTerm;
import org.apache.commons.rdf.jena.JenaVariable;
import org.apache.jena.graph.Node;

public class VariableImpl implements JenaRDFTerm, JenaVariable {

	private Node node;

	VariableImpl(Node node) {	
		if (! node.isVariable()) {
			throw new IllegalArgumentException("Node is not a variable: " + node);
		}
		this.node = node;
	}

	@Override
	public String ntriplesString() {
		return "?" + getVariableName();
	}

	@Override
	public String getVariableName() {
		return node.getName();
	}

	@Override
	public Node asJenaNode() {
		return node;
	}

}
