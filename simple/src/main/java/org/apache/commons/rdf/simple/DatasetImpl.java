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

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.simple.SimpleRDF.SimpleRDFTerm;

/**
 * A simple, memory-based implementation of Dataset.
 * <p>
 * {@link Quad}s in the graph are kept in a {@link Set}.
 * <p>
 * All Stream operations are performed using parallel and unordered directives.
 */
final class DatasetImpl implements Dataset {

    private static final int TO_STRING_MAX = 10;
    private final Set<Quad> quads = new HashSet<>();
    private final SimpleRDF factory;

    DatasetImpl(final SimpleRDF simpleRDF) {
        this.factory = simpleRDF;
    }

    @Override
    public void add(final BlankNodeOrIRI graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        final BlankNodeOrIRI newGraphName = (BlankNodeOrIRI) internallyMap(graphName);
        final BlankNodeOrIRI newSubject = (BlankNodeOrIRI) internallyMap(subject);
        final IRI newPredicate = (IRI) internallyMap(predicate);
        final RDFTerm newObject = internallyMap(object);
        final Quad result = factory.createQuad(newGraphName, newSubject, newPredicate, newObject);
        quads.add(result);
    }

    @Override
    public void add(final Quad quad) {
        final BlankNodeOrIRI newGraph = (BlankNodeOrIRI) internallyMap(quad.getGraphName().orElse(null));
        final BlankNodeOrIRI newSubject = (BlankNodeOrIRI) internallyMap(quad.getSubject());
        final IRI newPredicate = (IRI) internallyMap(quad.getPredicate());
        final RDFTerm newObject = internallyMap(quad.getObject());
        // Check if any of the object references changed during the mapping, to
        // avoid creating a new Quad object if possible
        if (newGraph == quad.getGraphName().orElse(null) && newSubject == quad.getSubject()
                && newPredicate == quad.getPredicate() && newObject == quad.getObject()) {
            quads.add(quad);
        } else {
            // Make a new Quad with our mapped instances
            final Quad result = factory.createQuad(newGraph, newSubject, newPredicate, newObject);
            quads.add(result);
        }
    }

    private <T extends RDFTerm> RDFTerm internallyMap(final T object) {
        if (object == null || object instanceof SimpleRDFTerm) {
            return object;
        }
        if (object instanceof BlankNode && !(object instanceof BlankNodeImpl)) {
            final BlankNode blankNode = (BlankNode) object;
            // This guarantees that adding the same BlankNode multiple times to
            // this graph will generate a local object that is mapped to an
            // equivalent object, based on the code in the package private
            // BlankNodeImpl class
            return factory.createBlankNode(blankNode.uniqueReference());
        } else if (object instanceof IRI && !(object instanceof IRIImpl)) {
            final IRI iri = (IRI) object;
            return factory.createIRI(iri.getIRIString());
        } else if (object instanceof Literal && !(object instanceof LiteralImpl)) {
            final Literal literal = (Literal) object;
            if (literal.getLanguageTag().isPresent()) {
                return factory.createLiteral(literal.getLexicalForm(), literal.getLanguageTag().get());
            } else {
                return factory.createLiteral(literal.getLexicalForm(), (IRI) internallyMap(literal.getDatatype()));
            }
        } else {
            throw new IllegalArgumentException("Not a BlankNode, IRI or Literal: " + object);
        }
    }

    @Override
    public void clear() {
        quads.clear();
    }

    @Override
    public boolean contains(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return stream(graphName, subject, predicate, object).findAny().isPresent();
    }

    @Override
    public boolean contains(final Quad quad) {
        return quads.contains(Objects.requireNonNull(quad));
    }

    @Override
    public Stream<Quad> stream() {
        return quads.parallelStream().unordered();
    }

    @Override
    public Stream<Quad> stream(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate,
            final RDFTerm object) {
        final Optional<BlankNodeOrIRI> newGraphName;
        if (graphName == null) { 
            // Avoid Optional<Optional<BlankNodeOrIRI>> ...
            newGraphName = null;
        } else {
            newGraphName = graphName.map(g -> (BlankNodeOrIRI) internallyMap(g));
        }
        final BlankNodeOrIRI newSubject = (BlankNodeOrIRI) internallyMap(subject);
        final IRI newPredicate = (IRI) internallyMap(predicate);
        final RDFTerm newObject = internallyMap(object);

        return getQuads(t -> {
            if (newGraphName != null && !t.getGraphName().equals(newGraphName)) {
                // This would check Optional.empty() == Optional.empty()
                return false;
            }
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

    private Stream<Quad> getQuads(final Predicate<Quad> filter) {
        return stream().filter(filter);
    }

    @Override
    public void remove(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        final Stream<Quad> toRemove = stream(graphName, subject, predicate, object);
        for (final Quad t : toRemove.collect(Collectors.toList())) {
            // Avoid ConcurrentModificationException in ArrayList
            remove(t);
        }
    }

    @Override
    public void remove(final Quad quad) {
        quads.remove(Objects.requireNonNull(quad));
    }

    @Override
    public long size() {
        return quads.size();
    }

    @Override
    public String toString() {
        final String s = stream().limit(TO_STRING_MAX).map(Object::toString).collect(Collectors.joining("\n"));
        if (size() > TO_STRING_MAX) {
            return s + "\n# ... +" + (size() - TO_STRING_MAX) + " more";
        } else {
            return s;
        }
    }

    @Override
    public Graph getGraph() {
        return getGraph(null).get();
    }

    @Override
    public Optional<Graph> getGraph(final BlankNodeOrIRI graphName) {
        return Optional.of(new DatasetGraphView(this, graphName));
    }

    @Override
    public Stream<BlankNodeOrIRI> getGraphNames() {
        // Not very efficient..
        return stream().map(Quad::getGraphName).filter(Optional::isPresent).map(Optional::get).distinct();
    }

}
