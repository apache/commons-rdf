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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.junit.jupiter.api.Test;

class RDF4JMethodOverloadsTest {

    @Test
    void testAsRDFTermOverloads() {
        final RDF4J rdf4J = new RDF4J();

        final ValueFactory valueFactory = SimpleValueFactory.getInstance();

        final Value bNode = valueFactory.createBNode("b1");
        final Value iri = valueFactory.createIRI("http://ex.org");
        final Value literal = valueFactory.createLiteral("b1");

        assertEquals(rdf4J.asRDFTerm(bNode), rdf4J.asRDFTerm((BNode) bNode));
        assertEquals(rdf4J.asRDFTerm(iri), rdf4J.asRDFTerm((IRI) iri));
        assertEquals(rdf4J.asRDFTerm(literal), rdf4J.asRDFTerm((Literal) literal));
        assertEquals(rdf4J.asRDFTerm(bNode), rdf4J.asRDFTerm((Resource) bNode));
        assertEquals(rdf4J.asRDFTerm(iri), rdf4J.asRDFTerm((Resource) iri));
    }
}
