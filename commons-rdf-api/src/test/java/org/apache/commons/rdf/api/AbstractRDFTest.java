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

import static org.junit.Assert.*;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.junit.Assume;
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
    public void testCreateGraph() throws Exception {
        try (final Graph graph = factory.createGraph(); final Graph graph2 = factory.createGraph()) {

            assertEquals("Graph was not empty", 0, graph.size());
            graph.add(factory.createBlankNode(), factory.createIRI("http://example.com/"), factory.createBlankNode());

            assertNotSame(graph, graph2);
            assertEquals("Graph was empty after adding", 1, graph.size());
            assertEquals("New graph was not empty", 0, graph2.size());
        }
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
        // make sure this file is edited/compiled as UTF-8
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


    private void assertEqualsBothWays(final Object a, final Object b) {
        assertEquals(a, b);
        assertEquals(b, a);
        // hashCode must match as well
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void testCreateLiteralLangCaseInsensitive() throws Exception {
        /*
         * COMMONSRDF-51: Literal langtag may not be in lowercase, but must be
         * COMPARED (aka .equals and .hashCode()) in lowercase as the language
         * space is lower case.
         */
        final Literal upper = factory.createLiteral("Hello", "EN-GB");
        final Literal lower = factory.createLiteral("Hello", "en-gb");
        final Literal mixed = factory.createLiteral("Hello", "en-GB");

        /*
         * Disabled as some RDF frameworks (namely RDF4J) can be c configured to
         * do BCP47 normalization (e.g. "en-GB"), so we can't guarantee
         * lowercase language tags are preserved.
         */
        // assertEquals("en-gb", lower.getLanguageTag().get());

        /*
         * NOTE: the RDF framework is free to lowercase the language tag or
         * leave it as-is, so we can't assume:
         */
        // assertEquals("en-gb", upper.getLanguageTag().get());
        // assertEquals("en-gb", mixed.getLanguageTag().get());

        /* ..unless we do a case-insensitive comparison: */
        assertEquals("en-gb",
                lower.getLanguageTag().get().toLowerCase(Locale.ROOT));
        assertEquals("en-gb",
                upper.getLanguageTag().get().toLowerCase(Locale.ROOT));
        assertEquals("en-gb",
                mixed.getLanguageTag().get().toLowerCase(Locale.ROOT));

        // However these should all be true using .equals
        assertEquals(lower, lower);
        assertEqualsBothWays(lower, upper);
        assertEqualsBothWays(lower, mixed);
        assertEquals(upper, upper);
        assertEqualsBothWays(upper, mixed);
        assertEquals(mixed, mixed);
        // Note that assertEqualsBothWays also checks
        // that .hashCode() matches
    }

    @Test
    public void testCreateLiteralLangCaseInsensitiveOther() throws Exception {
        // COMMONSRDF-51: Ensure the Literal is using case insensitive
        // comparison against any literal implementation
        // which may not have done .toLowerString() in their constructor
        final Literal upper = factory.createLiteral("Hello", "EN-GB");
        final Literal lower = factory.createLiteral("Hello", "en-gb");
        final Literal mixed = factory.createLiteral("Hello", "en-GB");

        final Literal otherLiteral = new Literal() {
            @Override
            public String ntriplesString() {
                return "Hello@eN-Gb";
            }
            @Override
            public String getLexicalForm() {
                return "Hello";
            }
            @Override
            public Optional<String> getLanguageTag() {
                return Optional.of("eN-Gb");
            }
            @Override
            public IRI getDatatype() {
                return factory.createIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString");
            }
            @Override
            public boolean equals(final Object obj) {
                throw new RuntimeException("Wrong way comparison of literal");
            }
        };

        // NOTE: Our fake Literal can't do .equals() or .hashCode(),
        // so don't check the wrong way around!
        assertEquals(mixed, otherLiteral);
        assertEquals(lower, otherLiteral);
        assertEquals(upper, otherLiteral);
    }

    @Test
    public void testCreateLiteralLangCaseInsensitiveInTurkish() throws Exception {
        // COMMONSRDF-51: Special test for Turkish issue where
        // "i".toLowerCase() != "i"
        // See also:
        // https://garygregory.wordpress.com/2015/11/03/java-lowercase-conversion-turkey/
        final Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.ROOT);
            final Literal mixedROOT = factory.createLiteral("moi", "fI");
            final Literal lowerROOT = factory.createLiteral("moi", "fi");
            final Literal upperROOT = factory.createLiteral("moi", "FI");

            final Locale turkish = Locale.forLanguageTag("TR");
            Locale.setDefault(turkish);
            // If the below assertion fails, then the Turkish
            // locale no longer have this peculiarity that
            // we want to test.
            Assume.assumeFalse("FI".toLowerCase().equals("fi"));

            final Literal mixed = factory.createLiteral("moi", "fI");
            final Literal lower = factory.createLiteral("moi", "fi");
            final Literal upper = factory.createLiteral("moi", "FI");

            assertEquals(lower, lower);
            assertEqualsBothWays(lower, upper);
            assertEqualsBothWays(lower, mixed);

            assertEquals(upper, upper);
            assertEqualsBothWays(upper, mixed);

            assertEquals(mixed, mixed);

            // And our instance created previously in ROOT locale
            // should still be equal to the instance created in TR locale
            // (e.g. test constructor is not doing a naive .toLowerCase())
            assertEqualsBothWays(lower, lowerROOT);
            assertEqualsBothWays(upper, lowerROOT);
            assertEqualsBothWays(mixed, lowerROOT);

            assertEqualsBothWays(lower, upperROOT);
            assertEqualsBothWays(upper, upperROOT);
            assertEqualsBothWays(mixed, upperROOT);

            assertEqualsBothWays(lower, mixedROOT);
            assertEqualsBothWays(upper, mixedROOT);
            assertEqualsBothWays(mixed, mixedROOT);
        } finally {
            Locale.setDefault(defaultLocale);
        }
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
