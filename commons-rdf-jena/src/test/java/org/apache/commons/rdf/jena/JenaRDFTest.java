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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.apache.commons.rdf.api.AbstractRDFTest;
import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.junit.Test;

public class JenaRDFTest extends AbstractRDFTest {

    @Override
    public JenaRDF createFactory() {
        return new JenaRDF();
    }

    @Test
    public void roundTripDatasetGraphShouldMaintainIdentity() {
        final DatasetGraph dsg = DatasetGraphFactory.create();
        final JenaDataset dataset = createFactory().asDataset(dsg);
        final DatasetGraph roundTrippedDSG = createFactory().asJenaDatasetGraph(dataset);
        assertSame("Should have gotten the same DatasetGraph object from a round trip!", dsg, roundTrippedDSG);
    }

    @Test
    public void testSimpleDatasetConversion() {
        final SimpleRDF factory = new SimpleRDF();
        final Dataset ds = factory.createDataset();
        final BlankNode subject = factory.createBlankNode("b1");
        final IRI predicate = factory.createIRI("http://example.com/pred");
        final IRI object = factory.createIRI("http://example.com/obj");
        final IRI graph = factory.createIRI("http://example.com/graph");
        final Quad quad = factory.createQuad(graph, subject, predicate, object);
        ds.add(quad);
        final JenaRDF jenaFactory = createFactory();
        final org.apache.jena.query.Dataset jenaDS = jenaFactory.asJenaDataset(ds);
        assertEquals("Should have found one named graph!", 1, jenaDS.asDatasetGraph().size());
        final Model namedModel = jenaDS.getNamedModel(graph.getIRIString());
        assertEquals("Should have found one triple in named graph!", 1, namedModel.size());
        final Statement statement = namedModel.listStatements().next();
        final Resource jenaSubject = statement.getSubject();
        final Property jenaPredicate = statement.getPredicate();
        final RDFNode jenaObject = statement.getObject();
        assertTrue(jenaSubject.isAnon());
        assertTrue(jenaObject.isResource());
        assertEquals(subject.ntriplesString(), "_:" + jenaSubject.getId().getLabelString());
        assertEquals(predicate.getIRIString(), jenaPredicate.getURI());
        assertEquals(object.getIRIString(), jenaObject.asResource().getURI());
    }

}
