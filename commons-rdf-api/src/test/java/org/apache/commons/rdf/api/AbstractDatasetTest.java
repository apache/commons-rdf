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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test Dataset implementation
 * <p>
 * To add to your implementation's tests, create a subclass with a name ending
 * in <code>Test</code> and provide {@link #createFactory()} which minimally
 * must support {@link RDF#createDataset()} and {@link RDF#createIRI(String)}, but
 * ideally support all operations.
 * <p>
 * This test uses try-with-resources blocks for calls to {@link Dataset#stream()}
 * and {@link Dataset#iterate()}.
 *
 * @see Dataset
 * @see RDF
 */
public abstract class AbstractDatasetTest {

    private static Optional<? extends Quad> closableFindAny(final Stream<? extends Quad> stream) {
        try (Stream<? extends Quad> s = stream) {
            return s.findAny();
        }
    }
    protected RDF factory;
    protected Dataset dataset;
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
    protected Quad bobNameQuad;
    private IRI isPrimaryTopicOf;
    private IRI graph1;

    private BlankNode graph2;

    /**
     * Add all quads from the source to the target.
     * <p>
     * The quads may be copied in any order. No special conversion or
     * adaptation of {@link BlankNode}s are performed.
     *
     * @param source
     *            Source Dataset to copy quads from
     * @param target
     *            Target Dataset where quads will be added
     */
    private void addAllQuads(final Dataset source, final Dataset target) {

        // unordered() as we don't need to preserve quad order
        // sequential() as we don't (currently) require target Dataset to be
        // thread-safe

        try (Stream<? extends Quad> stream = source.stream()) {
            stream.unordered().sequential().forEach(target::add);
        }
    }

    /**
     * Special quad closing for RDF4J.
     */
    private void closeIterable(final Iterable<Quad> iterate) throws Exception {
        if (iterate instanceof AutoCloseable) {
            ((AutoCloseable) iterate).close();
        }
    }

    /**
     * Make a new dataset with two BlankNodes - each with a different
     * uniqueReference
     */
    private Dataset createDataset1() {
        final RDF factory1 = createFactory();

        final IRI name = factory1.createIRI("http://xmlns.com/foaf/0.1/name");
        final Dataset g1 = factory1.createDataset();
        final BlankNode b1 = createOwnBlankNode("b1", "0240eaaa-d33e-4fc0-a4f1-169d6ced3680");
        g1.add(b1, b1, name, factory1.createLiteral("Alice"));

        final BlankNode b2 = createOwnBlankNode("b2", "9de7db45-0ce7-4b0f-a1ce-c9680ffcfd9f");
        g1.add(b2, b2, name, factory1.createLiteral("Bob"));

        final IRI hasChild = factory1.createIRI("http://example.com/hasChild");
        g1.add(null, b1, hasChild, b2);

        return g1;
    }

    private Dataset createDataset2() {
        final RDF factory2 = createFactory();
        final IRI name = factory2.createIRI("http://xmlns.com/foaf/0.1/name");

        final Dataset g2 = factory2.createDataset();

        final BlankNode b1 = createOwnBlankNode("b1", "bc8d3e45-a08f-421d-85b3-c25b373abf87");
        g2.add(b1, b1, name, factory2.createLiteral("Charlie"));

        final BlankNode b2 = createOwnBlankNode("b2", "2209097a-5078-4b03-801a-6a2d2f50d739");
        g2.add(b2, b2, name, factory2.createLiteral("Dave"));

        final IRI hasChild = factory2.createIRI("http://example.com/hasChild");
        // NOTE: Opposite direction of loadDataset1
        g2.add(b2, b2, hasChild, b1);
        return g2;
    }

    @BeforeEach
    public void createDatasetAndAdd() {
        factory = createFactory();
        dataset = factory.createDataset();
        assertEquals(0, dataset.size());

        graph1 = factory.createIRI("http://example.com/graph1");
        graph2 = factory.createBlankNode();

        alice = factory.createIRI("http://example.com/alice");
        bob = factory.createIRI("http://example.com/bob");
        name = factory.createIRI("http://xmlns.com/foaf/0.1/name");
        knows = factory.createIRI("http://xmlns.com/foaf/0.1/knows");
        member = factory.createIRI("http://xmlns.com/foaf/0.1/member");
        bnode1 = factory.createBlankNode("org1");
        bnode2 = factory.createBlankNode("org2");

        secretClubName = factory.createLiteral("The Secret Club");
        companyName = factory.createLiteral("A company");
        aliceName = factory.createLiteral("Alice");
        bobName = factory.createLiteral("Bob", "en-US");

        dataset.add(graph1, alice, name, aliceName);
        dataset.add(graph1, alice, knows, bob);

        dataset.add(graph1, alice, member, bnode1);

        bobNameQuad = factory.createQuad(graph2, bob, name, bobName);
        dataset.add(bobNameQuad);

        dataset.add(factory.createQuad(graph2, bob, member, bnode1));
        dataset.add(factory.createQuad(graph2, bob, member, bnode2));
        // NOTE: bnode1 used in both graph1 and graph2
        dataset.add(graph1, bnode1, name, secretClubName);
        dataset.add(graph2, bnode2, name, companyName);

        // default graph describes graph1 and graph2
        isPrimaryTopicOf = factory.createIRI("http://xmlns.com/foaf/0.1/isPrimaryTopicOf");
        dataset.add(null, alice, isPrimaryTopicOf, graph1);
        dataset.add(null, bob, isPrimaryTopicOf, graph2);

    }

    /**
     *
     * This method must be overridden by the implementing test to provide a
     * factory for the test to create {@link Dataset}, {@link IRI} etc.
     *
     * @return {@link RDF} instance to be tested.
     */
    protected abstract RDF createFactory();

    /**
     * Create a different implementation of BlankNode to be tested with
     * dataset.add(a,b,c); (the implementation may or may not then choose to
     * translate such to its own instances)
     *
     * @param name
     * @return
     */
    private BlankNode createOwnBlankNode(final String name, final String uuid) {
        return new BlankNode() {
            @Override
            public boolean equals(final Object obj) {
                if (!(obj instanceof BlankNode)) {
                    return false;
                }
                final BlankNode other = (BlankNode) obj;
                return uuid.equals(other.uniqueReference());
            }

            @Override
            public int hashCode() {
                return uuid.hashCode();
            }

            @Override
            public String ntriplesString() {
                return "_: " + name;
            }

            @Override
            public String uniqueReference() {
                return uuid;
            }
        };
    }

    private void notEquals(final BlankNodeOrIRI node1, final BlankNodeOrIRI node2) {
        assertNotEquals(node1, node2);
        // in which case we should be able to assume
        // (as they are in the same dataset)
        assertNotEquals(node1.ntriplesString(), node2.ntriplesString());
    }

    @Test
    void testAddBlankNodesFromMultipleDatasets() throws Exception {
        // Create two separate Dataset instances
        try (final Dataset g1 = createDataset1();
                final Dataset g2 = createDataset2();
                final Dataset g3 = factory.createDataset()) {

            addAllQuads(g1, g3);
            addAllQuads(g2, g3);

            // Let's make a map to find all those blank nodes after insertion
            // (The Dataset implementation is not currently required to
            // keep supporting those BlankNodes with contains() - see
            // COMMONSRDF-15)

            final Map<String, BlankNodeOrIRI> whoIsWho = new ConcurrentHashMap<>();
            // ConcurrentHashMap as we will try parallel forEach below,
            // which should not give inconsistent results (it does with a
            // HashMap!)

            // look up BlankNodes by name
            final IRI name = factory.createIRI("http://xmlns.com/foaf/0.1/name");
            try (Stream<? extends Quad> stream = g3.stream(null, null, name, null)) {
                stream.parallel().forEach(t -> whoIsWho.put(t.getObject().ntriplesString(), t.getSubject()));
            }

            assertEquals(4, whoIsWho.size());
            // and contains 4 unique values
            assertEquals(4, new HashSet<>(whoIsWho.values()).size());

            final BlankNodeOrIRI b1Alice = whoIsWho.get("\"Alice\"");
            assertNotNull(b1Alice);
            final BlankNodeOrIRI b2Bob = whoIsWho.get("\"Bob\"");
            assertNotNull(b2Bob);
            final BlankNodeOrIRI b1Charlie = whoIsWho.get("\"Charlie\"");
            assertNotNull(b1Charlie);
            final BlankNodeOrIRI b2Dave = whoIsWho.get("\"Dave\"");
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
            final IRI hasChild = factory.createIRI("http://example.com/hasChild");
            // FIXME: Check graph2 BlankNode in these ..?
            assertTrue(g3.contains(null, b1Alice, hasChild, b2Bob));
            assertTrue(g3.contains(null, b2Dave, hasChild, b1Charlie));
            // But not
            assertFalse(g3.contains(null, b1Alice, hasChild, b1Alice));
            assertFalse(g3.contains(null, b1Alice, hasChild, b1Charlie));
            assertFalse(g3.contains(null, b1Alice, hasChild, b2Dave));
            // nor
            assertFalse(g3.contains(null, b2Dave, hasChild, b1Alice));
            assertFalse(g3.contains(null, b2Dave, hasChild, b1Alice));

            // and these don't have any children (as far as we know)
            assertFalse(g3.contains(null, b2Bob, hasChild, null));
            assertFalse(g3.contains(null, b1Charlie, hasChild, null));
        }
    }

    @Test
    void testClear() throws Exception {
        dataset.clear();
        assertFalse(dataset.contains(null, alice, knows, bob));
        assertEquals(0, dataset.size());
        dataset.clear(); // no-op
        assertEquals(0, dataset.size());
        assertFalse(dataset.contains(null, null, null, null)); // nothing here
    }

    @Test
    void testContains() throws Exception {
        assertFalse(dataset.contains(null, bob, knows, alice)); // or so he claims.

        assertTrue(dataset.contains(Optional.of(graph1), alice, knows, bob));

        try (Stream<? extends Quad> stream = dataset.stream()) {
            final Optional<? extends Quad> first = stream.skip(4).findFirst();
            assumeTrue(first.isPresent());
            final Quad existingQuad = first.get();
            assertTrue(dataset.contains(existingQuad));
        }

        final Quad nonExistingQuad = factory.createQuad(graph2, bob, knows, alice);
        assertFalse(dataset.contains(nonExistingQuad));

        // An existing quad
        final Quad quad = factory.createQuad(graph1, alice, knows, bob);
        // FIXME: Should not this always be true?
         assertTrue(dataset.contains(quad));
    }

    @Test
    void testContainsLanguageTagsCaseInsensitive() {
        // COMMONSRDF-51: Ensure we can add/contains/remove with any casing
        // of literal language tag
        final Literal lower = factory.createLiteral("Hello there", "en-gb");
        final Literal upper = factory.createLiteral("Hello there", "EN-GB");
        final Literal mixed = factory.createLiteral("Hello there", "en-GB");

        final IRI example1 = factory.createIRI("http://example.com/s1");
        final IRI greeting = factory.createIRI("http://example.com/greeting");

        dataset.add(null, example1, greeting, upper);

        // any kind of Triple should match
        assertTrue(dataset.contains(factory.createQuad(null, example1, greeting, upper)));
        assertTrue(dataset.contains(factory.createQuad(null, example1, greeting, lower)));
        assertTrue(dataset.contains(factory.createQuad(null, example1, greeting, mixed)));

        // or as patterns
        assertTrue(dataset.contains(null, null, null, upper));
        assertTrue(dataset.contains(null, null, null, lower));
        assertTrue(dataset.contains(null, null, null, mixed));
    }

    @Test
    void testContainsLanguageTagsCaseInsensitiveTurkish() {
        // COMMONSRDF-51: Special test for Turkish issue where
        // "i".toLowerCase() != "i"
        // See also:
        // https://garygregory.wordpress.com/2015/11/03/java-lowercase-conversion-turkey/

        // This is similar to the test in AbstractRDFTest, but on a graph
        final Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ROOT);
            final Literal lowerROOT = factory.createLiteral("moi", "fi");
            final Literal upperROOT = factory.createLiteral("moi", "FI");
            final Literal mixedROOT = factory.createLiteral("moi", "fI");
            final IRI exampleROOT = factory.createIRI("http://example.com/s1");
            final IRI greeting = factory.createIRI("http://example.com/greeting");
            dataset.add(null, exampleROOT, greeting, mixedROOT);

            final Locale turkish = Locale.forLanguageTag("TR");
            Locale.setDefault(turkish);
            // If the below assertion fails, then the Turkish
            // locale no longer have this peculiarity that
            // we want to test.
            assumeFalse("FI".toLowerCase().equals("fi"));

            // Below is pretty much the same as in
            // containsLanguageTagsCaseInsensitive()
            final Literal lower = factory.createLiteral("moi", "fi");
            final Literal upper = factory.createLiteral("moi", "FI");
            final Literal mixed = factory.createLiteral("moi", "fI");

            final IRI exampleTR = factory.createIRI("http://example.com/s2");
            dataset.add(null, exampleTR, greeting, upper);
            assertTrue(dataset.contains(factory.createQuad(null, exampleTR, greeting, upper)));
            assertTrue(dataset.contains(factory.createQuad(null, exampleTR, greeting, upperROOT)));
            assertTrue(dataset.contains(factory.createQuad(null, exampleTR, greeting, lower)));
            assertTrue(dataset.contains(factory.createQuad(null, exampleTR, greeting, lowerROOT)));
            assertTrue(dataset.contains(factory.createQuad(null, exampleTR, greeting, mixed)));
            assertTrue(dataset.contains(factory.createQuad(null, exampleTR, greeting, mixedROOT)));
            assertTrue(dataset.contains(null, exampleTR, null, upper));
            assertTrue(dataset.contains(null, exampleTR, null, upperROOT));
            assertTrue(dataset.contains(null, exampleTR, null, lower));
            assertTrue(dataset.contains(null, exampleTR, null, lowerROOT));
            assertTrue(dataset.contains(null, exampleTR, null, mixed));
            assertTrue(dataset.contains(null, exampleTR, null, mixedROOT));

            // What about the triple we added while in ROOT locale?
            assertTrue(dataset.contains(factory.createQuad(null, exampleROOT, greeting, upper)));
            assertTrue(dataset.contains(factory.createQuad(null, exampleROOT, greeting, lower)));
            assertTrue(dataset.contains(factory.createQuad(null, exampleROOT, greeting, mixed)));
            assertTrue(dataset.contains(null, exampleROOT, null, upper));
            assertTrue(dataset.contains(null, exampleROOT, null, lower));
            assertTrue(dataset.contains(null, exampleROOT, null, mixed));
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    @Test
    void testGetGraph() throws Exception {
        try (final Graph defaultGraph = dataset.getGraph()) {
            // TODO: Can we assume the default graph was empty before our new triples?
            assertEquals(2, defaultGraph.size());
            assertFalse(defaultGraph.isEmpty());
            assertTrue(defaultGraph.contains(alice, isPrimaryTopicOf, graph1));
            // NOTE: graph2 is a BlankNode
            assertTrue(defaultGraph.contains(bob, isPrimaryTopicOf, null));
            final Triple triple = factory.createTriple(bnode1, alice, bob);
            defaultGraph.add(triple);
            assertTrue(defaultGraph.contains(triple));
            defaultGraph.remove(triple);
            assertFalse(defaultGraph.contains(triple));
            defaultGraph.add(bnode1, alice, bob);
            assertTrue(defaultGraph.contains(triple));
            defaultGraph.remove(bnode1, alice, bob);
            assertFalse(defaultGraph.contains(triple));
            defaultGraph.add(triple);
            assertTrue(defaultGraph.contains(triple));
            defaultGraph.clear();
            assertFalse(defaultGraph.contains(triple));
        }
    }

    @Test
    void testGetGraph1() throws Exception {
        // graph1 should be present
        try (final Graph g1 = dataset.getGraph(graph1).get()) {
            assertEquals(4, g1.size());

            assertTrue(g1.contains(alice, name, aliceName));
            assertTrue(g1.contains(alice, knows, bob));
            assertTrue(g1.contains(alice, member, null));
            assertTrue(g1.contains(null, name, secretClubName));
        }
    }

    @Test
    void testGetGraph2() throws Exception {
        // graph2 should be present, even if is named by a BlankNode
        // We'll look up the potentially mapped graph2 blanknode
        final BlankNodeOrIRI graph2Name = (BlankNodeOrIRI) dataset.stream(Optional.empty(), bob, isPrimaryTopicOf, null)
                .map(Quad::getObject).findAny().get();

        try (final Graph g2 = dataset.getGraph(graph2Name).get()) {
            assertEquals(4, g2.size());
            final Triple bobNameTriple = bobNameQuad.asTriple();
            assertTrue(g2.contains(bobNameTriple));
            assertTrue(g2.contains(bob, member, bnode1));
            assertTrue(g2.contains(bob, member, bnode2));
            assertFalse(g2.contains(bnode1, name, secretClubName));
            assertTrue(g2.contains(bnode2, name, companyName));
        }
    }

    /**
     * Ensure {@link Dataset#getGraphNames()} contains our two graphs.
     *
     * @throws Exception
     *             If test fails
     */
    @Test
    void testGetGraphNames() throws Exception {
        final Set<BlankNodeOrIRI> names = dataset.getGraphNames().collect(Collectors.toSet());
        assertTrue(names.contains(graph1), "Can't find graph name " + graph1);
        assertTrue(dataset.contains(Optional.of(graph1), null, null, null), "Found no quads in graph1");

        final Optional<BlankNodeOrIRI> graphName2 = dataset.getGraphNames().filter(BlankNode.class::isInstance).findAny();
        assertTrue(graphName2.isPresent(), "Could not find graph2-like BlankNode");
        assertTrue(dataset.contains(graphName2, null, null, null), "Found no quads in graph2");

        // Some implementations like Virtuoso might have additional internal graphs,
        // so we can't assume this:
        //assertEquals(2, names.size());
    }

    @Test
    void testGetGraphNull() throws Exception {
        // Default graph should be present
        try (final Graph defaultGraph = dataset.getGraph(null).get()) {
            // TODO: Can we assume the default graph was empty before our new triples?
            assertEquals(2, defaultGraph.size());
            assertTrue(defaultGraph.contains(alice, isPrimaryTopicOf, graph1));
            // NOTE: wildcard as graph2 is a (potentially mapped) BlankNode
            assertTrue(defaultGraph.contains(bob, isPrimaryTopicOf, null));
        }
    }

    @Test
    void testGetQuads() throws Exception {
        long quadCount;
        try (Stream<? extends Quad> stream = dataset.stream()) {
            quadCount = stream.count();
        }
        assertTrue(quadCount > 0);

        try (Stream<? extends Quad> stream = dataset.stream()) {
            assertTrue(stream.allMatch(t -> dataset.contains(t)));
        }

        // Check exact count
        assumeTrue(bnode1 != null && bnode2 != null && aliceName != null && bobName != null && secretClubName != null && companyName != null && bobNameQuad != null);
        assertEquals(10, quadCount);
    }

    @Test
    void testGetQuadsQuery() throws Exception {

        try (Stream<? extends Quad> stream = dataset.stream(Optional.of(graph1), alice, null, null)) {
            final long aliceCount = stream.count();
            assertTrue(aliceCount > 0);
            assumeTrue(aliceName != null);
            assertEquals(3, aliceCount);
        }

        assumeTrue(bnode1 != null && bnode2 != null && bobName != null && companyName != null && secretClubName != null);
        try (Stream<? extends Quad> stream = dataset.stream(null, null, name, null)) {
            assertEquals(4, stream.count());
        }
        assumeTrue(bnode1 != null);
        try (Stream<? extends Quad> stream = dataset.stream(null, null, member, null)) {
            assertEquals(3, stream.count());
        }
    }

    @Test
    void testIterate() throws Exception {
        assumeFalse(dataset.isEmpty());
        final List<Quad> quads = new ArrayList<>();
        for (final Quad t : dataset.iterate()) {
            quads.add(t);
        }
        assertFalse(dataset.isEmpty());
        assertEquals(dataset.size(), quads.size());

        //assertTrue(quads.contains(bobNameQuad));
        // java.util.List won't do any BlankNode mapping, so
        // instead bobNameQuad of let's check for an IRI-centric quad
        final Quad q = factory.createQuad(graph1, alice, name, aliceName);
        quads.contains(q);

        // aborted iteration
        final Iterable<Quad> iterate = dataset.iterate();
        final Iterator<Quad> it = iterate.iterator();

        assertTrue(it.hasNext());
        it.next();
        closeIterable(iterate);

        // second iteration - should start from fresh and
        // get the same count
        long count = 0;
        final Iterable<Quad> iterable = dataset.iterate();
        for (@SuppressWarnings("unused") final
        Quad t : iterable) {
            count++;
        }
        assertEquals(dataset.size(), count);

        // Pattern iteration which should cover multiple graphs.

        final Set<Quad> aliceQuads = new HashSet<>();
        for (final Quad aliceQ : dataset.iterate(null, alice, null, null)) {
            aliceQuads.add(aliceQ);
        }
        assertTrue(aliceQuads.contains(factory.createQuad(graph1, alice, name, aliceName)));
        assertTrue(aliceQuads.contains(factory.createQuad(graph1, alice, knows, bob)));
        // We can't test this by Quad equality, as bnode1 might become mapped by the
        // dataset
        //assertTrue(aliceQuads.contains(factory.createQuad(graph1, alice, member, bnode1)));
        assertTrue(aliceQuads.contains(factory.createQuad(null, alice, isPrimaryTopicOf, graph1)));
        assertEquals(4, aliceQuads.size());

        // Check the isPrimaryTopicOf statements in the default graph
        int topics = 0;
        for (final Quad topic : dataset.iterate(null, null, isPrimaryTopicOf, null)) {
            topics++;
            // COMMONSRDF-55: should not be <urn:x-arq:defaultgraph> or similar
            assertFalse(topic.getGraphName().isPresent());
        }
        assertEquals(2, topics);
    }

    @Test
    void testIterateFilter() throws Exception {
        final List<RDFTerm> friends = new ArrayList<>();
        final IRI alice = factory.createIRI("http://example.com/alice");
        final IRI knows = factory.createIRI("http://xmlns.com/foaf/0.1/knows");
        for (final Quad t : dataset.iterate(null, alice, knows, null)) {
            friends.add(t.getObject());
        }
        assertEquals(1, friends.size());
        assertEquals(bob, friends.get(0));

        // .. can we iterate over zero hits?
        final Iterable<Quad> iterate = dataset.iterate(Optional.of(graph2), bob, knows, alice);
        for (final Quad unexpected : iterate) {
            fail("Unexpected quad " + unexpected);
        }
        // closeIterable(iterate);
    }

    @Test
    void testRemove() throws Exception {
        final long fullSize = dataset.size();
        dataset.remove(Optional.of(graph1), alice, knows, bob);
        final long shrunkSize = dataset.size();
        assertEquals(1, fullSize - shrunkSize);

        dataset.remove(Optional.of(graph1), alice, knows, bob);
        assertEquals(shrunkSize, dataset.size()); // unchanged

        dataset.add(graph1, alice, knows, bob);
        dataset.add(graph2, alice, knows, bob);
        dataset.add(graph2, alice, knows, bob);
        // Undetermined size at this point -- but at least it
        // should be bigger
        assertTrue(dataset.size() > shrunkSize);

        // and after a single remove they should all be gone
        dataset.remove(null, alice, knows, bob);
        assertEquals(shrunkSize, dataset.size());

        Quad otherQuad;
        try (Stream<? extends Quad> stream = dataset.stream()) {
            final Optional<? extends Quad> anyQuad = stream.findAny();
            assumeTrue(anyQuad.isPresent());
            otherQuad = anyQuad.get();
        }

        dataset.remove(otherQuad);
        assertEquals(shrunkSize - 1, dataset.size());
        dataset.remove(otherQuad);
        assertEquals(shrunkSize - 1, dataset.size()); // no change

        // for some reason in rdf4j this causes duplicates!
        dataset.add(otherQuad);
        // dataset.stream().forEach(System.out::println);
        // should have increased
        assertTrue(dataset.size() >= shrunkSize);
    }

    @Test
    void testRemoveLanguageTagsCaseInsensitive() {
        // COMMONSRDF-51: Ensure we can remove with any casing
        // of literal language tag
        final Literal lower = factory.createLiteral("Howdy", "en-us");
        final Literal upper = factory.createLiteral("Howdy", "EN-US");
        final Literal mixed = factory.createLiteral("Howdy", "en-US");

        final IRI example1 = factory.createIRI("http://example.com/s1");
        final IRI greeting = factory.createIRI("http://example.com/greeting");

        dataset.add(null, example1, greeting, upper);

        // Remove should also honor any case
        dataset.remove(null, example1, null, mixed);
        assertFalse(dataset.contains(null, null, greeting, null));

        dataset.add(null, example1, greeting, lower);
        dataset.remove(null, example1, null, upper);

        // Check with Triple
        dataset.add(factory.createQuad(null, example1, greeting, mixed));
        dataset.remove(factory.createQuad(null, example1, greeting, upper));
        assertFalse(dataset.contains(null, null, greeting, null));
    }

    @Test
    void testSize() throws Exception {
        assertEquals(10, dataset.size());
    }

    @Test
    void testStreamDefaultGraphNameAlice() throws Exception {
        // null below would match in ANY graph (including default graph)
        final Optional<? extends Quad> aliceTopic = dataset.stream(null, alice, isPrimaryTopicOf, null).findAny();
        assertTrue(aliceTopic.isPresent());
        // COMMONSRDF-55: should not be <urn:x-arq:defaultgraph> or similar
        assertNull(aliceTopic.get().getGraphName().orElse(null));
        assertFalse(aliceTopic.get().getGraphName().isPresent());
    }

    @Test
    void testStreamDefaultGraphNameByPattern() throws Exception {
        dataset.stream(Optional.empty(), null, null, null).forEach(quad -> {
            // COMMONSRDF-55: should not be <urn:x-arq:defaultgraph> or similar
            Boolean isGraph1 = quad.getGraphName().map(gn -> gn.equals(graph1)).orElse(false);
            Boolean isGraph2 = quad.getGraphName().map(gn -> gn.equals(graph2)).orElse(false);
            if (!(isGraph1 || isGraph2)) {
                assertNull(quad.getGraphName().orElse(null));
                assertFalse(quad.getGraphName().isPresent());
            }
        });
    }

    @Test
    void testStreamLanguageTagsCaseInsensitive() {
        // COMMONSRDF-51: Ensure we can add/contains/remove with any casing
        // of literal language tag
        final Literal lower = factory.createLiteral("Good afternoon", "en-gb");
        final Literal upper = factory.createLiteral("Good afternoon", "EN-GB");
        final Literal mixed = factory.createLiteral("Good afternoon", "en-GB");

        final IRI example1 = factory.createIRI("http://example.com/s1");
        final IRI greeting = factory.createIRI("http://example.com/greeting");

        dataset.add(null, example1, greeting, upper);

        // or as patterns
        assertTrue(closableFindAny(dataset.stream(null, null, null, upper)).isPresent());
        assertTrue(closableFindAny(dataset.stream(null, null, null, lower)).isPresent());
        assertTrue(closableFindAny(dataset.stream(null, null, null, mixed)).isPresent());

        // Check the quad returned equal a new quad
        final Quad q = closableFindAny(dataset.stream(null, null, null, lower)).get();
        assertEquals(q, factory.createQuad(null, example1, greeting, mixed));
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
    void testWhyJavaStreamsMightNotTakeOverFromSparql() throws Exception {
        assumeTrue(bnode1 != null && bnode2 != null && secretClubName != null);
        // Find a secret organizations
        try (Stream<? extends Quad> stream = dataset.stream(null, null, knows, null)) {
            assertEquals("\"The Secret Club\"",
                    // Find One-way "knows"
                    stream.filter(t -> !dataset.contains(null, (BlankNodeOrIRI) t.getObject(), knows, t.getSubject()))
                            .map(knowsQuad -> {
                                try (Stream<? extends Quad> memberOf = dataset
                                        // and those they know, what are they
                                        // member of?
                                        .stream(null, (BlankNodeOrIRI) knowsQuad.getObject(), member, null)) {
                                    return memberOf
                                            // keep those which first-guy is a
                                            // member of
                                            .filter(memberQuad -> dataset.contains(null, knowsQuad.getSubject(), member,
                                                    // First hit is good enough
                                                    memberQuad.getObject()))
                                            .findFirst().get().getObject();
                                }
                            })
                            // then look up the name of that org
                            .map(org -> {
                                try (Stream<? extends Quad> orgName = dataset.stream(null, (BlankNodeOrIRI) org, name,
                                        null)) {
                                    return orgName.findFirst().get().getObject().ntriplesString();
                                }
                            }).findFirst().get());
        }
    }
}
