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
package org.apache.commons.rdf.jsonldjava;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.simple.Types;

import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Node;

/**
 * JSON-LD Java RDF implementation.
 */
public final class JsonLdRDF implements RDF {

    final String bnodePrefix;

    public JsonLdRDF() {
        // An "outside Graph" bnodePrefix
        this("urn:uuid:" + UUID.randomUUID() + "#b");
    }

    JsonLdRDF(final String bnodePrefix) {
        this.bnodePrefix = Objects.requireNonNull(bnodePrefix);
    }

    /**
     * Adapt a JsonLd {@link RDFDataset} as a Commons RDF {@link Dataset}.
     * <p>
     * Changes to the Commons RDF {@link Dataset} are reflected in the JsonLd
     * {@link RDFDataset} and vice versa.
     *
     * @see #asGraph(RDFDataset)
     * @param rdfDataSet
     *            JsonLd {@link RDFDataset} to adapt
     * @return Adapted {@link Dataset}
     */
    public JsonLdDataset asDataset(final RDFDataset rdfDataSet) {
        return new JsonLdDatasetImpl(rdfDataSet);
    }

    /**
     * Adapt a JsonLd {@link RDFDataset} as a Commons RDF {@link Graph}.
     * <p>
     * Only triples in the <em>default graph</em> are included. To retrieve any
     * other graph, {@link #asDataset(RDFDataset)} together with
     * {@link Dataset#getGraph(BlankNodeOrIRI)}.
     * <p>
     * Changes to the Commons RDF {@link Graph} are reflected in the JsonLd
     * {@link RDFDataset} and vice versa.
     *
     * @see #asDataset(RDFDataset)
     * @see #asUnionGraph(RDFDataset)
     * @param rdfDataSet
     *            JsonLd {@link RDFDataset} to adapt
     * @return Adapted {@link Graph} covering the <em>default graph</em>
     */
    public JsonLdGraph asGraph(final RDFDataset rdfDataSet) {
        return new JsonLdGraphImpl(rdfDataSet);
    }

    public Node asJsonLdNode(final RDFTerm term) {
        if (term instanceof JsonLdBlankNode) {
            final JsonLdBlankNode jsonLdBlankNode = (JsonLdBlankNode) term;
            if (jsonLdBlankNode.uniqueReference().startsWith(bnodePrefix)) {
                // Only return blank nodes 'as is' if they have the same prefix
                return jsonLdBlankNode.asJsonLdNode();
            }
        } else if (term instanceof JsonLdTerm) {
            // non-Bnodes can always be return as-is
            return ((JsonLdTerm) term).asJsonLdNode();
        }
        if (term instanceof IRI) {
            return new RDFDataset.IRI(((IRI) term).getIRIString());
        }
        if (term instanceof BlankNode) {
            final String ref = ((BlankNode) term).uniqueReference();
            if (ref.startsWith(bnodePrefix)) {
                // one of our own (but no longer a JsonLdBlankNode),
                // we can recover the label after our unique prefix
                return new RDFDataset.BlankNode(ref.replace(bnodePrefix, ""));
            }
            // The "foreign" unique reference might not be a valid bnode string,
            // we'll convert to a UUID
            final UUID uuid = UUID.nameUUIDFromBytes(ref.getBytes(StandardCharsets.UTF_8));
            return new RDFDataset.BlankNode("_:" + uuid);
        }
        if (term instanceof Literal) {
            final Literal literal = (Literal) term;
            return new RDFDataset.Literal(literal.getLexicalForm(), literal.getDatatype().getIRIString(),
                    literal.getLanguageTag().orElse(null));
        }
        throw new IllegalArgumentException("RDFTerm not instanceof IRI, BlankNode or Literal: " + term);
    }

    /**
     * Adapt a Commons RDF {@link org.apache.commons.rdf.api.Quad} as a JsonLd
     * {@link com.github.jsonldjava.core.RDFDataset.Quad}.
     *
     * @param quad
     *            Commons RDF {@link org.apache.commons.rdf.api.Quad} to adapt
     * @return Adapted JsonLd {@link com.github.jsonldjava.core.RDFDataset.Quad}
     */
    public RDFDataset.Quad asJsonLdQuad(final org.apache.commons.rdf.api.Quad quad) {
        final BlankNodeOrIRI g = quad.getGraphName().orElse(null);
        return createJsonLdQuad(g, quad.getSubject(), quad.getPredicate(), quad.getObject());
    }

    /**
     * Adapt a Commons RDF {@link Triple} as a JsonLd
     * {@link com.github.jsonldjava.core.RDFDataset.Quad}.
     *
     * @param triple
     *            Commons RDF {@link Triple} to adapt
     * @return Adapted JsonLd {@link com.github.jsonldjava.core.RDFDataset.Quad}
     */
    public RDFDataset.Quad asJsonLdQuad(final Triple triple) {
        return createJsonLdQuad(null, triple.getSubject(), triple.getPredicate(), triple.getObject());
    }

    /**
     * Adapt a JsonLd {@link com.github.jsonldjava.core.RDFDataset.Quad} as a
     * Commons RDF {@link org.apache.commons.rdf.api.Quad}.
     * <p>
     * The underlying JsonLd quad can be retrieved with
     * {@link JsonLdQuad#asJsonLdQuad()}.
     *
     * @param quad
     *            A JsonLd {@link com.github.jsonldjava.core.RDFDataset.Quad} to
     *            adapt
     * @return Adapted {@link JsonLdQuad}
     */
    public JsonLdQuad asQuad(final RDFDataset.Quad quad) {
        return new JsonLdQuadImpl(quad, bnodePrefix);
    }

    /**
     * Adapt a JsonLd {@link Node} as a Commons RDF {@link RDFTerm}.
     * <p>
     * The underlying node can be retrieved with
     * {@link JsonLdTerm#asJsonLdNode()}.
     *
     * @param node
     *            A JsonLd {@link Node} to adapt
     * @return Adapted {@link JsonLdTerm}
     */
    public JsonLdTerm asRDFTerm(final Node node) {
        return asRDFTerm(node, bnodePrefix);
    }

    /**
     * Adapt a JsonLd {@link com.github.jsonldjava.core.RDFDataset.Quad} as a
     * Commons RDF {@link org.apache.commons.rdf.api.Triple}.
     * <p>
     * The underlying JsonLd quad can be retrieved with
     * {@link JsonLdTriple#asJsonLdQuad()}.
     *
     * @param quad
     *            A JsonLd {@link com.github.jsonldjava.core.RDFDataset.Quad} to
     *            adapt
     * @return Adapted {@link JsonLdTriple}
     */
    public JsonLdTriple asTriple(final RDFDataset.Quad quad) {
        return new JsonLdTripleImpl(quad, bnodePrefix);
    }

    /**
     * Adapt a JsonLd {@link RDFDataset} as a Commons RDF {@link Graph}.
     * <p>
     * The graph can be seen as a <em>union graph</em> as it will contains all
     * the triples across all the graphs of the underlying {@link RDFDataset}.
     * <p>
     * Note that some triple operations on a union graph can be inefficient as
     * they need to remove any duplicate triples across the graphs.
     * <p>
     * Changes to the Commons RDF {@link Graph} are reflected in the JsonLd
     * {@link RDFDataset} and vice versa. Triples removed from the graph are
     * removed from <strong>all</strong> graphs, while triples added are added
     * to the <em>default graph</em>.
     *
     * @param rdfDataSet
     *            JsonLd {@link RDFDataset} to adapt
     * @return Adapted {@link Dataset}
     */
    public JsonLdUnionGraph asUnionGraph(final RDFDataset rdfDataSet) {
        return new JsonLdUnionGraphImpl(rdfDataSet);
    }

    @Override
    public JsonLdBlankNode createBlankNode() {
        final String id = "_:" + UUID.randomUUID().toString();
        return new JsonLdBlankNodeImpl(new RDFDataset.BlankNode(id), bnodePrefix);
    }

    @Override
    public JsonLdBlankNode createBlankNode(final String name) {
        final String id = "_:" + name;
        // TODO: Check if name is valid JSON-LD BlankNode identifier
        return new JsonLdBlankNodeImpl(new RDFDataset.BlankNode(id), bnodePrefix);
    }

    @Override
    public JsonLdDataset createDataset() {
        return new JsonLdDatasetImpl(bnodePrefix);
    }

    @Override
    public JsonLdGraph createGraph() {
        return new JsonLdGraphImpl(bnodePrefix);
    }

    @Override
    public JsonLdIRI createIRI(final String iri) {
        return new JsonLdIRIImpl(iri);
    }

    @Override
    public JsonLdLiteral createLiteral(final String literal) {
        return new JsonLdLiteralImpl(new RDFDataset.Literal(literal, null, null));
    }

    @Override
    public JsonLdLiteral createLiteral(final String literal, final IRI dataType) {
        return new JsonLdLiteralImpl(new RDFDataset.Literal(literal, dataType.getIRIString(), null));
    }

    @Override
    public JsonLdLiteral createLiteral(final String literal, final String language) {
        return new JsonLdLiteralImpl(new RDFDataset.Literal(literal, Types.RDF_LANGSTRING.getIRIString(), language));
    }

    @Override
    public JsonLdQuad createQuad(final BlankNodeOrIRI graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object)
            throws IllegalArgumentException, UnsupportedOperationException {
        return new JsonLdQuadImpl(createJsonLdQuad(graphName, subject, predicate, object), bnodePrefix);
    }

    @Override
    public JsonLdTriple createTriple(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return new JsonLdTripleImpl(createJsonLdQuad(null, subject, predicate, object), bnodePrefix);
    }

    String asJsonLdString(final BlankNodeOrIRI blankNodeOrIRI) {
        if (blankNodeOrIRI == null) {
            return null;
        }
        if (blankNodeOrIRI instanceof IRI) {
            return ((IRI) blankNodeOrIRI).getIRIString();
        }
        if (!(blankNodeOrIRI instanceof BlankNode)) {
            throw new IllegalArgumentException("Expected a BlankNode or IRI, not: " + blankNodeOrIRI);
        }
        final BlankNode blankNode = (BlankNode) blankNodeOrIRI;
        final String ref = blankNode.uniqueReference();
        if (ref.startsWith(bnodePrefix)) {
            // One of ours (but possibly not a JsonLdBlankNode) -
            // we can use the suffix directly
            return ref.replace(bnodePrefix, "");
        }
        // Map to unique bnode identifier, e.g.
        // _:0dbd92ee-ab1a-45e7-bba2-7ade54f87ec5
        final UUID uuid = UUID.nameUUIDFromBytes(ref.getBytes(StandardCharsets.UTF_8));
        return "_:" + uuid;
    }

    JsonLdTerm asRDFTerm(final Node node, final String blankNodePrefix) {
        if (node == null) {
            return null; // e.g. default graph
        }
        if (node.isIRI()) {
            return new JsonLdIRIImpl(node);
        }
        if (node.isBlankNode()) {
            return new JsonLdBlankNodeImpl(node, blankNodePrefix);
        }
        if (!node.isLiteral()) {
            throw new IllegalArgumentException("Node is neither IRI, BlankNode nor Literal: " + node);
        }
        // TODO: Our own JsonLdLiteral
        if (node.getLanguage() != null) {
            return createLiteral(node.getValue(), node.getLanguage());
        }
        return createLiteral(node.getValue(), createIRI(node.getDatatype()));
    }

    RDFDataset.Quad createJsonLdQuad(final BlankNodeOrIRI graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        return new RDFDataset.Quad(asJsonLdNode(subject), asJsonLdNode(predicate), asJsonLdNode(object),
                asJsonLdString(graphName));
    }

}