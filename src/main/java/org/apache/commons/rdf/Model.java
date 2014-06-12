package org.apache.commons.rdf;

import java.util.Collection;
import java.util.Iterator;

/**
 * Model, a collection of RDF triples
 *
 */
public interface Model {

    /**
     * Add a triple to the model
     *
     * @param triple triple to add
     * @return added or not
     */
    boolean add(Triple triple);

    /**
     * Add a triple to the model
     *
     * @param subject subject of the triple
     * @param predicate predicate of the triple
     * @param object object of the property
     * @return added or not
     */
    boolean add(Resource subject, IRI predicate, RDFTerm object);

    /**
     * Add a collection of triples to the model
     *
     * @param triples triples to add
     * @return added all or not
     */
    boolean add(Collection<? extends Triple> triples);

    /**
     * Remove a concrete triple from the model
     *
     * @param triple triple to remove
     * @return removed or not
     */
    boolean remove(Triple triple);

    /**
     * emove a concrete pattern of triples from the model
     *
     * @param subject subject (null is a wildcard)
     * @param predicate  predicate (null is a wildcard)
     * @param object object (null is a wildcard)
     * @return remoed or not
     */
    boolean remove(Resource subject, IRI predicate, RDFTerm object);

    /**
     * Clear the model
     *
     * @return clearer or not
     */
    boolean clear();

    /**
     * Number of triples contained by the model
     *
     * @return size
     */
    long size();

    /**
     * Get all triples contained by the model
     *
     * @return iterator over all triples
     */
    Iterator<? extends Triple> getTriples();

    /**
     * Get all triples contained by the model matched with the pattern
     *
     * @param subject subject (null is a wildcard)
     * @param predicate  predicate (null is a wildcard)
     * @param object object (null is a wildcard)
     * @return iterator over the matched triples
     */
    Iterator<? extends Triple> getTriples(Resource subject, IRI predicate, RDFTerm object);

}
