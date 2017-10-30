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
import java.util.stream.Stream;

class DummyGraph implements Graph {

    boolean streamCalled = false;
    boolean filteredStreamCalled;

    @Override
    public void add(final Triple triple) {
        if (! contains(triple)) {
            throw new IllegalStateException("DummyGraph can't be modified");
        }
    }
    @Override
    public void add(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        if (! contains(subject, predicate, object)) {
            throw new IllegalStateException("DummyGraph can't be modified");
        }
    }
    @Override
    public boolean contains(final Triple triple) {
        return triple.equals(new DummyTriple());
    }
    @Override
    public boolean contains(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return (subject == null || subject.equals(new DummyIRI(1))) &&
                (predicate == null || predicate.equals(new DummyIRI(2))) &&
                (object == null || object.equals(new DummyIRI(3)));
    }
    @Override
    public void remove(final Triple triple) {
        if (contains(triple)) {
            throw new IllegalStateException("DummyGraph can't be modified");
        }
    }
    @Override
    public void remove(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        if (contains(subject, predicate, object)) {
            throw new IllegalStateException("DummyGraph can't be modified");
        }
    }
    @Override
    public void clear() {
        throw new IllegalStateException("DummyGraph can't be modified");
    }
    @Override
    public long size() {
        return 1;
    }
    @Override
    public Stream<? extends Triple> stream() {
        streamCalled = true;
        return Arrays.asList(new DummyTriple()).stream();
    }

    @Override
    public Stream<? extends Triple> stream(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        filteredStreamCalled = true;
        if (contains(subject, predicate, object)) {
            return Stream.of(new DummyTriple());
        }
        return Stream.empty();
    }
}