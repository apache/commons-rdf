package org.apache.commons.rdf;

import java.util.Collection;

/**
 * In-memory Model
 *
 */
public interface Model {

    boolean add(Triple triple);

    boolean add(Resource subject, IRI predicate, RDFTerm object);

    boolean add(Collection<? extends Triple> triples);

    boolean remove(Triple triple);

    boolean clear();

    int size();

    Collection<? extends Triple> getTriples();

    Collection<? extends Resource> getSubjects();

    Collection<? extends IRI> getPredicates();

    Collection<? extends RDFTerm> getObjects();

    Collection<? extends Triple> filter(Resource subject, IRI predicate, RDFTerm object);

}
