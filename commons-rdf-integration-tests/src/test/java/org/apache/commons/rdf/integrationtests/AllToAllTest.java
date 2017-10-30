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
package org.apache.commons.rdf.integrationtests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.commons.rdf.jsonldjava.JsonLdRDF;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class AllToAllTest {

    private final RDF nodeFactory;
    private final RDF graphFactory;

    public AllToAllTest(final Class<? extends RDF> from, final Class<? extends RDF> to)
            throws InstantiationException, IllegalAccessException {
        this.nodeFactory = from.newInstance();
        this.graphFactory = to.newInstance();
    }

    @SuppressWarnings("rawtypes")
    @Parameters(name = "{index}: {0}->{1}")
    public static Collection<Object[]> data() {
        final List<Class> factories = Arrays.asList(SimpleRDF.class, JenaRDF.class, RDF4J.class, JsonLdRDF.class);
        final Collection<Object[]> allToAll = new ArrayList<>();
        for (final Class from : factories) {
            for (final Class to : factories) {
                // NOTE: we deliberately include self-to-self here
                // to test two instances of the same implementation
                allToAll.add(new Object[] { from, to });
            }
        }
        return allToAll;
    }

    /**
     * This test creates a {@link Graph} with the first {@link RDF},
     * then inserts/queries with triples using {@link RDFTerm}s created with the
     * second factory.
     *
     * @throws Exception
     *             Just in case..
     */
    @Test
    public void addTermsFromOtherFactory() throws Exception {
        try (final Graph g = graphFactory.createGraph()) {
            final BlankNode s = nodeFactory.createBlankNode();
            final IRI p = nodeFactory.createIRI("http://example.com/p");
            final Literal o = nodeFactory.createLiteral("Hello");

            g.add(s, p, o);

            // blankNode should still work with g.contains()
            assertTrue(g.contains(s, p, o));
            final Triple t1 = g.stream().findAny().get();

            // Can't make assumptions about BlankNode equality - it might
            // have been mapped to a different BlankNode.uniqueReference()
            // assertEquals(s, t.getSubject());

            assertEquals(p, t1.getPredicate());
            assertEquals(o, t1.getObject());

            final IRI s2 = nodeFactory.createIRI("http://example.com/s2");
            g.add(s2, p, s);
            assertTrue(g.contains(s2, p, s));

            // This should be mapped to the same BlankNode
            // (even if it has a different identifier), e.g.
            // we should be able to do:

            final Triple t2 = g.stream(s2, p, null).findAny().get();

            final BlankNode bnode = (BlankNode) t2.getObject();
            // And that (possibly adapted) BlankNode object should
            // match the subject of t1 statement
            assertEquals(bnode, t1.getSubject());
            // And can be used as a key:
            final Triple t3 = g.stream(bnode, p, null).findAny().get();
            assertEquals(t1, t3);
        }
    }

    /**
     * This is a variation of {@link #addTermsFromOtherFactory()}, but here
     * {@link Triple} is created in the "foreign" nodeFactory before adding to
     * the graph.
     *
     * @throws Exception
     *             Just in case..
     */
    @Test
    public void addTriplesFromOtherFactory() throws Exception {
        try (final Graph g = graphFactory.createGraph()) {
            final BlankNode s = nodeFactory.createBlankNode();
            final IRI p = nodeFactory.createIRI("http://example.com/p");
            final Literal o = nodeFactory.createLiteral("Hello");

            final Triple srcT1 = nodeFactory.createTriple(s, p, o);
            // This should work even with BlankNode as they are from the same
            // factory
            assertEquals(s, srcT1.getSubject());
            assertEquals(p, srcT1.getPredicate());
            assertEquals(o, srcT1.getObject());
            g.add(srcT1);

            // what about the blankNode within?
            assertTrue(g.contains(srcT1));
            final Triple t1 = g.stream().findAny().get();

            // Can't make assumptions about BlankNode equality - it might
            // have been mapped to a different BlankNode.uniqueReference()
            // assertEquals(srcT1, t1);
            // assertEquals(s, t1.getSubject());
            assertEquals(p, t1.getPredicate());
            assertEquals(o, t1.getObject());

            final IRI s2 = nodeFactory.createIRI("http://example.com/s2");
            final Triple srcT2 = nodeFactory.createTriple(s2, p, s);
            g.add(srcT2);
            assertTrue(g.contains(srcT2));

            // This should be mapped to the same BlankNode
            // (even if it has a different identifier), e.g.
            // we should be able to do:

            final Triple t2 = g.stream(s2, p, null).findAny().get();

            final BlankNode bnode = (BlankNode) t2.getObject();
            // And that (possibly adapted) BlankNode object should
            // match the subject of t1 statement
            assertEquals(bnode, t1.getSubject());
            // And can be used as a key:
            final Triple t3 = g.stream(bnode, p, null).findAny().get();
            assertEquals(t1, t3);
        }
    }
}
