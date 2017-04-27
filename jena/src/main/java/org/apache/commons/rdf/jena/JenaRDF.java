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

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.QuadLike;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.api.TripleLike;
import org.apache.commons.rdf.jena.impl.InternalJenaFactory;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.graph.GraphFactory;

/**
 * Apache Jena RDF implementation.
 * <p>
 * Instances of JenaRDF can also convert existing objects from Jena with methods
 * like {@link #asRDFTerm(Node)} and
 * {@link #asGraph(org.apache.jena.graph.Graph)}, and vice versa from any
 * Commons RDF object to Jena with the <code>asJena*</code> methods like
 * {@link #asJenaNode(RDFTerm)} and {@link #asJenaGraph(Graph)}.
 * <p>
 * Note that Commons RDF objects created by this class implement the
 * specializations interfaces like {@link JenaRDFTerm}, {@link JenaGraph} and
 * {@link JenaTriple}, which provide access to the underlying Jena objects, e.g.
 * with {@link JenaRDFTerm#asJenaNode()}.
 * <p>
 * For the purpose of {@link BlankNode} identity when using
 * {@link #createBlankNode(String)} (see {@link BlankNode#equals(Object)} and
 * {@link BlankNode#uniqueReference()}), each instance of JenaRDF uses an
 * internal random state. If for some reason consistent/reproducible BlankNode
 * identity is desired, it is possible to retrieve the state as a UUID using
 * {@link #salt()} for subsequent use with {@link JenaRDF#JenaRDF(UUID)} - note
 * that such consistency is only guaranteed within the same minor version of
 * Commons RDF.
 * 
 * @see RDF
 */
public final class JenaRDF implements RDF {

    private static InternalJenaFactory internalJenaFactory = new InternalJenaFactory() {
    };

    private final UUID salt;

    /**
     * Create a JenaRDF.
     * <p>
     * This constructor will use a randomly generated {@link UUID} as a salt for
     * the purposes of {@link BlankNode} identity, see {@link #salt()}.
     */
    public JenaRDF() {
        this.salt = UUID.randomUUID();
    }

    /**
     * Create a JenaRDF.
     * <p>
     * This constructor will use the specified {@link UUID} as a salt for the
     * purposes of {@link BlankNode} identity, and should only be used in cases
     * where predictable and consistent {@link BlankNode#uniqueReference()} are
     * important.
     * 
     * @param salt
     *            {@link UUID} to use as salt for {@link BlankNode} equality
     */
    public JenaRDF(final UUID salt) {
        this.salt = salt;
    }

    @Override
    public JenaBlankNode createBlankNode() {
        return internalJenaFactory.createBlankNode(salt());
    }

    @Override
    public JenaBlankNode createBlankNode(final String name) {
        return internalJenaFactory.createBlankNode(name, salt());
    }

    @Override
    public JenaDataset createDataset() {
        return internalJenaFactory.createDataset(salt());
    }

    @Override
    public JenaGraph createGraph() {
        return internalJenaFactory.createGraph(salt());
    }

    @Override
    public JenaIRI createIRI(final String iri) {
        validateIRI(iri);
        return internalJenaFactory.createIRI(iri);
    }

    @Override
    public JenaLiteral createLiteral(final String lexicalForm) {
        return internalJenaFactory.createLiteral(lexicalForm);
    }

    @Override
    public JenaLiteral createLiteral(final String lexicalForm, final IRI dataType) {
        return internalJenaFactory.createLiteralDT(lexicalForm, dataType.getIRIString());
    }

    @Override
    public JenaLiteral createLiteral(final String lexicalForm, final String languageTag) {
        validateLang(languageTag);
        return internalJenaFactory.createLiteralLang(lexicalForm, languageTag);
    }

    @Override
    public JenaTriple createTriple(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return internalJenaFactory.createTriple(subject, predicate, object);
    }

    /**
     * {@inheritDoc}
     * <p>
     * In addition to supporting a <code>graphName</code> of <code>null</code>
     * for representing a triple in the <em>default graph</em>, this method also
     * recognize a {@link JenaIRI} which {@link JenaRDFTerm#asJenaNode()}
     * represent the default graph according to
     * {@link org.apache.jena.sparql.core.Quad#isDefaultGraph(Node)}, in which
     * case the returned JenaQuad will have a {@link Quad#getGraphName()} of
     * {@link Optional#empty()} rather than the provided IRI.
     * 
     */
    @Override
    public JenaQuad createQuad(final BlankNodeOrIRI graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object)
            throws IllegalArgumentException, UnsupportedOperationException {
        return internalJenaFactory.createQuad(subject, predicate, object, graphName);
    }

    /**
     * Create a generalized Jena triple.
     * <p>
     * The <em>generalized triple</em> supports any {@link RDFTerm} as its
     * {@link TripleLike#getSubject()} {@link TripleLike#getPredicate()} or
     * {@link TripleLike#getObject()}.
     *
     * @see #createTriple(BlankNodeOrIRI, IRI, RDFTerm)
     * @see #createGeneralizedQuad(RDFTerm, RDFTerm, RDFTerm, RDFTerm)
     * 
     * @param subject
     *            The subject of the statement
     * @param predicate
     *            The predicate of the statement
     * @param object
     *            The object of the statement
     * @return Generalized {@link TripleLike}. Note that the generalized triple
     *         does <strong>not</strong> implement {@link Triple#equals(Object)}
     *         or {@link Triple#hashCode()}.
     */
    public JenaGeneralizedTripleLike createGeneralizedTriple(final RDFTerm subject, final RDFTerm predicate, final RDFTerm object) {
        return internalJenaFactory.createGeneralizedTriple(subject, predicate, object);
    }

    /**
     * Create a generalized Jena quad.
     * <p>
     * The <em>generalized quad</em> supports any {@link RDFTerm} as its
     * {@link QuadLike#getSubject()} {@link QuadLike#getPredicate()},
     * {@link QuadLike#getObject()} or {@link QuadLike#getObject()}.
     * <p>
     * In addition to supporting a <code>graphName</code> of <code>null</code>
     * for representing a triple in the <em>default graph</em>, this method also
     * recognize a {@link JenaIRI} which {@link JenaRDFTerm#asJenaNode()}
     * represent the default graph according to
     * {@link org.apache.jena.sparql.core.Quad#isDefaultGraph(Node)}, in which
     * case the returned JenaQuad will have a {@link Quad#getGraphName()} of
     * {@link Optional#empty()} rather than the provided IRI.
     * 
     * @see #createQuad(BlankNodeOrIRI, BlankNodeOrIRI, IRI, RDFTerm)
     * @see #createGeneralizedTriple(RDFTerm, RDFTerm, RDFTerm)
     * 
     * @param subject
     *            The subject of the statement
     * @param predicate
     *            The predicate of the statement
     * @param object
     *            The object of the statement
     * @param graphName
     *            The graph name of the statement
     * @return Generalized {@link QuadLike}. Note that the generalized quad does
     *         <strong>not</strong> implement {@link Quad#equals(Object)} or
     *         {@link Quad#hashCode()}.
     */
    public JenaGeneralizedQuadLike createGeneralizedQuad(final RDFTerm subject, final RDFTerm predicate, final RDFTerm object,
            final RDFTerm graphName) {
        return internalJenaFactory.createGeneralizedQuad(subject, predicate, object, graphName);
    }

    /**
     * Adapt an existing Jena Node to CommonsRDF {@link RDFTerm}.
     * <p>
     * If {@link Node#isLiteral()}, then the returned value is a
     * {@link Literal}. If {@link Node#isURI()}, the returned value is a IRI. If
     * {$@link Node#isBlank()}, the returned value is a {@link BlankNode}, which
     * will use a {@link UUID} salt from this {@link JenaRDF} instance in
     * combination with {@link Node#getBlankNodeId()} for the purpose of its
     * {@link BlankNode#uniqueReference()}.
     * 
     * @see #asRDFTerm(RDF, Node)
     * 
     * @param node
     *            The Jena Node to adapt. It's {@link Node#isConcrete()} must be
     *            <code>true</code>.
     * @return Adapted {@link JenaRDFTerm}
     * @
     *             If the {@link Node} can't be represented as an
     *             {@link RDFTerm}, e.g. if the node is not concrete or
     *             represents a variable in Jena.
     */
    public JenaRDFTerm asRDFTerm(final Node node) {
        return internalJenaFactory.createRDFTerm(node, salt());
    }

    /**
     * Convert from Jena {@link Node} to any Commons RDF implementation.
     * <p>
     * Note that if the {@link Node#isBlank()}, then the factory's
     * {@link RDF#createBlankNode(String)} will be used, meaning that care
     * should be taken if reusing an {@link RDF} instance for multiple
     * conversion sessions.
     * 
     * @see #asRDFTerm(Node)
     * 
     * @param factory
     *            {@link RDF} to use for creating {@link RDFTerm}.
     * @param node
     *            The Jena Node to adapt. It's {@link Node#isConcrete()} must be
     *            <code>true</code>.
     * @return Adapted {@link RDFTerm}
     * @
     *             If the {@link Node} can't be represented as an
     *             {@link RDFTerm}, e.g. if the node is not concrete or
     *             represents a variable in Jena.
     */
    public static RDFTerm asRDFTerm(final RDF factory, final Node node) {
        if (node == null) {
            return null;
        }
        if (factory instanceof JenaRDF) {
            // No need to convert, just wrap
            return ((JenaRDF) factory).asRDFTerm(node);
        }
        if (node.isURI()) {
            return factory.createIRI(node.getURI());
        }
        if (node.isLiteral()) {
            final String lang = node.getLiteralLanguage();
            if (lang != null && !lang.isEmpty()) {
                return factory.createLiteral(node.getLiteralLexicalForm(), lang);
            }
            if (node.getLiteralDatatype().equals(XSDDatatype.XSDstring)) {
                return factory.createLiteral(node.getLiteralLexicalForm());
            }
            final IRI dt = factory.createIRI(node.getLiteralDatatype().getURI());
            return factory.createLiteral(node.getLiteralLexicalForm(), dt);
        }
        if (node.isBlank()) {
            // The factory
            return factory.createBlankNode(node.getBlankNodeLabel());
        }
        throw new ConversionException("Node is not a concrete RDF Term: " + node);
    }

    /**
     * Adapt an existing Jena Triple to CommonsRDF {@link Triple}.
     * <p>
     * If the triple contains any {@link Node#isBlank()}, then any corresponding
     * {@link BlankNode} will use a {@link UUID} salt from this {@link JenaRDF}
     * instance in combination with {@link Node#getBlankNodeId()} for the
     * purpose of its {@link BlankNode#uniqueReference()}.
     *
     * @see #asTriple(RDF, org.apache.jena.graph.Triple)
     * 
     * @param triple
     *            Jena {@link org.apache.jena.graph.Triple} to adapt
     * @return Adapted {@link JenaTriple}
     * @
     *             if any of the triple's nodes are not concrete or the triple
     *             is a generalized triple
     */
    public JenaTriple asTriple(final org.apache.jena.graph.Triple triple)  {
        return internalJenaFactory.createTriple(triple, salt());
    }

    /**
     * Adapt a generalized Jena {@link org.apache.jena.graph.Triple} to a
     * CommonsRDF {@link TripleLike}.
     * <p>
     * The generalized triple supports any {@link RDFTerm} as its
     * {@link TripleLike#getSubject()} {@link TripleLike#getPredicate()} or
     * {@link TripleLike#getObject()}.
     * <p>
     * If the Jena triple contains any {@link Node#isBlank()}, then any
     * corresponding {@link BlankNode} will use a {@link UUID} salt from this
     * {@link JenaRDF} instance in combination with
     * {@link Node#getBlankNodeId()} for the purpose of its
     * {@link BlankNode#uniqueReference()}.
     *
     * @see #asTriple(RDF, org.apache.jena.graph.Triple)
     * 
     * @param triple
     *            Jena triple
     * @return Adapted {@link TripleLike}. Note that the generalized triple does
     *         <strong>not</strong> implement {@link Triple#equals(Object)} or
     *         {@link Triple#hashCode()}.
     * @
     *             if any of the triple's nodes are not concrete
     */
    public JenaTripleLike asGeneralizedTriple(final org.apache.jena.graph.Triple triple) {
        return internalJenaFactory.createGeneralizedTriple(triple, salt());
    }

    /**
     * Adapt a generalized Jena {@link org.apache.jena.sparql.core.Quad} to a
     * CommonsRDF {@link QuadLike}.
     * <p>
     * The generalized quad supports any {@link RDFTerm} as its
     * {@link QuadLike#getGraphName()}, {@link QuadLike#getSubject()}
     * {@link QuadLike#getPredicate()} or {@link QuadLike#getObject()}.
     * <p>
     * If the Jena quad contains any {@link Node#isBlank()}, then any
     * corresponding {@link BlankNode} will use a {@link UUID} salt from this
     * {@link JenaRDF} instance in combination with
     * {@link Node#getBlankNodeId()} for the purpose of its
     * {@link BlankNode#uniqueReference()}.
     * <p>
     * If the provided quad {@link org.apache.jena.sparql.core.Quad#isDefaultGraph()},
     * the returned {@link JenaQuadLike} has a {@link JenaQuadLike#getGraphName()} 
     * of {@link Optional#empty()}.
     *
     * @see #asQuad(org.apache.jena.sparql.core.Quad)
     * @see #asGeneralizedTriple(org.apache.jena.graph.Triple)
     * 
     * @param quad
     *            Jena quad
     * @return Adapted {@link QuadLike}. Note that the generalized quad does
     *         <strong>not</strong> implement {@link Quad#equals(Object)} or
     *         {@link Quad#hashCode()}.
     * @
     *             if any of the quad nodes are not concrete
     */
    public JenaQuadLike<RDFTerm> asGeneralizedQuad(final org.apache.jena.sparql.core.Quad quad) {
        return internalJenaFactory.createGeneralizedQuad(quad, salt());
    }

    /**
     * Convert from Jena {@link org.apache.jena.graph.Triple} to a Commons RDF
     * {@link Triple}.
     * <p>
     * Note that if any of the triple's nodes {@link Node#isBlank()}, then the
     * factory's {@link RDF#createBlankNode(String)} will be used, meaning that
     * care should be taken if reusing an {@link RDF} instance for multiple
     * conversion sessions.
     * 
     * @see #asTriple(org.apache.jena.graph.Triple)
     *
     * @param factory
     *            {@link RDF} to use for creating the {@link Triple} and its
     *            {@link RDFTerm}s.
     * @param triple
     *            Jena triple
     * @return Converted triple
     * @
     *             if any of the triple's nodes are not concrete or the triple
     *             is a generalized triple
     */
    public static Triple asTriple(final RDF factory, final org.apache.jena.graph.Triple triple)  {
        if (factory instanceof JenaRDF) {
            // No need to convert, just wrap
            return ((JenaRDF) factory).asTriple(triple);
        }
        final BlankNodeOrIRI subject;
        final IRI predicate;
        try {
            subject = (BlankNodeOrIRI) asRDFTerm(factory, triple.getSubject());
            predicate = (IRI) asRDFTerm(factory, triple.getPredicate());
        } catch (final ClassCastException ex) {
            throw new ConversionException("Can't convert generalized triple: " + triple, ex);
        }
        final RDFTerm object = asRDFTerm(factory, triple.getObject());
        return factory.createTriple(subject, predicate, object);
    }

    /**
     * Adapt an existing Jena {@link org.apache.jena.sparql.core.Quad} to
     * CommonsRDF {@link Quad}.
     * <p>
     * If the quad contains any {@link Node#isBlank()}, then any corresponding
     * {@link BlankNode} will use a {@link UUID} salt from this {@link JenaRDF}
     * instance in combination with {@link Node#getBlankNodeId()} for the
     * purpose of its {@link BlankNode#uniqueReference()}.
     * <p>
     * If the provided quad {@link org.apache.jena.sparql.core.Quad#isDefaultGraph()},
     * the returned {@link JenaQuad} has a {@link Quad#getGraphName()} 
     * of {@link Optional#empty()}.
     * 
     * @param quad
     *            Jena quad
     * @return Adapted quad
     */
    public JenaQuad asQuad(final org.apache.jena.sparql.core.Quad quad) {
        return internalJenaFactory.createQuad(quad, salt());
    }

    /**
     * Adapt an existing Jena {@link org.apache.jena.graph.Graph} to CommonsRDF
     * {@link Graph}.
     * <p>
     * This does not take a copy, changes to the CommonsRDF Graph are reflected
     * in the jena graph, which is accessible from
     * {@link JenaGraph#asJenaGraph()}.
     * <p>
     * If the graph contains any {@link Node#isBlank()}, then any corresponding
     * {@link BlankNode} will use a {@link UUID} salt from this {@link JenaRDF}
     * instance in combination with {@link Node#getBlankNodeId()} for the
     * purpose of its {@link BlankNode#uniqueReference()}.
     * 
     * @param graph
     *            Jena {@link org.apache.jena.graph.Graph} to adapt
     * @return Adapted {@link JenaGraph}
     */
    public JenaGraph asGraph(final org.apache.jena.graph.Graph graph) {
        return internalJenaFactory.createGraph(graph, salt());
    }

    /**
     * Adapt an existing Jena {@link org.apache.jena.rdf.model.Model} to
     * CommonsRDF {@link Graph}.
     * <p>
     * This does not ake a copy, changes to the CommonsRDF Graph are reflected
     * in the jena graph, which is accessible from
     * {@link JenaGraph#asJenaGraph()}.
     * <p>
     * If the graph contains any {@link Node#isBlank()}, then any corresponding
     * {@link BlankNode} will use a {@link UUID} salt from this {@link JenaRDF}
     * instance in combination with {@link Node#getBlankNodeId()} for the
     * purpose of its {@link BlankNode#uniqueReference()}.
     * 
     * @param model
     *            Jena {@link org.apache.jena.rdf.model.Model} to adapt
     * @return Adapted {@link JenaGraph}
     */
    public JenaGraph asGraph(final org.apache.jena.rdf.model.Model model) {
        return internalJenaFactory.createGraph(model, salt());
    }

    /**
     * Adapt an existing Jena {@link DatasetGraph} to CommonsRDF
     * {@link Dataset}.
     * <p>
     * This does not take a copy, changes to the CommonsRDF Dataset are
     * reflected in the jena dataset graph, which is accessible from
     * {@link JenaDataset#asJenaDatasetGraph()}.
     * <p>
     * If the dataset contains any {@link Node#isBlank()}, then any
     * corresponding {@link BlankNode} will use a {@link UUID} salt from this
     * {@link JenaRDF} instance in combination with
     * {@link Node#getBlankNodeId()} for the purpose of its
     * {@link BlankNode#uniqueReference()}.
     * 
     * @param datasetGraph
     *            Jena {@link DatasetGraph} to adapt
     * @return Adapted {@link JenaDataset}
     */
    public JenaDataset asDataset(final DatasetGraph datasetGraph) {
        return internalJenaFactory.createDataset(datasetGraph, salt());
    }

    /**
     * Adapt an existing Jena {@link org.apache.jena.query.Dataset} to
     * CommonsRDF {@link Dataset}.
     * <p>
     * This does not take a copy, changes to the CommonsRDF Dataset are
     * reflected in the jena dataset graph, which is accessible from
     * {@link JenaDataset#asJenaDatasetGraph()}.
     * <p>
     * If the dataset contains any {@link Node#isBlank()}, then any
     * corresponding {@link BlankNode} will use a {@link UUID} salt from this
     * {@link JenaRDF} instance in combination with
     * {@link Node#getBlankNodeId()} for the purpose of its
     * {@link BlankNode#uniqueReference()}.
     * 
     * @param datasetGraph
     *            Jena {@link org.apache.jena.query.Dataset} to adapt
     * @return Adapted {@link JenaDataset}
     */
    public JenaDataset asDataset(final org.apache.jena.query.Dataset datasetGraph) {
        return internalJenaFactory.createDataset(datasetGraph.asDatasetGraph(), salt());
    }

    /**
     * Convert from Jena {@link org.apache.jena.sparql.core.Quad} to a Commons
     * RDF {@link Quad}.
     * <p>
     * Note that if any of the quad's nodes {@link Node#isBlank()}, then the
     * factory's {@link RDF#createBlankNode(String)} will be used, meaning that
     * care should be taken if reusing an {@link RDF} instance for multiple
     * conversion sessions.
     * <p>
     * If the provided quad {@link org.apache.jena.sparql.core.Quad#isDefaultGraph()},
     * the returned {@link JenaQuadLike} has a {@link JenaQuadLike#getGraphName()} 
     * of {@link Optional#empty()}.
     * 
     * @see #asQuad(org.apache.jena.sparql.core.Quad)
     * @see #asGeneralizedQuad(org.apache.jena.sparql.core.Quad)
     *
     * @param factory
     *            {@link RDF} to use for creating the {@link Triple} and its
     *            {@link RDFTerm}s.
     * @param quad
     *            Jena {@link org.apache.jena.sparql.core.Quad} to adapt
     * @return Converted {@link Quad}
     * @
     *             if any of the quad's nodes are not concrete or the quad is a
     *             generalized quad
     */
    public static Quad asQuad(final RDF factory, final org.apache.jena.sparql.core.Quad quad) {
        if (factory instanceof JenaRDF) {
            // No need to convert, just wrap
            return ((JenaRDF) factory).asQuad(quad);
        }
        final BlankNodeOrIRI graphName = (BlankNodeOrIRI) (asRDFTerm(factory, quad.getGraph()));
        final BlankNodeOrIRI subject = (BlankNodeOrIRI) (asRDFTerm(factory, quad.getSubject()));
        final IRI predicate = (IRI) (asRDFTerm(factory, quad.getPredicate()));
        final RDFTerm object = asRDFTerm(factory, quad.getObject());
        return factory.createQuad(graphName, subject, predicate, object);
    }

    /**
     * Return {@link RDFSyntax} corresponding to a Jena {@link Lang}.
     * 
     * @param lang
     *            {@link Lang} to convert
     * @return Matched {@link RDFSyntax}, otherwise {@link Optional#empty()}
     */
    public Optional<RDFSyntax> asRDFSyntax(final Lang lang) {
        return RDFSyntax.byMediaType(lang.getContentType().getContentType());
    }

    /**
     * Return Jena {@link Lang} corresponding to a {@link RDFSyntax}.
     * 
     * @param rdfSyntax
     *            {@link RDFSyntax} to convert
     * @return Matched {@link Lang}, otherwise {@link Optional#empty()}
     */
    public Optional<Lang> asJenaLang(final RDFSyntax rdfSyntax) {
        return Optional.ofNullable(RDFLanguages.contentTypeToLang(rdfSyntax.getmediaType()));
    }

    /**
     * Create a {@link StreamRDF} instance that inserts the converted
     * {@link Quad}s. into a the provided {@link Consumer}.
     * <p>
     * The returned {@link StreamRDF} can be used for instance with Jena's
     * {@link RDFDataMgr#parse(StreamRDF, String)}.
     * 
     * @param factory
     *            {@link RDF} to use for creating {@link RDFTerm}s and
     *            {@link Quad}s.
     * @param consumer
     *            A {@link Consumer} of {@link Quad}s
     * @return A {@link StreamRDF} that will stream converted quads to the
     *         consumer
     */
    public static StreamRDF streamJenaToQuad(final RDF factory, final Consumer<Quad> consumer) {
        return new StreamRDFBase() {
            @Override
            public void quad(final org.apache.jena.sparql.core.Quad quad) {
                consumer.accept(asQuad(factory, quad));
            }
        };
    }

    /**
     * Create a {@link StreamRDF} instance that inserts generalized
     * {@link TripleLike}s. into a the provided {@link Consumer}.
     * <p>
     * A generalized triple allows any {@link RDFTerm} for
     * {@link TripleLike#getSubject()}, {@link TripleLike#getPredicate()} and
     * {@link TripleLike#getObject()}.
     * <p>
     * The returned {@link StreamRDF} can be used for instance with Jena's
     * {@link RDFDataMgr#parse(StreamRDF, String)}.
     * 
     * @param generalizedConsumer
     *            A {@link Consumer} of generalized {@link TripleLike}s
     * @return A {@link StreamRDF} that will stream generalized triples to the
     *         consumer
     */
    public StreamRDF streamJenaToGeneralizedTriple(final Consumer<TripleLike> generalizedConsumer) {
        return new StreamRDFBase() {
            @Override
            public void triple(final org.apache.jena.graph.Triple triple) {
                generalizedConsumer.accept(asGeneralizedTriple(triple));
            }
        };
    }

    /**
     * Create a {@link StreamRDF} instance that inserts generalized
     * {@link QuadLike}s. into a the provided {@link Consumer}.
     * <p>
     * A generalized quad allows any {@link RDFTerm} for
     * {@link QuadLike#getSubject()}, {@link TripleLike#getPredicate()},
     * {@link QuadLike#getObject()} and {@link QuadLike#getGraphName()} .
     * <p>
     * The returned {@link StreamRDF} can be used for instance with Jena's
     * {@link RDFDataMgr#parse(StreamRDF, String)}.
     * 
     * @param generalizedConsumer
     *            A {@link Consumer} of generalized {@link QuadLike}s
     * @return A {@link StreamRDF} that will stream generalized quads to the
     *         consumer
     */
    public StreamRDF streamJenaToGeneralizedQuad(final Consumer<QuadLike<RDFTerm>> generalizedConsumer) {
        return new StreamRDFBase() {
            @Override
            public void quad(final org.apache.jena.sparql.core.Quad quad) {
                generalizedConsumer.accept(asGeneralizedQuad(quad));
            }
        };
    }

    /**
     * Convert a CommonsRDF Graph to a Jena Graph. If the Graph was from Jena
     * originally, return that original object else create a copy using Jena
     * objects.
     * 
     * @param graph
     *            Commons RDF {@link Graph} to convert
     * @return Converted Jena {@link org.apache.jena.graph.Graph}
     */
    public org.apache.jena.graph.Graph asJenaGraph(final Graph graph) {
        if (graph instanceof JenaGraph) {
            return ((JenaGraph) graph).asJenaGraph();
        }
        final org.apache.jena.graph.Graph g = GraphFactory.createGraphMem();
        graph.stream().forEach(t -> g.add(asJenaTriple(t)));
        return g;
    }

    /**
     * Convert a CommonsRDF RDFTerm to a Jena Node. If the RDFTerm was from Jena
     * originally, return that original object, else create a copy using Jena
     * objects.
     * 
     * @param term
     *            Commons RDF {@link RDFTerm} to convert
     * @return Converted Jena {@link Node}
     */
    public Node asJenaNode(final RDFTerm term) {
        if (term == null) {
            return null;
        }
        if (term instanceof JenaRDFTerm) {
            // TODO: What if it's a JenaBlankNodeImpl with
            // a different salt? Do we need to rewrite the
            // jena blanknode identifier?
            return ((JenaRDFTerm) term).asJenaNode();
        }

        if (term instanceof IRI) {
            return NodeFactory.createURI(((IRI) term).getIRIString());
        }

        if (term instanceof Literal) {
            final Literal lit = (Literal) term;
            final RDFDatatype dt = NodeFactory.getType(lit.getDatatype().getIRIString());
            final String lang = lit.getLanguageTag().orElse("");
            return NodeFactory.createLiteral(lit.getLexicalForm(), lang, dt);
        }

        if (term instanceof BlankNode) {
            final String id = ((BlankNode) term).uniqueReference();
            return NodeFactory.createBlankNode(id);
        }
        throw new ConversionException("Not a concrete RDF Term: " + term);
    }

    /**
     * Convert a CommonsRDF {@link Triple} to a Jena
     * {@link org.apache.jena.graph.Triple}.
     * <p>
     * If the triple was from Jena originally, return that original object, else
     * create a copy using Jena objects.
     * 
     * @param triple
     *            Commons RDF {@link Triple} to convert
     * @return Converted Jena {@link org.apache.jena.graph.Triple}
     */
    public org.apache.jena.graph.Triple asJenaTriple(final Triple triple) {
        if (triple instanceof JenaTriple) {
            return ((JenaTriple) triple).asJenaTriple();
        }
        return org.apache.jena.graph.Triple.create(asJenaNode(triple.getSubject()), 
                asJenaNode(triple.getPredicate()),
                asJenaNode(triple.getObject()));
    }

    /**
     * Convert a CommonsRDF {@link Quad} to a Jena
     * {@link org.apache.jena.sparql.core.Quad}.
     * <p>
     * If the quad was from Jena originally, return that original object,
     * otherwise create a copy using Jena objects.
     *
     * @param quad
     *            Commons RDF {@link Quad} to convert
     * @return Converted Jena {@link org.apache.jena.sparql.core.Quad}
     */
    public org.apache.jena.sparql.core.Quad asJenaQuad(final Quad quad) {
        if (quad instanceof JenaQuad) {
            return ((JenaQuad) quad).asJenaQuad();
        }
        return org.apache.jena.sparql.core.Quad.create(
                asJenaNode(quad.getGraphName().orElse(null)),
                asJenaNode(quad.getSubject()), 
                asJenaNode(quad.getPredicate()),
                asJenaNode(quad.getObject()));
    }

    // Some simple validations - full IRI parsing is not cheap.
    private void validateIRI(final String iri) {
        if (iri.contains(" ")) {
            throw new IllegalArgumentException();
        }
        if (iri.contains("<")) {
            throw new IllegalArgumentException();
        }
        if (iri.contains(">")) {
            throw new IllegalArgumentException();
        }
    }

    private static void validateLang(final String languageTag) {
        if (languageTag.contains(" ")) {
            throw new IllegalArgumentException("Invalid language tag: " + languageTag);
        }
    }

    /**
     * Return the {@link UUID} salt used by this factory.
     * <p>
     * The salt is used for the purposes of {@link BlankNode} identity, see
     * {@link BlankNode#uniqueReference()} for details.
     * <p>
     * This salt can be used with the constructor {@link JenaRDF#JenaRDF(UUID)}
     * if consistent or reproducible {@link BlankNode}s are desirable.
     * 
     * @return The {@link UUID} used as salt
     */
    public UUID salt() {
        return salt;
    }

}
