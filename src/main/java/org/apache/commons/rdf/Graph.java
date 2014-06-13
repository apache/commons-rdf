package org.apache.commons.rdf;

import java.util.Collection;
import java.util.Iterator;

/**
 * An <a href="http://www.w3.org/TR/rdf11-concepts/#section-rdf-graph">
 * RDF 1.1 Graph</a>, a set of RDF triples, as defined by <a
 * href="http://www.w3.org/TR/rdf11-concepts/" >RDF-1.1 Concepts
 * and Abstract Syntax</a>, a W3C Recommendation published on 25
 * February 2014.
 */
public interface Graph {

    /**
     * Add a triple to the graph
     *
     * @param triple triple to add
     * @return added or not
     */
    boolean add(Triple triple);

    /**
     * Add a triple to the graph
     *
     * @param subject subject of the triple
     * @param predicate predicate of the triple
     * @param object object of the property
     * @return added or not
     */
    boolean add(Resource subject, IRI predicate, RDFTerm object);

    /**
     * Add a collection of triples to the graph
     *
     * @param triples triples to add
     * @return added all or not
     */
    boolean add(Collection<? extends Triple> triples);

    /**
     * Remove a concrete triple from the graph
     *
     * @param triple triple to remove
     * @return removed or not
     */
    boolean remove(Triple triple);

    /**
     * emove a concrete pattern of triples from the graph
     *
     * @param subject subject (null is a wildcard)
     * @param predicate  predicate (null is a wildcard)
     * @param object object (null is a wildcard)
     * @return remoed or not
     */
    boolean remove(Resource subject, IRI predicate, RDFTerm object);

    /**
     * Clear the graph
     *
     * @return clearer or not
     */
    boolean clear();

    /**
     * Number of triples contained by the graph
     *
     * @return size
     */
    long size();

    /**
     * Get all triples contained by the graph
     *
     * @return iterator over all triples
     */
    Iterator<? extends Triple> getTriples();

    /**
     * Get all triples contained by the graph matched with the pattern
     *
     * @param subject subject (null is a wildcard)
     * @param predicate  predicate (null is a wildcard)
     * @param object object (null is a wildcard)
     * @return iterator over the matched triples
     */
    Iterator<? extends Triple> getTriples(Resource subject, IRI predicate, RDFTerm object);

}
