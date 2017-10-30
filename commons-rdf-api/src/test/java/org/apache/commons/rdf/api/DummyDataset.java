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
package org.apache.commons.rdf.api;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

class DummyDataset implements Dataset {

    boolean streamCalled = false;
    boolean filteredStreamCalled;

    @Override
    public void add(final Quad Quad) {
        if (! contains(Quad)) {
            throw new IllegalStateException("DummyDataset can't be modified");
        }
    }

    @Override
    public void add(final BlankNodeOrIRI graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        if (! contains(Optional.ofNullable(graphName), subject, predicate, object)) {
            throw new IllegalStateException("DummyDataset can't be modified");
        }
    }

    @Override
    public boolean contains(final Quad Quad) {
        return Quad.equals(new DummyQuad());
    }

    @Override
    public boolean contains(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return (graphName == null || ! graphName.isPresent()) &&
                (subject == null || subject.equals(new DummyIRI(1))) &&
                (predicate == null || predicate.equals(new DummyIRI(2))) &&
                (object == null || object.equals(new DummyIRI(3)));
    }
    @Override
    public void remove(final Quad Quad) {
        if (contains(Quad)) {
            throw new IllegalStateException("DummyDataset can't be modified");
        }
    }
    @Override
    public void remove(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        if (contains(graphName, subject, predicate, object)) {
            throw new IllegalStateException("DummyDataset can't be modified");
        }
    }
    @Override
    public void clear() {
        throw new IllegalStateException("DummyDataset can't be modified");
    }
    @Override
    public long size() {
        return 1;
    }
    @Override
    public Stream<? extends Quad> stream() {
        streamCalled = true;
        return Arrays.asList(new DummyQuad()).stream();
    }

    @Override
    public Stream<? extends Quad> stream(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate,
            final RDFTerm object) {
        filteredStreamCalled = true;
        if (contains(graphName, subject, predicate, object)) {
            return Stream.of(new DummyQuad());
        }
        return Stream.empty();
    }

    @Override
    public Graph getGraph() {
        return new DummyGraph();
    }

    @Override
    public Optional<Graph> getGraph(final BlankNodeOrIRI graphName) {
        if (graphName == null) {
            return Optional.of(getGraph());
        }
        return Optional.empty();
    }

    @Override
    public Stream<BlankNodeOrIRI> getGraphNames() {
        return Stream.empty();
    }
}