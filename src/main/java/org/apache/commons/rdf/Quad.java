package org.apache.commons.rdf;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * A Quad is an abstraction aimed at targeting an <a href=
 * "http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#dfn-rdf-triple"
 * >RDF-1.1 Triple</a> to a specific <a
 * href="http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#dfn-rdf-graph"
 * >RDF-1.1 Graph</a> without using a {@link Set} or {@link Map}.
 */
public interface Quad {

	/**
	 * The subject of this quad, which may be either a {@link BlankNode} or an
	 * {@link IRI}, which are represented in Commons RDF by the interface
	 * {@link BlankNodeOrIRI}.
	 * 
	 * @return The subject {@link BlankNodeOrIRI} of this quad.
	 * @see <a
	 *      href="http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#dfn-subject">RDF-1.1
	 *      Triple subject</a>
	 */
	BlankNodeOrIRI getSubject();

	/**
	 * The predicate {@link IRI} of this quad.
	 * 
	 * @return The predicate {@link IRI} of this quad.
	 * @see <a
	 *      href="http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#dfn-predicate">RDF-1.1
	 *      Triple predicate</a>
	 */
	IRI getPredicate();

	/**
	 * The object of this quad, which may be either a {@link BlankNode} an
	 * {@link IRI}, or a {@link Literal}, which are represented in Commons RDF
	 * by the interface {@link RDFTerm}.
	 * 
	 * @return The object {@link RDFTerm} of this quad.
	 * @see <a
	 *      href="http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#dfn-object">RDF-1.1
	 *      Triple object</a>
	 */
	RDFTerm getObject();

	/**
	 * The optional named graph of this quad, which may be either a
	 * {@link BlankNode} or an {@link IRI}, which are represented in Commons RDF
	 * by the interface {@link BlankNodeOrIRI}.<br>
	 * If the {@link Optional#isPresent()} method on the result returns
	 * <code>false</code>, this Quad is defined to be in the <a
	 * href="dfn-default-graph">default graph</a>
	 * 
	 * @return The optional {@link BlankNodeOrIRI} representing the Named Graph
	 *         of this quad if present, and the Default Graph if not present.
	 * @see <a
	 *      href="http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#dfn-named-graph">RDF-1.1
	 *      Named Graph</a>
	 */
	Optional<BlankNodeOrIRI> getGraph();

	/**
	 * A helper method used to convert to a {@link Triple}, removing any Graph
	 * information that may be present.
	 * 
	 * @return A {@link Triple} containing the Subject, Predicate, and Object
	 *         from this Quad.
	 **/
	Triple toTriple();
}
