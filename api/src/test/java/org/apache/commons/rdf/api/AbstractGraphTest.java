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
package org.apache.commons.rdf.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Graph implementation
 * <p>
 * To add to your implementation's tests, create a subclass with a name ending
 * in <code>Test</code> and provide {@link #createFactory()} which minimally
 * must support {@link RDF#createGraph()} and {@link RDF#createIRI(String)}, but
 * ideally support all operations.
 * <p>
 * This test uses try-with-resources blocks for calls to {@link Graph#stream()}
 * and {@link Graph#iterate()}.
 * 
 * @see Graph
 * @see RDF
 */
public abstract class AbstractGraphTest {

    protected RDF factory;
    protected Graph graph;
    protected IRI alice;
    protected IRI bob;
    protected IRI name;
    protected IRI knows;
    protected IRI member;
    protected BlankNode bnode1;
    protected BlankNode bnode2;
    protected Literal aliceName;
    protected Literal bobName;
    protected Literal secretClubName;
    protected Literal companyName;
    protected Triple bobNameTriple;

    /**
     * 
     * This method must be overridden by the implementing test to provide a
     * factory for the test to create {@link Graph}, {@link IRI} etc.
     * 
     * @return {@link RDF} instance to be tested.
     */
    protected abstract RDF createFactory();

    @Before
    public void createGraphAndAdd() {
        factory = createFactory();
        graph = factory.createGraph();
        assertEquals(0, graph.size());

        alice = factory.createIRI("http://example.com/alice");
        bob = factory.createIRI("http://example.com/bob");
        name = factory.createIRI("http://xmlns.com/foaf/0.1/name");
        knows = factory.createIRI("http://xmlns.com/foaf/0.1/knows");
        member = factory.createIRI("http://xmlns.com/foaf/0.1/member");
        try {
            bnode1 = factory.createBlankNode("org1");
            bnode2 = factory.createBlankNode("org2");
        } catch (UnsupportedOperationException ex) {
            // leave as null
        }

        try {
            secretClubName = factory.createLiteral("The Secret Club");
            companyName = factory.createLiteral("A company");
            aliceName = factory.createLiteral("Alice");
            bobName = factory.createLiteral("Bob", "en-US");
        } catch (UnsupportedOperationException ex) {
            // leave as null
        }

        if (aliceName != null) {
            graph.add(alice, name, aliceName);
        }
        graph.add(alice, knows, bob);

        if (bnode1 != null) {
            graph.add(alice, member, bnode1);
        }

        if (bobName != null) {
            try {
                bobNameTriple = factory.createTriple(bob, name, bobName);
            } catch (UnsupportedOperationException ex) {
                // leave as null
            }
            if (bobNameTriple != null) {
                graph.add(bobNameTriple);
            }
        }
        if (bnode1 != null) {
            graph.add(factory.createTriple(bob, member, bnode1));
            graph.add(factory.createTriple(bob, member, bnode2));
            if (secretClubName != null) {
                graph.add(bnode1, name, secretClubName);
                graph.add(bnode2, name, companyName);
            }
        }
    }

    @Test
    public void size() throws Exception {
        assertTrue(graph.size() > 0);
        Assume.assumeNotNull(bnode1, bnode2, aliceName, bobName, secretClubName, companyName, bobNameTriple);
        // Can only reliably predict size if we could create all triples
        assertEquals(8, graph.size());
    }

    @Test
    public void iterate() throws Exception {

        Assume.assumeTrue(graph.size() > 0);

        List<Triple> triples = new ArrayList<>();
        for (Triple t : graph.iterate()) {
            triples.add(t);
        }
        assertEquals(graph.size(), triples.size());
        if (bobNameTriple != null) {
            assertTrue(triples.contains(bobNameTriple));
        }

        // aborted iteration
        Iterable<Triple> iterate = graph.iterate();
        Iterator<Triple> it = iterate.iterator();

        assertTrue(it.hasNext());
        it.next();
        closeIterable(iterate);

        // second iteration - should start from fresh and
        // get the same count
        long count = 0;
        Iterable<Triple> iterable = graph.iterate();
        for (@SuppressWarnings("unused")
        Triple t : iterable) {
            count++;
        }
        assertEquals(graph.size(), count);
    }

    /**
     * Special triple closing for RDF4J.
     */
    private void closeIterable(Iterable<Triple> iterate) throws Exception {
        if (iterate instanceof AutoCloseable) {
            ((AutoCloseable) iterate).close();
        }
    }

    @Test
    public void iterateFilter() throws Exception {
        List<RDFTerm> friends = new ArrayList<>();
        IRI alice = factory.createIRI("http://example.com/alice");
        IRI knows = factory.createIRI("http://xmlns.com/foaf/0.1/knows");
        for (Triple t : graph.iterate(alice, knows, null)) {
            friends.add(t.getObject());
        }
        assertEquals(1, friends.size());
        assertEquals(bob, friends.get(0));

        // .. can we iterate over zero hits?
        Iterable<Triple> iterate = graph.iterate(bob, knows, alice);
        for (Triple unexpected : iterate) {
            fail("Unexpected triple " + unexpected);
        }
        // closeIterable(iterate);
    }

    @Test
    public void contains() throws Exception {
        assertFalse(graph.contains(bob, knows, alice)); // or so he claims..

        assertTrue(graph.contains(alice, knows, bob));

        try (Stream<? extends Triple> stream = graph.stream()) {
            Optional<? extends Triple> first = stream.skip(4).findFirst();
            Assume.assumeTrue(first.isPresent());
            Triple existingTriple = first.get();
            assertTrue(graph.contains(existingTriple));
        }

        Triple nonExistingTriple = factory.createTriple(bob, knows, alice);
        assertFalse(graph.contains(nonExistingTriple));

        Triple triple = null;
        try {
            triple = factory.createTriple(alice, knows, bob);
        } catch (UnsupportedOperationException ex) {
        }
        if (triple != null) {
            // FIXME: Should not this always be true?
            // assertTrue(graph.contains(triple));
        }
    }

    @Test
    public void remove() throws Exception {
        long fullSize = graph.size();
        graph.remove(alice, knows, bob);
        long shrunkSize = graph.size();
        assertEquals(1, fullSize - shrunkSize);

        graph.remove(alice, knows, bob);
        assertEquals(shrunkSize, graph.size()); // unchanged

        graph.add(alice, knows, bob);
        graph.add(alice, knows, bob);
        graph.add(alice, knows, bob);
        // Undetermined size at this point -- but at least it
        // should be bigger
        assertTrue(graph.size() > shrunkSize);

        // and after a single remove they should all be gone
        graph.remove(alice, knows, bob);
        assertEquals(shrunkSize, graph.size());

        Triple otherTriple;
        try (Stream<? extends Triple> stream = graph.stream()) {
            Optional<? extends Triple> anyTriple = stream.findAny();
            Assume.assumeTrue(anyTriple.isPresent());
            otherTriple = anyTriple.get();
        }

        graph.remove(otherTriple);
        assertEquals(shrunkSize - 1, graph.size());
        graph.remove(otherTriple);
        assertEquals(shrunkSize - 1, graph.size()); // no change

        // for some reason in rdf4j this causes duplicates!
        graph.add(otherTriple);
        // graph.stream().forEach(System.out::println);
        // should have increased
        assertTrue(graph.size() >= shrunkSize);
    }

    @Test
    public void clear() throws Exception {
        graph.clear();
        assertFalse(graph.contains(alice, knows, bob));
        assertEquals(0, graph.size());
        graph.clear(); // no-op
        assertEquals(0, graph.size());
    }

    @Test
    public void getTriples() throws Exception {
        long tripleCount;
        try (Stream<? extends Triple> stream = graph.stream()) {
            tripleCount = stream.count();
        }
        assertTrue(tripleCount > 0);

        try (Stream<? extends Triple> stream = graph.stream()) {
            assertTrue(stream.allMatch(t -> graph.contains(t)));
        }

        // Check exact count
        Assume.assumeNotNull(bnode1, bnode2, aliceName, bobName, secretClubName, companyName, bobNameTriple);
        assertEquals(8, tripleCount);
    }

    @Test
    public void getTriplesQuery() throws Exception {

        try (Stream<? extends Triple> stream = graph.stream(alice, null, null)) {
            long aliceCount = stream.count();
            assertTrue(aliceCount > 0);
            Assume.assumeNotNull(aliceName);
            assertEquals(3, aliceCount);
        }

        Assume.assumeNotNull(bnode1, bnode2, bobName, companyName, secretClubName);
        try (Stream<? extends Triple> stream = graph.stream(null, name, null)) {
            assertEquals(4, stream.count());
        }
        Assume.assumeNotNull(bnode1);
        try (Stream<? extends Triple> stream = graph.stream(null, member, null)) {
            assertEquals(3, stream.count());
        }
    }

    @Test
    public void addBlankNodesFromMultipleGraphs() {

        try {
            // Create two separate Graph instances
            Graph g1 = createGraph1();
            Graph g2 = createGraph2();

            // and add them to a new Graph g3
            Graph g3 = factory.createGraph();
            addAllTriples(g1, g3);
            addAllTriples(g2, g3);

            // Let's make a map to find all those blank nodes after insertion
            // (The Graph implementation is not currently required to
            // keep supporting those BlankNodes with contains() - see
            // COMMONSRDF-15)

            final Map<String, BlankNodeOrIRI> whoIsWho = new ConcurrentHashMap<>();
            // ConcurrentHashMap as we will try parallel forEach below,
            // which should not give inconsistent results (it does with a
            // HashMap!)

            // look up BlankNodes by name
            IRI name = factory.createIRI("http://xmlns.com/foaf/0.1/name");
            try (Stream<? extends Triple> stream = g3.stream(null, name, null)) {
                stream.parallel().forEach(t -> whoIsWho.put(t.getObject().ntriplesString(), t.getSubject()));
            }

            assertEquals(4, whoIsWho.size());
            // and contains 4 unique values
            assertEquals(4, new HashSet<>(whoIsWho.values()).size());

            BlankNodeOrIRI b1Alice = whoIsWho.get("\"Alice\"");
            assertNotNull(b1Alice);
            BlankNodeOrIRI b2Bob = whoIsWho.get("\"Bob\"");
            assertNotNull(b2Bob);
            BlankNodeOrIRI b1Charlie = whoIsWho.get("\"Charlie\"");
            assertNotNull(b1Charlie);
            BlankNodeOrIRI b2Dave = whoIsWho.get("\"Dave\"");
            assertNotNull(b2Dave);

            // All blank nodes should differ
            notEquals(b1Alice, b2Bob);
            notEquals(b1Alice, b1Charlie);
            notEquals(b1Alice, b2Dave);
            notEquals(b2Bob, b1Charlie);
            notEquals(b2Bob, b2Dave);
            notEquals(b1Charlie, b2Dave);

            // And we should be able to query with them again
            // as we got them back from g3
            IRI hasChild = factory.createIRI("http://example.com/hasChild");
            assertTrue(g3.contains(b1Alice, hasChild, b2Bob));
            assertTrue(g3.contains(b2Dave, hasChild, b1Charlie));
            // But not
            assertFalse(g3.contains(b1Alice, hasChild, b1Alice));
            assertFalse(g3.contains(b1Alice, hasChild, b1Charlie));
            assertFalse(g3.contains(b1Alice, hasChild, b2Dave));
            // nor
            assertFalse(g3.contains(b2Dave, hasChild, b1Alice));
            assertFalse(g3.contains(b2Dave, hasChild, b1Alice));

            // and these don't have any children (as far as we know)
            assertFalse(g3.contains(b2Bob, hasChild, null));
            assertFalse(g3.contains(b1Charlie, hasChild, null));
        } catch (UnsupportedOperationException ex) {
            Assume.assumeNoException(ex);
        }
    }

    private void notEquals(BlankNodeOrIRI node1, BlankNodeOrIRI node2) {
        assertFalse(node1.equals(node2));
        // in which case we should be able to assume
        // (as they are in the same graph)
        assertFalse(node1.ntriplesString().equals(node2.ntriplesString()));
    }

    /**
     * Add all triples from the source to the target.
     * <p>
     * The triples may be copied in any order. No special conversion or
     * adaptation of {@link BlankNode}s are performed.
     *
     * @param source
     *            Source Graph to copy triples from
     * @param target
     *            Target Graph where triples will be added
     */
    private void addAllTriples(Graph source, Graph target) {

        // unordered() as we don't need to preserve triple order
        // sequential() as we don't (currently) require target Graph to be
        // thread-safe

        try (Stream<? extends Triple> stream = source.stream()) {
            stream.unordered().sequential().forEach(t -> target.add(t));
        }
    }

    /**
     * Make a new graph with two BlankNodes - each with a different
     * uniqueReference
     */
    private Graph createGraph1() {
        RDF factory1 = createFactory();

        IRI name = factory1.createIRI("http://xmlns.com/foaf/0.1/name");
        Graph g1 = factory1.createGraph();
        BlankNode b1 = createOwnBlankNode("b1", "0240eaaa-d33e-4fc0-a4f1-169d6ced3680");
        g1.add(b1, name, factory1.createLiteral("Alice"));

        BlankNode b2 = createOwnBlankNode("b2", "9de7db45-0ce7-4b0f-a1ce-c9680ffcfd9f");
        g1.add(b2, name, factory1.createLiteral("Bob"));

        IRI hasChild = factory1.createIRI("http://example.com/hasChild");
        g1.add(b1, hasChild, b2);

        return g1;
    }

    /**
     * Create a different implementation of BlankNode to be tested with
     * graph.add(a,b,c); (the implementation may or may not then choose to
     * translate such to its own instances)
     * 
     * @param name
     * @return
     */
    private BlankNode createOwnBlankNode(String name, String uuid) {
        return new BlankNode() {
            @Override
            public String ntriplesString() {
                return "_: " + name;
            }

            @Override
            public String uniqueReference() {
                return uuid;
            }

            @Override
            public int hashCode() {
                return uuid.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                if (!(obj instanceof BlankNode)) {
                    return false;
                }
                BlankNode other = (BlankNode) obj;
                return uuid.equals(other.uniqueReference());
            }
        };
    }

    private Graph createGraph2() {
        RDF factory2 = createFactory();
        IRI name = factory2.createIRI("http://xmlns.com/foaf/0.1/name");

        Graph g2 = factory2.createGraph();

        BlankNode b1 = createOwnBlankNode("b1", "bc8d3e45-a08f-421d-85b3-c25b373abf87");
        g2.add(b1, name, factory2.createLiteral("Charlie"));

        BlankNode b2 = createOwnBlankNode("b2", "2209097a-5078-4b03-801a-6a2d2f50d739");
        g2.add(b2, name, factory2.createLiteral("Dave"));

        IRI hasChild = factory2.createIRI("http://example.com/hasChild");
        // NOTE: Opposite direction of loadGraph1
        g2.add(b2, hasChild, b1);
        return g2;
    }

    /**
     * An attempt to use the Java 8 streams to look up a more complicated query.
     * <p>
     * FYI, the equivalent SPARQL version (untested):
     * 
     * <pre>
     *     SELECT ?orgName WHERE {
     *             ?org foaf:name ?orgName .
     *             ?alice foaf:member ?org .
     *             ?bob foaf:member ?org .
     *             ?alice foaf:knows ?bob .
     *           FILTER NOT EXIST { ?bob foaf:knows ?alice }
     *    }
     * </pre>
     *
     * @throws Exception If test fails
     */
    @Test
    public void whyJavaStreamsMightNotTakeOverFromSparql() throws Exception {
        Assume.assumeNotNull(bnode1, bnode2, secretClubName);
        // Find a secret organizations
        try (Stream<? extends Triple> stream = graph.stream(null, knows, null)) {
            assertEquals("\"The Secret Club\"",
                    // Find One-way "knows"
                    stream.filter(t -> !graph.contains((BlankNodeOrIRI) t.getObject(), knows, t.getSubject()))
                            .map(knowsTriple -> {
                                try (Stream<? extends Triple> memberOf = graph
                                        // and those they know, what are they
                                        // member of?
                                        .stream((BlankNodeOrIRI) knowsTriple.getObject(), member, null)) {
                                    return memberOf
                                            // keep those which first-guy is a
                                            // member of
                                            .filter(memberTriple -> graph.contains(knowsTriple.getSubject(), member,
                                                    // First hit is good enough
                                                    memberTriple.getObject()))
                                            .findFirst().get().getObject();
                                }
                            })
                            // then look up the name of that org
                            .map(org -> {
                                try (Stream<? extends Triple> orgName = graph.stream((BlankNodeOrIRI) org, name,
                                        null)) {
                                    return orgName.findFirst().get().getObject().ntriplesString();
                                }
                            }).findFirst().get());
        }
    }
}
