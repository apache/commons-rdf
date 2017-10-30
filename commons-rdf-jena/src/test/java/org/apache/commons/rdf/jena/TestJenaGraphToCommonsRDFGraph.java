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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.simple.Types;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.sparql.graph.GraphFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Adapt a Jena Graph after parsing data into it */
public class TestJenaGraphToCommonsRDFGraph {
    private static final boolean DEBUG = false;
    private Path turtleFile;

    @Before
    public void preparePath() throws IOException {
        turtleFile = Files.createTempFile("commonsrdf", "test.ttl");
        Files.copy(getClass().getResourceAsStream("/D.ttl"), turtleFile, StandardCopyOption.REPLACE_EXISTING);
    }

    @After
    public void deletePath() throws IOException {
        if (turtleFile != null) {
            Files.deleteIfExists(turtleFile);
        }
    }

    @Test
    public void jenaToCommonsRDF() throws Exception {
        final org.apache.jena.graph.Graph jGraph = GraphFactory.createGraphMem();
        RDFDataMgr.read(jGraph, turtleFile.toUri().toString());

        final JenaRDF factory = new JenaRDF();

        // "graph" is a CommonsRDF graph
        try (final Graph graph = factory.asGraph(jGraph)) {

            // The below check expected statements from D.ttl

            final JenaIRI p = factory.createIRI("http://example.com/p");
            final JenaIRI s = factory.createIRI("http://example.com/s");
            final JenaLiteral literal123 = factory.createLiteral("123", Types.XSD_INTEGER);
            assertTrue(graph.contains(s, p, literal123));

            final JenaIRI p1 = factory.createIRI("http://example.com/p1");
            // Let's look up the BlankNode
            final BlankNodeOrIRI bnode1 = graph.stream(null, p1, null).findFirst().map(Triple::getSubject).get();
            assertTrue(bnode1 instanceof BlankNode);

            // Verify we can use BlankNode in query again
            final RDFTerm obj = graph.stream(bnode1, p1, null).findFirst().map(Triple::getObject).get();

            // Let's look up also that nested blank node
            assertTrue(obj instanceof BlankNode);
            final BlankNode bnode2 = (BlankNode) obj;

            final JenaIRI q = factory.createIRI("http://example.com/q");
            final JenaLiteral literalR = factory.createLiteral("r", "en");
            assertTrue(graph.contains(bnode2, q, literalR));

            // Can we add the same triple again as s/p/o
            // without affecting graph size?
            // Just to be evil we add a blanknode-iri-blanknode statement
            assertEquals(3, graph.size());
            graph.add(bnode1, p1, bnode2);
            assertEquals(3, graph.size());

            // Add the same Triple again
            graph.stream(bnode2, null, null).findFirst().ifPresent(graph::add);
            assertEquals(3, graph.size());

            // Add to CommonsRDF Graph
            final JenaIRI s2 = factory.createIRI("http://example/s2");
            final JenaIRI p2 = factory.createIRI("http://example/p2");
            final JenaLiteral foo = factory.createLiteral("foo");
            graph.add(s2, p2, foo);
            assertEquals(4, graph.size());
            assertTrue(graph.contains(s2, p2, foo));

            // Verify the corresponding Jena Nodes are in Jena graph
            assertTrue(jGraph.contains(s2.asJenaNode(), p2.asJenaNode(), foo.asJenaNode()));

            if (DEBUG) {
                System.out.println("==== Write CommonsRDF graph\n");
                graph.stream().forEach(System.out::println);
                // And its in the Jena graph
                System.out.println("\n==== Write Jena graph directly\n");
                RDFDataMgr.write(System.out, jGraph, Lang.TTL);
            }
        }
    }
}
