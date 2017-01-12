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
package org.apache.commons.rdf.rdf4j;

import org.apache.commons.rdf.api.AbstractGraphTest;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDF;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.eclipse.rdf4j.sail.memory.model.MemValueFactory;

public class MemoryGraphTest extends AbstractGraphTest {

    public static final class MemoryStoreRDF implements RDF {

        RDF4J rdf4jFactory = new RDF4J(new MemValueFactory());

        @Override
        public RDF4JBlankNode createBlankNode() {
            return rdf4jFactory.createBlankNode();
        }

        @Override
        public RDF4JBlankNode createBlankNode(final String name) {
            return rdf4jFactory.createBlankNode(name);
        }

        @Override
        public Dataset createDataset() {
            return rdf4jFactory.createDataset();
        }

        @Override
        public RDF4JIRI createIRI(final String iri) throws IllegalArgumentException, UnsupportedOperationException {
            return rdf4jFactory.createIRI(iri);
        }

        @Override
        public RDF4JLiteral createLiteral(final String lexicalForm) {
            return rdf4jFactory.createLiteral(lexicalForm);
        }

        @Override
        public Literal createLiteral(final String lexicalForm, final IRI dataType) {
            return rdf4jFactory.createLiteral(lexicalForm, dataType);
        }

        @Override
        public Literal createLiteral(final String lexicalForm, final String languageTag) {
            return rdf4jFactory.createLiteral(lexicalForm, languageTag);
        }

        @Override
        public RDF4JTriple createTriple(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
            return rdf4jFactory.createTriple(subject, predicate, object);
        }

        @Override
        public Quad createQuad(final BlankNodeOrIRI graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
            return rdf4jFactory.createQuad(graphName, subject, predicate, object);
        }

        @Override
        public RDF4JGraph createGraph() {
            final Sail sail = new MemoryStore();
            final Repository repository = new SailRepository(sail);
            repository.initialize();
            return rdf4jFactory.asGraph(repository);
        }
    }

    @Override
    public RDF createFactory() {
        return new MemoryStoreRDF();
    }

}
