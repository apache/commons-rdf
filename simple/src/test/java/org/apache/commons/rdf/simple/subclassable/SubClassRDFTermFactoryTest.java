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
package org.apache.commons.rdf.simple.subclassable;

import org.apache.commons.rdf.api.AbstractRDFTermFactoryTest;
import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.simple.BlankNodeImpl;
import org.apache.commons.rdf.simple.GraphImpl;
import org.apache.commons.rdf.simple.IRIImpl;
import org.apache.commons.rdf.simple.LiteralImpl;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;
import org.apache.commons.rdf.simple.TripleImpl;

/**
 * Test that all classes in simple can be extended by subclasses (in a different
 * package).
 *
 */
public class SubClassRDFTermFactoryTest extends AbstractRDFTermFactoryTest {

    @Override
    public RDFTermFactory createFactory() {
        return new SubClassRDFTermFactory();
    }

    private class SubClassRDFTermFactory extends SimpleRDFTermFactory {
        @Override
        public BlankNode createBlankNode() {
            return new BlankNodeImpl() {
            };
        }

        @Override
        public BlankNode createBlankNode(String identifier)
                throws UnsupportedOperationException {
            return new BlankNodeImpl(this, identifier) {
            };
        }

        @Override
        public Graph createGraph() throws UnsupportedOperationException {
            return new GraphImpl(this) {
            };
        }

        @Override
        public Literal createLiteral(String literal) {
            return new LiteralImpl(literal) {
            };
        }

        @Override
        public Literal createLiteral(String lexicalForm, IRI dataType) {
            return new LiteralImpl(lexicalForm, dataType) {
            };
        }

        @Override
        public Literal createLiteral(String literal, String language) {
            return new LiteralImpl(literal, language) {
            };
        }

        @Override
        public IRI createIRI(String iri) {
            return new IRIImpl(iri) {
            };
        }

        @Override
        public Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
                RDFTerm object) {
            return new TripleImpl(subject, predicate, object) {
            };
        }
    }
}
