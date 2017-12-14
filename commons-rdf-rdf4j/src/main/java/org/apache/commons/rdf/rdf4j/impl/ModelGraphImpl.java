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
package org.apache.commons.rdf.rdf4j.impl;

import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.rdf4j.ClosableIterable;
import org.apache.commons.rdf.rdf4j.RDF4JBlankNodeOrIRI;
import org.apache.commons.rdf.rdf4j.RDF4JGraph;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.apache.commons.rdf.rdf4j.RDF4JTriple;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.Repository;

@SuppressWarnings("PMD.UnnecessaryFullyQualifiedName") // we use fully-qualified names for clarity
final class ModelGraphImpl implements RDF4JGraph {

    private final Model model;
    private final RDF4J rdf4jTermFactory;

    ModelGraphImpl(final Model model, final RDF4J rdf4jTermFactory) {
        this.model = model;
        this.rdf4jTermFactory = rdf4jTermFactory;
    }

    @Override
    public void add(final BlankNodeOrIRI subject, final org.apache.commons.rdf.api.IRI predicate, final RDFTerm object) {
        model.add((Resource) rdf4jTermFactory.asValue(subject),
                (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate), rdf4jTermFactory.asValue(object));
    }

    @Override
    public void add(final Triple triple) {
        model.add(rdf4jTermFactory.asStatement(triple));
    }

    @Override
    public Optional<Model> asModel() {
        return Optional.of(model);
    }

    @Override
    public Optional<Repository> asRepository() {
        return Optional.empty();
    }

    @Override
    public void clear() {
        model.clear();
    }

    @Override
    public boolean contains(final BlankNodeOrIRI subject, final org.apache.commons.rdf.api.IRI predicate, final RDFTerm object) {
        return model.contains((Resource) rdf4jTermFactory.asValue(subject),
                (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate), rdf4jTermFactory.asValue(object));
    }

    @Override
    public boolean contains(final Triple triple) {
        return model.contains(rdf4jTermFactory.asStatement(triple));
    }

    @Override
    public void remove(final BlankNodeOrIRI subject, final org.apache.commons.rdf.api.IRI predicate, final RDFTerm object) {
        model.remove((Resource) rdf4jTermFactory.asValue(subject),
                (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate), rdf4jTermFactory.asValue(object));
    }

    @Override
    public void remove(final Triple triple) {
        model.remove(rdf4jTermFactory.asStatement(triple));
    }

    @Override
    public long size() {
        final int size = model.size();
        if (size < Integer.MAX_VALUE) {
            return size;
        }
        // TODO: Check if this can really happen with RDF4J models
        // Collection.size() can't help us, we'll have to count
        return model.parallelStream().count();
    }

    @Override
    public Stream<RDF4JTriple> stream() {
        return model.parallelStream().map(rdf4jTermFactory::asTriple);
    }

    @Override
    public Stream<RDF4JTriple> stream(final BlankNodeOrIRI subject, final org.apache.commons.rdf.api.IRI predicate,
            final RDFTerm object) {
        return model.filter((Resource) rdf4jTermFactory.asValue(subject),
                (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate), rdf4jTermFactory.asValue(object))
                .parallelStream().map(rdf4jTermFactory::asTriple);
    }

    @Override
    public Set<RDF4JBlankNodeOrIRI> getContextMask() {
        // ModelGraph always do the unionGraph
        return Collections.emptySet();
        // TODO: Should we support contextMask like in RepositoryGraphImpl?
    }

    @Override
    public ClosableIterable<Triple> iterate(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return new ClosableIterable<Triple>() {
            @SuppressWarnings("unchecked")
            @Override
            public Iterator<Triple> iterator() {
                // double-cast to fight Java generics..
                final Stream<? extends Triple> s = stream(subject, predicate, object);
                return (Iterator<Triple>) s.iterator();
            }

            @Override
            public void close() throws Exception {
                // no-op as Model don't have transaction
            }
        };
    }

    @Override
    public ClosableIterable<Triple> iterate() throws ConcurrentModificationException, IllegalStateException {
        return iterate(null, null, null);
    }

}