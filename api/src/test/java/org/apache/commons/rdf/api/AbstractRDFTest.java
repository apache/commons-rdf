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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

/**
 * Test RDF implementation (and thus its RDFTerm implementations)
 * <p>
 * To add to your implementation's tests, create a subclass with a name ending
 * in <code>Test</code> and provide {@link #createFactory()} which minimally
 * supports one of the operations, but ideally supports all operations.
 *
 * @see RDF
 */
public abstract class AbstractRDFTest {

    private RDF factory;

    /**
     * 
     * This method must be overridden by the implementing test to provide a
     * factory for the test to create {@link Literal}, {@link IRI} etc.
     * 
     * @return {@link RDF} instance to be tested.
     */
    protected abstract RDF createFactory();

    @Before
    public void setUp() {
        factory = createFactory();
    }

    @Test
    public void testCreateBlankNode() throws Exception {
        final BlankNode bnode = factory.createBlankNode();

        final BlankNode bnode2 = factory.createBlankNode();
        assertNotEquals("Second blank node has not got a unique internal identifier", bnode.uniqueReference(),
                bnode2.uniqueReference());
    }

    @Test
    public void testCreateBlankNodeIdentifierEmpty() throws Exception {
        try {
            factory.createBlankNode("");
        } catch (final IllegalArgumentException e) {
            // Expected exception
        }
    }

    @Test
    public void testCreateBlankNodeIdentifier() throws Exception {
        factory.createBlankNode("example1");
    }

    @Test
    public void testCreateBlankNodeIdentifierTwice() throws Exception {
        BlankNode bnode1, bnode2, bnode3;
        bnode1 = factory.createBlankNode("example1");
        bnode2 = factory.createBlankNode("example1");
        bnode3 = factory.createBlankNode("differ");
        // We don't know what the identifier is, but it MUST be the same
        assertEquals(bnode1.uniqueReference(), bnode2.uniqueReference());
        // We don't know what the ntriplesString is, but it MUST be the same
        assertEquals(bnode1.ntriplesString(), bnode2.ntriplesString());
        // and here it MUST differ
        assertNotEquals(bnode1.uniqueReference(), bnode3.uniqueReference());
        assertNotEquals(bnode1.ntriplesString(), bnode3.ntriplesString());
    }

    @Test
    public void testCreateBlankNodeIdentifierTwiceDifferentFactories() throws Exception {
        BlankNode bnode1, differentFactory;
        bnode1 = factory.createBlankNode();
        // it MUST differ from a second factory
        differentFactory = createFactory().createBlankNode();

        // NOTE: We can't make similar assumption if we provide a
        // name to createBlankNode(String) as its documentation
        // only says:
        //
        // * BlankNodes created using this method with the same parameter, for
        // * different instances of RDFFactory, SHOULD NOT be equivalent.
        //
        // https://github.com/apache/incubator-commonsrdf/pull/7#issuecomment-92312779
        assertNotEquals(bnode1, differentFactory);
        assertNotEquals(bnode1.uniqueReference(), differentFactory.uniqueReference());
        // but we can't require:
        // assertNotEquals(bnode1.ntriplesString(),
        // differentFactory.ntriplesString());
    }

    @Test
    public void testCreateGraph() {
        final Graph graph = factory.createGraph();

        assertEquals("Graph was not empty", 0, graph.size());
        graph.add(factory.createBlankNode(), factory.createIRI("http://example.com/"), factory.createBlankNode());

        final Graph graph2 = factory.createGraph();
        assertNotSame(graph, graph2);
        assertEquals("Graph was empty after adding", 1, graph.size());
        assertEquals("New graph was not empty", 0, graph2.size());
    }

    @Test
    public void testCreateIRI() throws Exception {
        final IRI example = factory.createIRI("http://example.com/");

        assertEquals("http://example.com/", example.getIRIString());
        assertEquals("<http://example.com/>", example.ntriplesString());

        final IRI term = factory.createIRI("http://example.com/vocab#term");
        assertEquals("http://example.com/vocab#term", term.getIRIString());
        assertEquals("<http://example.com/vocab#term>", term.ntriplesString());

        // and now for the international fun!

        final IRI latin1 = factory.createIRI("http://accént.example.com/première");
        assertEquals("http://accént.example.com/première", latin1.getIRIString());
        assertEquals("<http://accént.example.com/première>", latin1.ntriplesString());

        final IRI cyrillic = factory.createIRI("http://example.испытание/Кириллица");
        assertEquals("http://example.испытание/Кириллица", cyrillic.getIRIString());
        assertEquals("<http://example.испытание/Кириллица>", cyrillic.ntriplesString());

        final IRI deseret = factory.createIRI("http://𐐀.example.com/𐐀");
        assertEquals("http://𐐀.example.com/𐐀", deseret.getIRIString());
        assertEquals("<http://𐐀.example.com/𐐀>", deseret.ntriplesString());
    }

    @Test
    public void testCreateLiteral() throws Exception {
        final Literal example = factory.createLiteral("Example");
        assertEquals("Example", example.getLexicalForm());
        assertFalse(example.getLanguageTag().isPresent());
        assertEquals("http://www.w3.org/2001/XMLSchema#string", example.getDatatype().getIRIString());
        // http://lists.w3.org/Archives/Public/public-rdf-comments/2014Dec/0004.html
        assertEquals("\"Example\"", example.ntriplesString());
    }

    @Test
    public void testCreateLiteralDateTime() throws Exception {
        final Literal dateTime = factory.createLiteral("2014-12-27T00:50:00T-0600",
                factory.createIRI("http://www.w3.org/2001/XMLSchema#dateTime"));
        assertEquals("2014-12-27T00:50:00T-0600", dateTime.getLexicalForm());
        assertFalse(dateTime.getLanguageTag().isPresent());
        assertEquals("http://www.w3.org/2001/XMLSchema#dateTime", dateTime.getDatatype().getIRIString());
        assertEquals("\"2014-12-27T00:50:00T-0600\"^^<http://www.w3.org/2001/XMLSchema#dateTime>",
                dateTime.ntriplesString());
    }

    @Test
    public void testCreateLiteralLang() throws Exception {
        final Literal example = factory.createLiteral("Example", "en");

        assertEquals("Example", example.getLexicalForm());
        assertEquals("en", example.getLanguageTag().get());
        assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString", example.getDatatype().getIRIString());
        assertEquals("\"Example\"@en", example.ntriplesString());
    }

    @Test
    public void testCreateLiteralLangISO693_3() throws Exception {
        // see https://issues.apache.org/jira/browse/JENA-827
        final Literal vls = factory.createLiteral("Herbert Van de Sompel", "vls"); // JENA-827

        assertEquals("vls", vls.getLanguageTag().get());
        assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString", vls.getDatatype().getIRIString());
        assertEquals("\"Herbert Van de Sompel\"@vls", vls.ntriplesString());
    }

    @Test
    public void testCreateLiteralString() throws Exception {
        final Literal example = factory.createLiteral("Example",
                factory.createIRI("http://www.w3.org/2001/XMLSchema#string"));
        assertEquals("Example", example.getLexicalForm());
        assertFalse(example.getLanguageTag().isPresent());
        assertEquals("http://www.w3.org/2001/XMLSchema#string", example.getDatatype().getIRIString());
        // http://lists.w3.org/Archives/Public/public-rdf-comments/2014Dec/0004.html
        assertEquals("\"Example\"", example.ntriplesString());
    }

    @Test
    public void testCreateTripleBnodeBnode() {
        final BlankNode subject = factory.createBlankNode("b1");
        final IRI predicate = factory.createIRI("http://example.com/pred");
        final BlankNode object = factory.createBlankNode("b2");
        final Triple triple = factory.createTriple(subject, predicate, object);

        // bnode equivalence should be OK as we used the same
        // factory and have not yet inserted Triple into a Graph
        assertEquals(subject, triple.getSubject());
        assertEquals(predicate, triple.getPredicate());
        assertEquals(object, triple.getObject());
    }

    @Test
    public void testCreateTripleBnodeIRI() {
        final BlankNode subject = factory.createBlankNode("b1");
        final IRI predicate = factory.createIRI("http://example.com/pred");
        final IRI object = factory.createIRI("http://example.com/obj");
        final Triple triple = factory.createTriple(subject, predicate, object);

        // bnode equivalence should be OK as we used the same
        // factory and have not yet inserted Triple into a Graph
        assertEquals(subject, triple.getSubject());
        assertEquals(predicate, triple.getPredicate());
        assertEquals(object, triple.getObject());
    }

    @Test
    public void testCreateTripleBnodeTriple() {
        final BlankNode subject = factory.createBlankNode();
        final IRI predicate = factory.createIRI("http://example.com/pred");
        final Literal object = factory.createLiteral("Example", "en");
        final Triple triple = factory.createTriple(subject, predicate, object);

        // bnode equivalence should be OK as we used the same
        // factory and have not yet inserted Triple into a Graph
        assertEquals(subject, triple.getSubject());
        assertEquals(predicate, triple.getPredicate());
        assertEquals(object, triple.getObject());
    }

    @Test
    public void testPossiblyInvalidBlankNode() throws Exception {
        BlankNode withColon;
        try {
            withColon = factory.createBlankNode("with:colon");
        } catch (final IllegalArgumentException ex) {
            // Good!
            return;
        }
        // Factory allows :colon, which is OK as long as it's not causing an
        // invalid ntriplesString
        assertFalse(withColon.ntriplesString().contains("with:colon"));

        // and creating it twice gets the same ntriplesString
        assertEquals(withColon.ntriplesString(), factory.createBlankNode("with:colon").ntriplesString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidIRI() throws Exception {
        factory.createIRI("<no_brackets>");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLiteralLang() throws Exception {
        factory.createLiteral("Example", "with space");
    }

    @Test(expected = Exception.class)
    public void testInvalidTriplePredicate() {
        final BlankNode subject = factory.createBlankNode("b1");
        final BlankNode predicate = factory.createBlankNode("b2");
        final BlankNode object = factory.createBlankNode("b3");
        factory.createTriple(subject, (IRI) predicate, object);
    }

    @Test
    public void hashCodeBlankNode() throws Exception {
        final BlankNode bnode1 = factory.createBlankNode();
        assertEquals(bnode1.uniqueReference().hashCode(), bnode1.hashCode());
    }

    @Test
    public void hashCodeIRI() throws Exception {
        final IRI iri = factory.createIRI("http://example.com/");
        assertEquals(iri.getIRIString().hashCode(), iri.hashCode());
    }

    @Test
    public void hashCodeLiteral() throws Exception {
        final Literal literal = factory.createLiteral("Hello");
        assertEquals(Objects.hash(literal.getLexicalForm(), literal.getDatatype(), literal.getLanguageTag()),
                literal.hashCode());
    }

    @Test
    public void hashCodeTriple() throws Exception {
        final IRI iri = factory.createIRI("http://example.com/");
        final Triple triple = factory.createTriple(iri, iri, iri);
        assertEquals(Objects.hash(iri, iri, iri), triple.hashCode());
    }
}
