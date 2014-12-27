package com.github.commonsrdf.api;

/**
 * Factory for creating RDFTerm and Graph instances.
 * <p>
 * If an implementation does not support a particular method (e.g. because it
 * needs more parameters), then it may throw UnsupportedOperationException.
 * 
 * @see RDFTerm
 * @see Graph
 * 
 */
public interface RDFTermFactory {
	public BlankNode createBlankNode() throws UnsupportedOperationException;

	public BlankNode createBlankNode(String identifier)
			throws UnsupportedOperationException;

	public Graph createGraph() throws UnsupportedOperationException;

	public IRI createIRI(String iri) throws UnsupportedOperationException;

	public Literal createLiteral(String literal)
			throws UnsupportedOperationException;

	public Literal createLiteral(String literal, IRI dataType)
			throws UnsupportedOperationException;

	public Literal createLiteral(String literal, String language)
			throws UnsupportedOperationException;

	public Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object) throws UnsupportedOperationException;

}
