package org.apache.commons.rdf;

import java.util.Optional;

/**
 * A Quad is an abstraction to specify that a <a href=
 * "http://www.w3.org/TR/rdf11-concepts/#dfn-rdf-triple" >RDF-1.1 Triple</a> is
 * in an <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-rdf-graph" >RDF-1.1
 * Graph</a>.
 */
public interface Quad {

    /**
     * The subject of this quad, which may be either a {@link BlankNode} or an
     * {@link IRI}, which are represented in Commons RDF by the interface
     * {@link BlankNodeOrIRI}.
     *
     * @return The subject {@link BlankNodeOrIRI} of this quad.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-subject">RDF-1.1
     * Triple subject</a>
     */
    BlankNodeOrIRI getSubject();

    /**
     * The predicate {@link IRI} of this quad.
     *
     * @return The predicate {@link IRI} of this quad.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-predicate">RDF-1.1
     * Triple predicate</a>
     */
    IRI getPredicate();

    /**
     * The object of this quad, which may be either a {@link BlankNode}, an
     * {@link IRI}, or a {@link Literal}, which are represented in Commons RDF
     * by the interface {@link RDFTerm}.
     *
     * @return The object {@link RDFTerm} of this quad.
     * @see <a href="http://www.w3.org/TR/rdf11-concepts/#dfn-object">RDF-1.1
     * Triple object</a>
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
     * of this quad if present, and the Default Graph if not present.
     * @see <a
     * href="http://www.w3.org/TR/rdf11-concepts/#dfn-named-graph">RDF-1.1
     * Named Graph</a>
     */
    Optional<BlankNodeOrIRI> getGraph();

    /**
     * A helper method used to convert to a {@link Triple}, removing any Graph
     * information that may be present.
     *
     * @return A {@link Triple} containing the Subject, Predicate, and Object
     * from this Quad.
     */
    Triple toTriple();

}
