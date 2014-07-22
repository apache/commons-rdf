package org.apache.commons.rdf;

import java.util.stream.Stream;

/**
 * An <a href="http://www.w3.org/TR/rdf11-concepts/#section-rdf-graph"> RDF 1.1
 * Graph</a>, a set of RDF triples, as defined by <a
 * href="http://www.w3.org/TR/rdf11-concepts/" >RDF-1.1 Concepts and Abstract
 * Syntax</a>, a W3C Recommendation published on 25 February 2014.
 */
public interface Graph {

    /**
     * Add a triple to the graph.
     *
     * @param triple
     *            The triple to add
     */
    void add(Triple triple);

    /**
     * Add a triple to the graph.
     *
     * @param subject
     *            The triple subject
     * @param predicate
     *            The triple predicate
     * @param object
     *            The triple object
     */
    void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

    /**
     * Check if graph contains triple.
     *
     * @param triple
     *            The triple to check.
     * @return True if the Graph contains the given Triple.
     */
    boolean contains(Triple triple);

    /**
     * Check if graph contains a pattern of triples.
     *
     * @param subject
     *            The triple subject (null is a wildcard)
     * @param predicate
     *            The triple predicate (null is a wildcard)
     * @param object
     *            The triple object (null is a wildcard)
     * @return True if the Graph contains any Triples that match
     *            the given pattern.
     */
    boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

    /**
     * Remove a concrete triple from the graph.
     *
     * @param triple
     *            triple to remove
     */
    void remove(Triple triple);

    /**
     * Remove a concrete pattern of triples from the graph.
     *
     * @param subject
     *            The triple subject (null is a wildcard)
     * @param predicate
     *            The triple predicate (null is a wildcard)
     * @param object
     *            The triple object (null is a wildcard)
     */
    void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

    /**
     * Clear the graph.
     */
    void clear();

    /**
     * Number of triples contained by the graph.
     *
     * @return The size of the graph.
     */
    long size();

    /**
     * Get all triples contained by the graph.<br>
     *
     * The behaviour of the Stream is not specified if add, remove, or clear,
     * are called on the Stream before it terminates.<br>
     *
     * Implementations may throw ConcurrentModificationException from Stream
     * methods if they detect a conflict while the Stream is active.
     *
     * @return A {@link Stream} over all of the triples in the graph.
     */
    Stream<? extends Triple> getTriples();

    /**
     * Get all triples contained by the graph matched with the pattern.
     *
     * The behaviour of the Stream is not specified if add, remove, or clear,
     * are called on the Stream before it terminates.<br>
     *
     * Implementations may throw ConcurrentModificationException from Stream
     * methods if they detect a conflict while the Stream is active.
     *
     * @param subject
     *            The triple subject (null is a wildcard)
     * @param predicate
     *            The triple predicate (null is a wildcard)
     * @param object
     *            The triple object (null is a wildcard)
     * @return A {@link Stream} over the matched triples.
     */
    Stream<? extends Triple> getTriples(BlankNodeOrIRI subject, IRI predicate, RDFTerm object);

}
