package org.apache.commons.rdf;

/**
 * An <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-iri"
 * >RDF-1.1 IRI</a>, as defined by <a href=
 * "http://www.w3.org/TR/rdf11-concepts/" >RDF-1.1 Concepts
 * and Abstract Syntax</a>, a W3C Recommendation published on 25 February 2014.<br>
 */
public interface IRI extends BlankNodeOrIRI {

	/**
	 * Returns the IRI encoded as a native Unicode String.<br>
	 * 
	 * The returned string must not include URL-encoding to escape 
	 * non-ASCII characters.
	 * 
	 * @return The IRI encoded as a native Unicode String.
	 */
    String getIRIString();
}
