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

package org.apache.commons.rdf.jena.impl;

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.jena.ConversionException;
import org.apache.commons.rdf.jena.JenaBlankNode;
import org.apache.commons.rdf.jena.JenaDataset;
import org.apache.commons.rdf.jena.JenaGeneralizedQuadLike;
import org.apache.commons.rdf.jena.JenaGeneralizedTripleLike;
import org.apache.commons.rdf.jena.JenaGraph;
import org.apache.commons.rdf.jena.JenaIRI;
import org.apache.commons.rdf.jena.JenaLiteral;
import org.apache.commons.rdf.jena.JenaQuad;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.commons.rdf.jena.JenaRDFTerm;
import org.apache.commons.rdf.jena.JenaTriple;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.system.JenaSystem;

/**
 * Constructs Jena implementations of Commons RDF.
 * <p>
 * This class is deliberately an abstract class, as it is an internal helper which <strong>may change</strong> in any minor version update; users should instead
 * use {@link JenaRDF}.
 * <p>
 * For the purpose of blank node identity, some of these methods require a {@link UUID} to use as a salt. See {@link BlankNode#uniqueReference()} for details.
 */
public abstract class InternalJenaFactory {

    static {
        // https://jena.apache.org/documentation/notes/system-initialization.html
        JenaSystem.init();
    }

    /**
     * Creates a blank node based on the given ID and name.
     *
     * @param id   A non-empty, non-null, String that is unique to this blank node in the context of this {@link RDF}.
     * @param salt A UUID salt.
     * @return A blank node based on the given ID and name.
     */
    public JenaBlankNode createBlankNode(final String id, final UUID salt) {
        return new JenaBlankNodeImpl(NodeFactory.createBlankNode(id), salt);
    }

    /**
     * Creates a blank node based on the given ID.
     *
     * @param salt A UUID salt.
     * @return A blank node based on the given ID and name.
     */
    public JenaBlankNode createBlankNode(final UUID salt) {
        return new JenaBlankNodeImpl(NodeFactory.createBlankNode(), salt);
    }

    /**
     * Creates a dataset based on the given graph and salt.
     *
     * @param datasetGraph dataset to wrap.
     * @param salt         a UUID salt.
     * @return A dataset based on the given graph and salt.
     */
    public JenaDataset createDataset(final DatasetGraph datasetGraph, final UUID salt) {
        return new JenaDatasetImpl(datasetGraph, salt);
    }

    /**
     * Creates a dataset based on the given graph and salt.
     *
     * @param salt A UUID salt.
     * @return A dataset based on the given graph and salt.
     */
    public JenaDataset createDataset(final UUID salt) {
        final DatasetGraph dg = DatasetGraphFactory.createGeneral();
        // Or which createMethod() -- a bit confusing with lots of choice.
        return new JenaDatasetImpl(dg, salt);
    }

    /**
     * Creates a generalized quad representation for the given quad and salt.
     *
     * @param quad The quad to wrap.
     * @param salt A UUID salt.
     * @return A generalized quad representation for the given quad and salt.
     */
    public JenaGeneralizedQuadLike createGeneralizedQuad(final org.apache.jena.sparql.core.Quad quad, final UUID salt) {
        return new JenaGeneralizedQuadLikeImpl(quad, salt);
    }

    /**
     * Creates a generalized quad representation for the given inputs.
     *
     * @param subject   An RDF subject.
     * @param predicate An RDF predicate.
     * @param object    An RDF object.
     * @param graphName An RDF graph name.
     * @return A generalized quad representation for the given inputs.
     */
    public JenaGeneralizedQuadLike createGeneralizedQuad(final RDFTerm subject, final RDFTerm predicate, final RDFTerm object, final RDFTerm graphName) {
        return new JenaGeneralizedQuadLikeImpl(subject, predicate, object, Optional.ofNullable(graphName));
    }

    /**
     * Creates a generalized triple representation for the given inputs.
     *
     * @param triple A triple.
     * @param salt   a UUID salt.
     * @return A generalized triple representation for the given inputs.
     */
    public JenaGeneralizedTripleLike createGeneralizedTriple(final org.apache.jena.graph.Triple triple, final UUID salt) {
        return new JenaGeneralizedTripleLikeImpl(triple, salt);
    }

    /**
     * Creates a generalized triple representation for the given inputs.
     *
     * @param subject   An RDF subject.
     * @param predicate An RDF predicate.
     * @param object    An RDF object.
     * @return A generalized triple representation for the given inputs.
     */
    public JenaGeneralizedTripleLike createGeneralizedTriple(final RDFTerm subject, final RDFTerm predicate, final RDFTerm object) {
        return new JenaGeneralizedTripleLikeImpl(subject, predicate, object);
    }

    /**
     * Creates a Jena-backed {@link org.apache.commons.rdf.api.Graph}.
     *
     * @param model A model.
     * @param salt A UUID salt.
     * @return A Jena-backed {@link org.apache.commons.rdf.api.Graph}.
     */
    public JenaGraph createGraph(final Model model, final UUID salt) {
        return new JenaGraphImpl(model, salt);
    }

    /**
     * Creates a Jena-backed {@link org.apache.commons.rdf.api.Graph}.
     *
     * @param graph A graph.
     * @param salt A UUID salt.
     * @return A Jena-backed {@link org.apache.commons.rdf.api.Graph}.
     */
    public JenaGraph createGraph(final org.apache.jena.graph.Graph graph, final UUID salt) {
        return new JenaGraphImpl(graph, salt);
    }

    /**
     * Creates a Jena-backed {@link org.apache.commons.rdf.api.Graph}.
     *
     * @param salt A UUID salt.
     * @return A Jena-backed {@link org.apache.commons.rdf.api.Graph}.
     */
    public JenaGraph createGraph(final UUID salt) {
        return new JenaGraphImpl(GraphFactory.createDefaultGraph(), salt);
    }

    /**
     * Creates a Jena-backed {@link IRI}.
     *
     * @param iriStr IRI string.
     * @return A Jena-backed {@link IRI}.
     */
    public JenaIRI createIRI(final String iriStr) {
        return new JenaIRIImpl(iriStr);
    }

    /**
     * Creates a Jena-backed {@link Literal}.
     *
     * @param lexStr a literal string.
     * @return A Jena-backed {@link Literal}.
     */
    public JenaLiteral createLiteral(final String lexStr) {
        return new JenaLiteralImpl(NodeFactory.createLiteral(lexStr));
    }

    /**
     * Creates a Jena-backed {@link Literal}.
     *
     * @param lexStr a literal string.
     * @param datatypeIRI datatype IRI string.
     * @return A Jena-backed {@link Literal}.
     */
    public JenaLiteral createLiteralDT(final String lexStr, final String datatypeIRI) {
        return new JenaLiteralImpl(NodeFactory.createLiteral(lexStr, NodeFactory.getType(datatypeIRI)));
    }

    /**
     * Creates a Jena-backed {@link Literal}.
     *
     * @param lexStr a literal string.
     * @param langTag A language tag.
     * @return A Jena-backed {@link Literal}.
     */
    public JenaLiteral createLiteralLang(final String lexStr, final String langTag) {
        return new JenaLiteralImpl(NodeFactory.createLiteral(lexStr, langTag));
    }

    /**
     * Creates a Jena-backed {@link org.apache.commons.rdf.api.Quad}.
     *
     * @param subject A subject as a blank node or IRI.
     * @param predicate A predicate IRI.
     * @param object An RDF object.
     * @param graphName A graph name as a blank node or IRI.
     * @return A Jena-backed {@link org.apache.commons.rdf.api.Quad}.
     */
    public JenaQuad createQuad(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object, final BlankNodeOrIRI graphName) {
        return new JenaQuadImpl(subject, predicate, object, Optional.ofNullable(graphName));
    }

    /**
     * Creates a Jena-backed {@link org.apache.commons.rdf.api.Quad}.
     *
     * @param quad A quad.
     * @param salt A UUID salt.
     * @return A Jena-backed {@link org.apache.commons.rdf.api.Quad}.
     */
    public JenaQuad createQuad(final org.apache.jena.sparql.core.Quad quad, final UUID salt) {
        return new JenaQuadImpl(quad, salt);
    }

    /**
     * Creates a Jena-backed {@link RDFTerm}.
     *
     * @param node A node.
     * @param salt A UUID salt.
     * @return A Jena-backed {@link RDFTerm}.
     * @throws ConversionException Thrown for an unrecognized node type.
     */
    public JenaRDFTerm createRDFTerm(final Node node, final UUID salt) throws ConversionException {
        if (!node.isConcrete()) {
            throw new ConversionException("Node is not a concrete RDF Term: " + node);
        }
        if (node.isURI()) {
            return new JenaIRIImpl(node);
        }
        if (node.isLiteral()) {
            return new JenaLiteralImpl(node);
        }
        if (node.isBlank()) {
            return new JenaBlankNodeImpl(node, salt);
        }
        if (node.equals(Node.ANY)) {
            // NOTE: JenaAny no longer supported by Commons RDF
            // return JenaAnyImpl.Singleton.instance;
        }
        if (node.isVariable()) {
            // NOTE: JenaVariable no longer supported by Commons RDF
            // return new JenaVariableImpl(node);
        }
        throw new ConversionException("Unrecognized node type: " + node);
    }

    /**
     * Creates a Jena-backed {@link org.apache.commons.rdf.api.Triple}.
     *
     * @param subject A subject as a blank node or IRI.
     * @param predicate A predicate IRI.
     * @param object An RDF object.
     * @return A Jena-backed {@link org.apache.commons.rdf.api.Triple}.
     */
    public JenaTriple createTriple(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return new JenaTripleImpl(subject, predicate, object);
    }

    /**
     * Creates a Jena-backed {@link org.apache.commons.rdf.api.Triple}.
     *
     * @param triple A triple.
     * @param salt A UUID salt.
     * @return A Jena-backed {@link org.apache.commons.rdf.api.Triple}.
     */
    public JenaTriple createTriple(final org.apache.jena.graph.Triple triple, final UUID salt) {
        return new JenaTripleImpl(triple, salt);
    }
}
