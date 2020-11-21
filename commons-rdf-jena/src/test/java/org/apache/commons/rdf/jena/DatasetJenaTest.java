/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
package org.apache.commons.rdf.jena;

import static org.junit.Assert.assertEquals;

import org.apache.commons.rdf.api.AbstractDatasetTest;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.simple.Types;
import org.junit.Test;

public class DatasetJenaTest extends AbstractDatasetTest {

    @Override
    public RDF createFactory() {
        return new JenaRDF();
    }

    @Test
    public void datasetImplToStringTest() {
        final RDF rdf = createFactory();
        final JenaDataset jena = (JenaDataset) rdf.createDataset();
        final IRI graph = rdf.createIRI("http://example.com/");
        final IRI s = rdf.createIRI("http://example.com/s");
        final IRI p = rdf.createIRI("http://example.com/p");
        final Literal literal123 = rdf.createLiteral("123", Types.XSD_INTEGER);
        jena.add(graph, s, p, literal123);
        final String out = jena.toString();
        assertEquals("<http://example.com/s> <http://example.com/p> \"123\"^^<http://www"
                + ".w3.org/2001/XMLSchema#integer> <http://example.com/> .\n", out);
        assertEquals(10L, dataset.size());
    }
}
