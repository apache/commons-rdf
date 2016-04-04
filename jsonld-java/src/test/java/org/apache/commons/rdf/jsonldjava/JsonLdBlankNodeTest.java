package org.apache.commons.rdf.jsonldjava;

import java.util.UUID;

import org.apache.commons.rdf.api.AbstractBlankNodeTest;
import org.apache.commons.rdf.api.BlankNode;

import com.github.jsonldjava.core.RDFDataset;

public class JsonLdBlankNodeTest extends AbstractBlankNodeTest {

	String fixedPrefix = "urn:uuid:d028ca89-8b2f-4e18-90a0-8959f955038d#";
	
	@Override
	protected BlankNode getBlankNode() {
		return getBlankNode(UUID.randomUUID().toString());
	}

	@Override
	protected BlankNode getBlankNode(String identifier) {
		return new JsonLdBlankNode(new RDFDataset.BlankNode("_:" + identifier), fixedPrefix);
	}

}
