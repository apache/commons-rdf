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
package example;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.QuadLike;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.api.TripleLike;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.apache.commons.rdf.simple.Types;
import org.junit.Before;
import org.junit.Test;

public class UserGuideTest {

    private RDF factory;

    @Before
    public void factory() {
        factory = new SimpleRDF();
    }

    @Test
    public void creating() throws Exception {
        BlankNode aliceBlankNode = factory.createBlankNode();
        IRI nameIri = factory.createIRI("http://example.com/name");
        Literal aliceLiteral = factory.createLiteral("Alice");
        Triple triple = factory.createTriple(aliceBlankNode, nameIri, aliceLiteral);

        System.out.println(aliceBlankNode.ntriplesString());
        System.out.println(nameIri.ntriplesString());
        System.out.println(aliceLiteral.ntriplesString());

    }

    @Test
    public void ntriples() throws Exception {
        IRI iri = factory.createIRI("http://example.com/alice");
        System.out.println(iri.getIRIString());

        IRI iri2 = factory.createIRI("http://example.com/alice");
        System.out.println(iri.equals(iri2));

        IRI iri3 = factory.createIRI("http://example.com/alice/./");
        System.out.println(iri.equals(iri3));

        System.out.println(iri.equals("http://example.com/alice"));
        System.out.println(iri.equals(factory.createLiteral("http://example.com/alice")));

    }

    @Test
    public void blanknode() throws Exception {
        BlankNode bnode = factory.createBlankNode();
        System.out.println(bnode.equals(bnode));
        System.out.println(bnode.equals(factory.createBlankNode()));

        BlankNode b1 = factory.createBlankNode("b1");

        System.out.println(b1.ntriplesString());

        System.out.println(b1.equals(factory.createBlankNode("b1")));
        System.out.println(b1.equals(new SimpleRDF().createBlankNode("b1")));

        System.out.println(bnode.uniqueReference());
    }

    @Test
    public void literal() throws Exception {
        Literal literal = factory.createLiteral("Hello world!");
        System.out.println(literal.ntriplesString());

        String lexical = literal.getLexicalForm();
        System.out.println(lexical);

        IRI datatype = literal.getDatatype();
        System.out.println(datatype.ntriplesString());

        IRI xsdDouble = factory.createIRI("http://www.w3.org/2001/XMLSchema#double");
        Literal literalDouble = factory.createLiteral("13.37", xsdDouble);
        System.out.println(literalDouble.ntriplesString());

        Literal literalDouble2 = factory.createLiteral("13.37", Types.XSD_DOUBLE);

        System.out.println(Types.XSD_STRING.equals(literal.getDatatype()));

        Literal inSpanish = factory.createLiteral("¡Hola, Mundo!", "es");
        System.out.println(inSpanish.ntriplesString());
        System.out.println(inSpanish.getLexicalForm());
        System.out.println(inSpanish.getDatatype().ntriplesString());

        Optional<String> tag = inSpanish.getLanguageTag();
        if (tag.isPresent()) {
            System.out.println(tag.get());
        }

        System.out.println(literal.getLanguageTag().isPresent());
        System.out.println(literalDouble.getLanguageTag().isPresent());
    }

    @Test
    public void triple() throws Exception {
        BlankNodeOrIRI subject = factory.createBlankNode();
        IRI predicate = factory.createIRI("http://example.com/says");
        RDFTerm object = factory.createLiteral("Hello");
        Triple triple = factory.createTriple(subject, predicate, object);

        BlankNodeOrIRI subj = triple.getSubject();
        System.out.println(subj.ntriplesString());

        IRI pred = triple.getPredicate();
        System.out.println(pred.getIRIString());

        RDFTerm obj = triple.getObject();
        System.out.println(obj.ntriplesString());

        if (subj instanceof IRI) {
            String s = ((IRI) subj).getIRIString();
            System.out.println(s);
        }
        // ..
        if (obj instanceof Literal) {
            IRI type = ((Literal) obj).getDatatype();
            System.out.println(type);
        }

        // Equal triples must have same s,p,o
        System.out.println(triple.equals(factory.createTriple(subj, pred, obj)));
    }

    @Test
    public void quad() throws Exception {
        BlankNodeOrIRI graph = factory.createIRI("http://example.com/graph");
        BlankNodeOrIRI subject = factory.createBlankNode();
        IRI predicate = factory.createIRI("http://example.com/says");
        RDFTerm object = factory.createLiteral("Hello");
        Quad quad = factory.createQuad(graph, subject, predicate, object);

        Optional<BlankNodeOrIRI> g = quad.getGraphName();
        if (g.isPresent()) {
            System.out.println(g.get().ntriplesString());
        }

        BlankNodeOrIRI subj = quad.getSubject();
        System.out.println(subj.ntriplesString());

        // null means default graph
        Quad otherQuad = factory.createQuad(null, subject, predicate, object);
        // Equal quads must have same g,s,p,o
        System.out.println(quad.equals(otherQuad));

        // all quads can be viewed as triples - "stripping" the graph
        Triple asTriple = quad.asTriple();
        Triple otherAsTriple = quad.asTriple();
        System.out.println(asTriple.equals(otherAsTriple));

        // NOTE: Quad does NOT extend Triple, however both Triple and Quad
        // extend TripleLike

        TripleLike a = quad;
        TripleLike b = quad.asTriple();
        // Unlike Triple and Quad, TripleLike does not mandate any .equals(),
        // it just provides common access to getSubject(), getPredicate(),
        // getObject()

        // TripleLike supports generalized RDF - therefore all s/p/o are of type
        // RDFTerm
        RDFTerm s = a.getSubject();
    }

    @Test
    public void graph() throws Exception {
        IRI nameIri = factory.createIRI("http://example.com/name");
        BlankNode aliceBlankNode = factory.createBlankNode();
        Literal aliceLiteral = factory.createLiteral("Alice");
        Triple triple = factory.createTriple(aliceBlankNode, nameIri, aliceLiteral);

        Graph graph = factory.createGraph();

        graph.add(triple);

        IRI bob = factory.createIRI("http://example.com/bob");
        Literal bobName = factory.createLiteral("Bob");
        graph.add(bob, nameIri, bobName);

        System.out.println(graph.contains(triple));

        System.out.println(graph.contains(null, nameIri, bobName));

        System.out.println(graph.size());

        for (Triple t : graph.iterate()) {
            System.out.println(t.getObject());
        }

        for (Triple t : graph.iterate(null, null, bobName)) {
            System.out.println(t.getPredicate());
        }

        try (Stream<? extends Triple> triples = graph.stream()) {
            Stream<RDFTerm> subjects = triples.map(t -> t.getObject());
            String s = subjects.map(RDFTerm::ntriplesString).collect(Collectors.joining(" "));
            System.out.println(s);
        }

        try (Stream<? extends Triple> named = graph.stream(null, nameIri, null)) {
            Stream<? extends Triple> namedB = named.filter(t -> t.getObject().ntriplesString().contains("B"));
            System.out.println(namedB.map(t -> t.getSubject()).findAny().get());
        }

        graph.remove(triple);
        System.out.println(graph.contains(triple));

        graph.remove(null, nameIri, null);

        graph.clear();
        System.out.println(graph.contains(null, null, null));

    }

    public static String tripleAsString(Triple t) {
        return t.getSubject().ntriplesString() + " " + t.getPredicate().ntriplesString() + " "
                + t.getObject().ntriplesString() + " .";
    }

    public static void writeGraph(Graph graph, Path graphFile) throws Exception {
        Stream<CharSequence> stream = graph.stream().map(UserGuideTest::tripleAsString);
        Files.write(graphFile, stream::iterator, StandardCharsets.UTF_8);
    }
}
