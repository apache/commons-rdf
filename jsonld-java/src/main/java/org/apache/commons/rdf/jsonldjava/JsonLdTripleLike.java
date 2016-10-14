package org.apache.commons.rdf.jsonldjava;

import org.apache.commons.rdf.api.TripleLike;

public interface JsonLdTripleLike extends TripleLike {

	/**
	 * Return the underlying JsonLD {@link com.github.jsonldjava.core.RDFDataset.Quad}
	 * 
	 * @return The JsonLD {@link com.github.jsonldjava.core.RDFDataset.Quad}
	 */
	public com.github.jsonldjava.core.RDFDataset.Quad asJsonLdQuad();

}
