package org.apache.commons.rdf;

import java.util.Collection;
import java.util.Iterator;

/**
 * Model, a collection of RDF triples
 *
 */
public interface Model {

    boolean add(Triple triple);

    boolean add(Resource subject, IRI predicate, RDFTerm object);

    boolean add(Collection<? extends Triple> triples);

    boolean remove(Triple triple);

    boolean remove(Resource subject, IRI predicate, RDFTerm object);

    boolean clear();

    long size();

    Iterator<? extends Triple> getTriples();

    Iterator<? extends Triple> filter(Resource subject, IRI predicate, RDFTerm object);

}
