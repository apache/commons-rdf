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
 * Construct Jena implementations of Commons RDF.
 * <p>
 * This class is deliberately an abstract class, as it is an internal helper
 * which <strong>may change</strong> in any minor version update; users should
 * instead use {@link JenaRDF}.
 * <p>
 * For the purpose of blank node identity, some of these methods require a
 * {@link UUID} to use as a salt. See {@link BlankNode#uniqueReference()} for
 * details.
 *
 */
public abstract class InternalJenaFactory {

    static {
        // http://jena.apache.org/documentation/notes/system-initialization.html
        JenaSystem.init();
    }


    public JenaBlankNode createBlankNode(final String id, final UUID salt) {
        return new JenaBlankNodeImpl(NodeFactory.createBlankNode(id), salt);
    }

    public JenaBlankNode createBlankNode(final UUID salt) {
        return new JenaBlankNodeImpl(NodeFactory.createBlankNode(), salt);
    }

    public JenaDataset createDataset(final DatasetGraph datasetGraph, final UUID salt) {
        return new JenaDatasetImpl(datasetGraph, salt);
    }

    public JenaDataset createDataset(final UUID salt) {
        final DatasetGraph dg = DatasetGraphFactory.createGeneral();
        // Or which createMethod() -- a bit confusing with lots of choice..
        return new JenaDatasetImpl(dg, salt);
    }

    public JenaGeneralizedQuadLike createGeneralizedQuad(final org.apache.jena.sparql.core.Quad quad, final UUID salt) {
        return new JenaGeneralizedQuadLikeImpl(quad, salt);
    }

    public JenaGeneralizedQuadLike createGeneralizedQuad(final RDFTerm subject, final RDFTerm predicate, final RDFTerm object,
            final RDFTerm graphName) {
        return new JenaGeneralizedQuadLikeImpl(subject, predicate, object, Optional.ofNullable(graphName));
    }

    public JenaGeneralizedTripleLike createGeneralizedTriple(final org.apache.jena.graph.Triple triple, final UUID salt) {
        return new JenaGeneralizedTripleLikeImpl(triple, salt);
    }

    public JenaGeneralizedTripleLike createGeneralizedTriple(final RDFTerm subject, final RDFTerm predicate, final RDFTerm object) {
        return new JenaGeneralizedTripleLikeImpl(subject, predicate, object);
    }

    public JenaGraph createGraph(final Model model, final UUID salt) {
        return new JenaGraphImpl(model, salt);
    }

    public JenaGraph createGraph(final org.apache.jena.graph.Graph graph, final UUID salt) {
        return new JenaGraphImpl(graph, salt);
    }

    public JenaGraph createGraph(final UUID salt) {
        return new JenaGraphImpl(GraphFactory.createDefaultGraph(), salt);
    }

    public JenaIRI createIRI(final String iriStr) {
        return new JenaIRIImpl(iriStr);
    }

    public JenaLiteral createLiteral(final String lexStr) {
        return new JenaLiteralImpl(NodeFactory.createLiteral(lexStr));
    }

    public JenaLiteral createLiteralDT(final String lexStr, final String datatypeIRI) {
        return new JenaLiteralImpl(NodeFactory.createLiteral(lexStr, NodeFactory.getType(datatypeIRI)));
    }

    public JenaLiteral createLiteralLang(final String lexStr, final String langTag) {
        return new JenaLiteralImpl(NodeFactory.createLiteral(lexStr, langTag));
    }

    public JenaQuad createQuad(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object, final BlankNodeOrIRI graphName) {
        return new JenaQuadImpl(subject, predicate, object, Optional.ofNullable(graphName));
    }

    public JenaQuad createQuad(final org.apache.jena.sparql.core.Quad quad, final UUID salt) {
        return new JenaQuadImpl(quad, salt);
    }

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

    public JenaTriple createTriple(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return new JenaTripleImpl(subject, predicate, object);
    }

    public JenaTriple createTriple(final org.apache.jena.graph.Triple triple, final UUID salt) {
        return new JenaTripleImpl(triple, salt);
    }

}
