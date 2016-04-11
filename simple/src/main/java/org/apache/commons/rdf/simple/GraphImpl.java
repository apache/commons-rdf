/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rdf.simple;

import org.apache.commons.rdf.api.*;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory.SimpleRDFTerm;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A simple, memory-based implementation of Graph.
 * <p>
 * {@link Triple}s in the graph are kept in a {@link Set}.
 * <p>
 * All Stream operations are performed using parallel and unordered directives.
 */
final class GraphImpl implements Graph {

    private static final int TO_STRING_MAX = 10;
    private final Set<Triple> triples = new HashSet<Triple>();
    private final SimpleRDFTermFactory factory;

    GraphImpl(SimpleRDFTermFactory simpleRDFTermFactory) {
        this.factory = simpleRDFTermFactory;
    }

    @Override
    public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        BlankNodeOrIRI newSubject = (BlankNodeOrIRI) internallyMap(subject);
        IRI newPredicate = (IRI) internallyMap(predicate);
        RDFTerm newObject = internallyMap(object);
        Triple result = factory.createTriple(newSubject, newPredicate, newObject);
        triples.add(result);
    }

    @Override
    public void add(Triple triple) {
        BlankNodeOrIRI newSubject = (BlankNodeOrIRI) internallyMap(triple
                .getSubject());
        IRI newPredicate = (IRI) internallyMap(triple.getPredicate());
        RDFTerm newObject = internallyMap(triple.getObject());
        // Check if any of the object references changed during the mapping, to
        // avoid creating a new Triple object if possible
        if (newSubject == triple.getSubject()
                && newPredicate == triple.getPredicate()
                && newObject == triple.getObject()) {
            triples.add(triple);
        } else {
            Triple result = factory.createTriple(newSubject, newPredicate,
                    newObject);
            triples.add(result);
        }
    }

    private <T extends RDFTerm> RDFTerm internallyMap(T object) {
    	if (object == null || object instanceof SimpleRDFTerm) {
    		// No need to re-map our own objects.
    		// We support null as internallyMap() is also used by the filters, and the
    		// factory constructors later do null checks
    		return object;
    	}
        if (object instanceof BlankNode) {
            BlankNode blankNode = (BlankNode) object;
            // This guarantees that adding the same BlankNode multiple times to
            // this graph will generate a local object that is mapped to an
            // equivalent object, based on the code in the package private
            // BlankNodeImpl class
            return factory.createBlankNode(blankNode.uniqueReference());
        } else if (object instanceof IRI) {
            IRI iri = (IRI) object;
            return factory.createIRI(iri.getIRIString());
        } else if (object instanceof Literal) {
            Literal literal = (Literal) object;
            if (literal.getLanguageTag().isPresent()) {
                return factory.createLiteral(literal.getLexicalForm(), literal
                        .getLanguageTag().get());
            } else {
                return factory.createLiteral(literal.getLexicalForm(),
                        (IRI) internallyMap(literal.getDatatype()));
            }
        } else {
        	throw new IllegalArgumentException("RDFTerm was neither a BlankNode, IRI nor Literal: " + object);
        }
    }

    @Override
    public void clear() {
        triples.clear();
    }

    @Override
    public boolean contains(BlankNodeOrIRI subject, IRI predicate,
                            RDFTerm object) {
        return getTriples(subject, predicate, object).findFirst().isPresent();
    }

    @Override
    public boolean contains(Triple triple) {
        return triples.contains(Objects.requireNonNull(triple));
    }

    @Override
    public Stream<Triple> getTriples() {
        return triples.parallelStream().unordered();
    }

    @Override
    public Stream<Triple> getTriples(final BlankNodeOrIRI subject,
                                     final IRI predicate, final RDFTerm object) {
        final BlankNodeOrIRI newSubject = (BlankNodeOrIRI) internallyMap(subject);
        final IRI newPredicate = (IRI) internallyMap(predicate);
        final RDFTerm newObject = internallyMap(object);

        return getTriples(t -> {
            // Lacking the requirement for .equals() we have to be silly
            // and test ntriples string equivalance
            if (subject != null && !t.getSubject().equals(newSubject)) {
                return false;
            }
            if (predicate != null && !t.getPredicate().equals(newPredicate)) {
                return false;
            }
            if (object != null && !t.getObject().equals(newObject)) {
                return false;
            }
            return true;
        });
    }

    private Stream<Triple> getTriples(final Predicate<Triple> filter) {
        return getTriples().filter(filter);
    }

    @Override
    public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        Stream<Triple> toRemove = getTriples(subject, predicate, object);
        for (Triple t : toRemove.collect(Collectors.toList())) {
            // Avoid ConcurrentModificationException in ArrayList
            remove(t);
        }
    }

    @Override
    public void remove(Triple triple) {
        triples.remove(Objects.requireNonNull(triple));
    }

    @Override
    public long size() {
        return triples.size();
    }

    @Override
    public String toString() {
        String s = getTriples().limit(TO_STRING_MAX).map(Object::toString)
                .collect(Collectors.joining("\n"));
        if (size() > TO_STRING_MAX) {
            return s + "\n# ... +" + (size() - TO_STRING_MAX) + " more";
        } else {
            return s;
        }
    }

}
