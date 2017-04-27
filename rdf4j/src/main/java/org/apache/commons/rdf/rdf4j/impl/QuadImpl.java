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

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.apache.commons.rdf.rdf4j.RDF4JQuad;
import org.eclipse.rdf4j.model.Statement;

final class QuadImpl implements RDF4JQuad {
    private transient int hashCode = 0;
    private final UUID salt;
    private final Statement statement;

    QuadImpl(final Statement statement, final UUID salt) {
        this.statement = statement;
        this.salt = salt;
    }

    @Override
    public Statement asStatement() {
        return statement;
    }

    @Override
    public Triple asTriple() {
        return new TripleImpl(statement, salt);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Quad) {
            final Quad quad = (Quad) obj;
            return getGraphName().equals(quad.getGraphName()) &&
                    getSubject().equals(quad.getSubject()) && 
                    getPredicate().equals(quad.getPredicate()) &&
                    getObject().equals(quad.getObject());
        }
        return false;
    }

    @Override
    public Optional<BlankNodeOrIRI> getGraphName() {
        if (statement.getContext() == null) {
            return Optional.empty();
        }
        final BlankNodeOrIRI g = (BlankNodeOrIRI) RDF4J.asRDFTerm(statement.getContext(), salt);
        return Optional.of(g);
    }

    @Override
    public RDFTerm getObject() {
        return RDF4J.asRDFTerm(statement.getObject(), salt);
    }

    @Override
    public org.apache.commons.rdf.api.IRI getPredicate() {
        return (org.apache.commons.rdf.api.IRI) RDF4J.asRDFTerm(statement.getPredicate(), null);
    }

    @Override
    public BlankNodeOrIRI getSubject() {
        return (BlankNodeOrIRI) RDF4J.asRDFTerm(statement.getSubject(), salt);
    }

    @Override
    public int hashCode() {
        if (hashCode != 0) {
            return hashCode;
        }
        hashCode = Objects.hash(getSubject(), getPredicate(), getObject(), getGraphName());
        return hashCode;
    }

    @Override
    public String toString() {
        return statement.toString();
    }
}