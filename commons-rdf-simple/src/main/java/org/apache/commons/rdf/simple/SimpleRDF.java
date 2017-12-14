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

import java.util.UUID;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.Triple;

/**
 * Simple RDF implementation.
 * <p>
 * The {@link RDFTerm}, {@link Triple}, {@link Quad}, {@link Graph} and
 * {@link Dataset} instances created by SimpleRDF are simple in-memory
 * Implementations that are not thread-safe or efficient, but which may be
 * useful for testing and prototyping purposes.
 */
public class SimpleRDF implements RDF {

    /**
     * Marker interface to say that this RDFTerm is part of the Simple
     * implementation. Used by {@link GraphImpl} to avoid double remapping.
     * <p>
     * This method is package protected to avoid any third-party subclasses.
     *
     */
    interface SimpleRDFTerm extends RDFTerm {
    }

    /**
     * Unique salt per instance, for {@link #createBlankNode(String)}
     */
    private final UUID SALT = UUID.randomUUID();

    @Override
    public BlankNode createBlankNode() {
        return new BlankNodeImpl();
    }

    @Override
    public BlankNode createBlankNode(final String name) {
        return new BlankNodeImpl(SALT, name);
    }

    @Override
    public Graph createGraph() {
        // Creates a GraphImpl object using this object as the factory for
        // delegating all object creation to
        return new GraphImpl(this);
    }

    @Override
    public Dataset createDataset() throws UnsupportedOperationException {
        return new DatasetImpl(this);
    }

    @Override
    public IRI createIRI(final String iri) {
        final IRI result = new IRIImpl(iri);
        // Reuse any IRI objects already created in Types
        return Types.get(result).orElse(result);
    }

    @Override
    public Literal createLiteral(final String literal) {
        return new LiteralImpl(literal);
    }

    @Override
    public Literal createLiteral(final String literal, final IRI dataType) {
        return new LiteralImpl(literal, dataType);
    }

    @Override
    public Literal createLiteral(final String literal, final String language) {
        return new LiteralImpl(literal, language);
    }

    @Override
    public Triple createTriple(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return new TripleImpl(subject, predicate, object);
    }

    @Override
    public Quad createQuad(final BlankNodeOrIRI graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object)
            throws IllegalArgumentException {
        return new QuadImpl(graphName, subject, predicate, object);
    }
}
