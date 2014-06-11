package org.apache.commons.rdf;

/**
 * An <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-rdf-triple" >RDF-1.1
 * Triple</a>, as defined by <a href= "http://www.w3.org/TR/rdf11-concepts/"
 * >RDF-1.1 Concepts and Abstract Syntax</a>, a W3C Recommendation published on
 * 25 February 2014.<br>
 * 
 * @see <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-rdf-triple" >RDF-1.1
 *      Triple</a>
 */
public interface Triple {

	/**
	 * The subject of this triple, which may be either a {@link BlankNode} or an
	 * {@link IRI}, which are represented in Commons RDF by the interface
	 * {@link BlankNodeOrIRI}.
	 * 
	 * @return The subject {@link BlankNodeOrIRI} of this triple.
	 * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-subject">RDF-1.1
	 *      Triple subject</a>
	 */
	BlankNodeOrIRI getSubject();

	/**
	 * The predicate {@link IRI} of this triple.
	 * 
	 * @return The predicate {@link IRI} of this triple.
	 * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-predicate">RDF-1.1
	 *      Triple predicate</a>
	 */
	IRI getPredicate();

	/**
	 * The object of this triple, which may be either a {@link BlankNode}, an
	 * {@link IRI}, or a {@link Literal}, which are represented in Commons RDF
	 * by the interface {@link RDFTerm}.
	 * 
	 * @return The object {@link RDFTerm} of this triple.
	 * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-object">RDF-1.1
	 *      Triple object</a>
	 */
	RDFTerm getObject();
}
