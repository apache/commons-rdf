package org.apache.commons.rdf.jsonldjava;

import com.github.jsonldjava.core.RDFDataset.Node;

public abstract class JsonLdTerm {
	final Node node;
	JsonLdTerm(Node node) {
		this.node = node;
	}
	public Node asNode() {
		return node;
	}
	
}
