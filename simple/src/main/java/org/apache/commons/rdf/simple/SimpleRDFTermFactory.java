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

/**
 * A simple implementation of RDFTermFactory.
 * <p>
 * The {@link RDFTerm} and {@link Graph} instances created by this factory are
 * simple in-memory Implementations that are not thread-safe or efficient, but
 * which may be useful for testing and prototyping purposes.
 */
public class SimpleRDFTermFactory implements RDFTermFactory {

    @Override
    public BlankNode createBlankNode() {
        return new BlankNodeImpl();
    }

    @Override
    public BlankNode createBlankNode(String identifier) {
        // Creates a BlankNodeImpl object using this object as the salt
        return new BlankNodeImpl(this, identifier);
    }

    @Override
    public Graph createGraph() {
        // Creates a GraphImpl object using this object as the factory for
        // delegating all object creation to
        return new GraphImpl(this);
    }

    @Override
    public IRI createIRI(String iri) {
        IRI result = new IRIImpl(iri);
        // Reuse any IRI objects already created in Types
        return Types.get(result).orElse(result);
    }

    @Override
    public Literal createLiteral(String literal) {
        return new LiteralImpl(literal);
    }

    @Override
    public Literal createLiteral(String literal, IRI dataType) {
        return new LiteralImpl(literal, dataType);
    }

    @Override
    public Literal createLiteral(String literal, String language) {
        return new LiteralImpl(literal, language);
    }

    @Override
    public Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                               RDFTerm object) {
        return new TripleImpl(subject, predicate, object);
    }
}
