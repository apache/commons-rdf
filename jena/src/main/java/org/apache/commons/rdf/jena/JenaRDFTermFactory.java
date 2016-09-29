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
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.api.TripleLike;
import org.apache.commons.rdf.jena.impl.JenaFactory;
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
 * RDFTermFactory with Jena-backed objects.
 * <p>
 * This factory can also convert existing objects from/to Jena with methods like
 * {@link #fromJena(org.apache.jena.graph.Graph)} and {@link #toJena(Graph)}.
 * <p>
 * For the purpose of {@link BlankNode} identity, this factory will use an internal
 * {@link UUID} as a salt. See {@link BlankNode#uniqueReference()} for details.
 * 
 * @see RDFTermFactory
 */
public final class JenaRDFTermFactory implements RDFTermFactory {

	private final UUID salt;

	/**
	 * Create a JenaRDFTermFactory.
	 * <p>
	 * This constructor will use a randomly generated {@link UUID} as a salt 
	 * for the purposes of {@link BlankNode} identity, see {@link #getSalt()}.
	 */
	public JenaRDFTermFactory() {
		this.salt = UUID.randomUUID();
	}

	/**
	 * Create a JenaRDFTermFactory.
	 * <p>
	 * This constructor will use the specified {@link UUID} as a salt 
	 * for the purposes of {@link BlankNode} identity.
	 * 
	 * @param salt {@link UUID} to use as salt
	 */	
	public JenaRDFTermFactory(UUID salt) {
		this.salt = salt;
	}

	@Override
	public JenaBlankNode createBlankNode() {
		return JenaFactory.createBlankNode(getSalt());
	}

	@Override
	public JenaBlankNode createBlankNode(String name) {
		return JenaFactory.createBlankNode(name, getSalt());
	}
	
	@Override
	public JenaDataset createDataset() {
		return JenaFactory.createDataset(getSalt());
	}

	@Override
	public JenaGraph createGraph() {
		return JenaFactory.createGraph(getSalt());
	}

	@Override
	public JenaIRI createIRI(String iri) {
		validateIRI(iri);
		return JenaFactory.createIRI(iri);
	}

	@Override
	public JenaLiteral createLiteral(String lexicalForm) {
		return JenaFactory.createLiteral(lexicalForm);
	}

	@Override
	public JenaLiteral createLiteral(String lexicalForm, IRI dataType) {
		return JenaFactory.createLiteralDT(lexicalForm, dataType.getIRIString());
	}

	@Override
	public JenaLiteral createLiteral(String lexicalForm, String languageTag) {
		validateLang(languageTag);
		return JenaFactory.createLiteralLang(lexicalForm, languageTag);
	}

	@Override
	public JenaTriple createTriple(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return JenaFactory.createTriple(subject, predicate, object);
	}
	
	@Override
	public JenaQuad createQuad(BlankNodeOrIRI graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object)
			throws IllegalArgumentException, UnsupportedOperationException {
		return JenaFactory.createQuad(subject, predicate, object, graphName);
	}

	public JenaAny createAnyVariable() {
		return JenaFactory.createAnyVariable();
	}
	
	public JenaVariable createVariable(String variableName) {
		return JenaFactory.createVariable(variableName);
	}
	
	/**
	 * Create a generalized Jena triple.
	 * <p>
	 * The <em>generalized triple</em> supports any {@link RDFTerm} as its
	 * {@link TripleLike#getSubject()} {@link TripleLike#getPredicate()} or
	 * {@link TripleLike#getObject()}, including {@link JenaAny} or
	 * {@link JenaVariable}.
	 *
	 * @see #createTriple(BlankNodeOrIRI, IRI, RDFTerm)
	 * @see #createGeneralizedQuad(RDFTerm, RDFTerm, RDFTerm, RDFTerm)
	 * @see #createAnyVariable()
	 * @see #createVariable(String)
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
	public JenaTripleLike<RDFTerm, RDFTerm, RDFTerm> createGeneralizedTriple(
			RDFTerm subject, RDFTerm predicate, RDFTerm object) {
		return JenaFactory.createGeneralizedTriple(subject, predicate, object);
	}

	/**
	 * Create a generalized Jena quad.
	 * <p>
	 * The <em>generalized quad</em> supports any {@link RDFTerm} as its
	 * {@link QuadLike#getSubject()} {@link QuadLike#getPredicate()},
	 * {@link QuadLike#getObject()} or {@link QuadLike#getObject()} including
	 * {@link JenaAny} or {@link JenaVariable}.
	 * 
	 * @see #createQuad(BlankNodeOrIRI, BlankNodeOrIRI, IRI, RDFTerm)
	 * @see #createGeneralizedTriple(RDFTerm, RDFTerm, RDFTerm)
	 * @see #createAnyVariable()
	 * @see #createVariable(String)
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
	public JenaQuadLike<RDFTerm, RDFTerm, RDFTerm, RDFTerm> createGeneralizedQuad(
			RDFTerm subject, RDFTerm predicate, RDFTerm object, RDFTerm graphName) {
		return JenaFactory.createGeneralizedQuad(subject, predicate, object, graphName);
	}	
	/**
	 * Adapt an existing Jena Node to CommonsRDF {@link RDFTerm}.
	 * <p>
	 * If {@link Node#isLiteral()}, then the returned value is a {@link Literal}.
	 * If {@link Node#isURI()}, the returned value is a IRI. If {$@link Node#isBlank()},
	 * the returned value is a {@link BlankNode}, which will use a {@link UUID}
	 * salt from this {@link JenaRDFTermFactory} instance in combination with
	 * {@link Node#getBlankNodeId()} for the purpose of its
	 * {@link BlankNode#uniqueReference()}.
	 * 
	 * @see #fromJena(Node, UUID)
	 * @see #fromJena(RDFTermFactory, Node)
	 * 
	 * @param node
	 *            The Jena Node to adapt. It's {@link Node#isConcrete()} must be
	 *            <code>true</code>.
	 * @return Adapted {@link JenaRDFTerm}
	 * @throws ConversionException
	 *             if the node is not concrete.
	 */
	public JenaRDFTerm fromJena(Node node) throws ConversionException {
		return JenaFactory.fromJena(node, getSalt());
	}

	/**
	 * Adapt an existing Jena Node to CommonsRDF {@link RDFTerm}.
	 * <p>
	 * If {@link Node#isLiteral()}, then the returned value is a {@link Literal}.
	 * If {@link Node#isURI()}, the returned value is a IRI. If {@link Node#isBlank()},
	 * the returned value is a {@link BlankNode}, which will use the provided
	 * {@link UUID} salt in combination with {@link Node#getBlankNodeId()} for
	 * the purpose of its {@link BlankNode#uniqueReference()}.
	 * 
	 * @see #fromJena(Node)
	 * @see #fromJena(RDFTermFactory, Node)
	 * 
	 * @param node
	 *            The Jena Node to adapt. It's {@link Node#isConcrete()} must be
	 *            <code>true</code>.
	 * @param salt
	 *            UUID salt for the purpose of
	 *            {@link BlankNode#uniqueReference()}
	 * @return Adapted {@link JenaRDFTerm}
	 * @throws ConversionException
	 *             if the node is not concrete.
	 */
	public static JenaRDFTerm fromJena(Node node, UUID salt) {
		return JenaFactory.fromJena(node, salt);
	}
	
	/**
	 * Convert from Jena {@link Node} to any Commons RDF implementation.
	 * <p>
	 * Note that if the {@link Node#isBlank()}, then the factory's 
	 * {@link RDFTermFactory#createBlankNode(String)} will be used, meaning
	 * that care should be taken if reusing an {@link RDFTermFactory} instance
	 * for multiple conversion sessions.
	 * 
	 * @see #fromJena(Node)
	 * @see #fromJena(Node, UUID)
	 * 
	 * @param factory {@link RDFTermFactory} to use for creating {@link RDFTerm}.
	 * @param node
	 *            The Jena Node to adapt. It's {@link Node#isConcrete()} must be
	 *            <code>true</code>.
	 * @return Adapted {@link RDFTerm}            
	 * @throws ConversionException
	 *             if the node is not concrete.
	 */
	public static RDFTerm fromJena(RDFTermFactory factory, Node node) {
		if (node == null) {
			return null;
		}
		if (factory instanceof JenaRDFTermFactory) {
			// No need to convert, just wrap
			return ((JenaRDFTermFactory) factory).fromJena(node);
		}
		if (node.isURI())
			return factory.createIRI(node.getURI());
		if (node.isLiteral()) {
			String lang = node.getLiteralLanguage();
			if (lang != null && !lang.isEmpty())
				return factory.createLiteral(node.getLiteralLexicalForm(), lang);
			if (node.getLiteralDatatype().equals(XSDDatatype.XSDstring))
				return factory.createLiteral(node.getLiteralLexicalForm());
			IRI dt = factory.createIRI(node.getLiteralDatatype().getURI());
			return factory.createLiteral(node.getLiteralLexicalForm(), dt);
		}
		if (node.isBlank())
			// The factory
			return factory.createBlankNode(node.getBlankNodeLabel());
		throw new ConversionException("Node is not a concrete RDF Term: " + node);
	}	
	
	/**
	 * Adapt an existing Jena Triple to CommonsRDF {@link Triple}.
	 * <p>
	 * If the triple contains any {@link Node#isBlank()}, then any corresponding
	 * {@link BlankNode} will use a {@link UUID} salt from this
	 * {@link JenaRDFTermFactory} instance in combination with
	 * {@link Node#getBlankNodeId()} for the purpose of its
	 * {@link BlankNode#uniqueReference()}.
	 *
	 * @see #fromJena(org.apache.jena.graph.Triple, UUID)
	 * @see #fromJena(RDFTermFactory, org.apache.jena.graph.Triple)
	 * 
	 * @param triple
	 *            Jena {@link org.apache.jena.graph.Triple} to adapt
	 * @return Adapted {@link JenaTriple}
	 * @throws ConversionException
	 *             if any of the triple's nodes are not concrete or the triple
	 *             is a generalized triple
	 */
	public JenaTriple fromJena(org.apache.jena.graph.Triple triple) throws ConversionException {
		return JenaFactory.fromJena(triple, getSalt());
	}


	/**
	 * Adapt a generalized Jena Triple to a CommonsRDF {@link TripleLike}.
	 * <p>
	 * The generalized triple supports any {@link RDFTerm} as its {@link TripleLike#getSubject()}
	 * {@link TripleLike#getPredicate()} or {@link TripleLike#getObject()}. 
	 * <p>
	 * If the Jena triple contains any {@link Node#isBlank()}, then any corresponding
	 * {@link BlankNode} will use the provided {@link UUID} salt 
	 * in combination with
	 * {@link Node#getBlankNodeId()} for the purpose of its
	 * {@link BlankNode#uniqueReference()}.
	 *
	 * @see #fromJena(org.apache.jena.graph.Triple, UUID)
	 * @see #fromJena(RDFTermFactory, org.apache.jena.graph.Triple)
	 * 
	 * @param triple
	 *            Jena triple
	 * @param salt
	 *            UUID salt for the purpose of
	 *            {@link BlankNode#uniqueReference()}
	 * @return Adapted {@link TripleLike}. Note that the generalized triple does
	 *         <strong>not</strong> implement {@link Triple#equals(Object)} or
	 *         {@link Triple#hashCode()}.
	 * @throws ConversionException
	 *             if any of the triple's nodes are not concrete
	 */
	public JenaTripleLike<RDFTerm, RDFTerm, RDFTerm> fromJenaGeneralized(org.apache.jena.graph.Triple triple, UUID salt) throws ConversionException {
		return JenaFactory.fromJenaGeneralized(triple, salt);
	}
	
	/**
	 * Adapt a generalized Jena {@link org.apache.jena.graph.Triple} to a CommonsRDF {@link TripleLike}.
	 * <p>
	 * The generalized triple supports any {@link RDFTerm} as its {@link TripleLike#getSubject()}
	 * {@link TripleLike#getPredicate()} or {@link TripleLike#getObject()}, including 
	 * the extensions {@link JenaAny} and {@link JenaVariable}.
	 * <p>
	 * If the Jena triple contains any {@link Node#isBlank()}, then any corresponding
	 * {@link BlankNode} will use a {@link UUID} salt from this
	 * {@link JenaRDFTermFactory} instance in combination with
	 * {@link Node#getBlankNodeId()} for the purpose of its
	 * {@link BlankNode#uniqueReference()}.
	 *
	 * @see #fromJena(org.apache.jena.graph.Triple, UUID)
	 * @see #fromJena(RDFTermFactory, org.apache.jena.graph.Triple)
	 * 
	 * @param triple
	 *            Jena triple
	 * @return Adapted {@link TripleLike}. Note that the generalized triple does
	 *         <strong>not</strong> implement {@link Triple#equals(Object)} or
	 *         {@link Triple#hashCode()}.
	 * @throws ConversionException
	 *             if any of the triple's nodes are not concrete
	 */
	public JenaTripleLike<RDFTerm, RDFTerm, RDFTerm> fromJenaGeneralized(org.apache.jena.graph.Triple triple) throws ConversionException {
		return JenaFactory.fromJenaGeneralized(triple, getSalt());
	}

	/**
	 * Adapt a generalized Jena {@link org.apache.jena.sparql.core.Quad} to a CommonsRDF {@link QuadLike}.
	 * <p>
	 * The generalized quad supports any {@link RDFTerm} as its 
	 * {@link QuadLike#getGraphName()}, 
	 * {@link QuadLike#getSubject()}
	 * {@link QuadLike#getPredicate()} or 
	 * {@link QuadLike#getObject()}, including 
	 * the extensions 
	 * {@link JenaAny} and {@link JenaVariable}. 
	 * <p>
	 * If the Jena quad contains any {@link Node#isBlank()}, then any corresponding
	 * {@link BlankNode} will use a {@link UUID} salt from this
	 * {@link JenaRDFTermFactory} instance in combination with
	 * {@link Node#getBlankNodeId()} for the purpose of its
	 * {@link BlankNode#uniqueReference()}.
	 *
	 * @see #fromJena(org.apache.jena.sparql.core.Quad)
	 * @see #fromJenaGeneralized(org.apache.jena.graph.Triple)
	 * 
	 * @param quad
	 *            Jena quad
	 * @return Adapted {@link QuadLike}. Note that the generalized quad does
	 *         <strong>not</strong> implement {@link Quad#equals(Object)} or
	 *         {@link Quad#hashCode()}.
	 * @throws ConversionException
	 *             if any of the quad nodes are not concrete
	 */
	public JenaQuadLike<RDFTerm, RDFTerm, RDFTerm, RDFTerm> fromJenaGeneralized(org.apache.jena.sparql.core.Quad quad) throws ConversionException {
		return JenaFactory.fromJenaGeneralized(quad, getSalt());
	}
	
	
	/**
	 * Adapt an existing Jena Triple to CommonsRDF {@link Triple}.
	 * <p>
	 * If the triple contains any {@link Node#isBlank()}, then any corresponding
	 * {@link BlankNode} will use the provided a {@link UUID} salt in
	 * combination with {@link Node#getBlankNodeId()} for the purpose of its
	 * {@link BlankNode#uniqueReference()}.
	 * 
	 * @param triple
	 *            Jena triple
	 * @param salt
	 *            A {@link UUID} salt for adapting any {@link BlankNode}s
	 * @return Adapted triple
	 * @throws ConversionException
	 *             if any of the triple's nodes are not concrete or the triple
	 *             is a generalized triple
	 */
	public static JenaTriple fromJena(org.apache.jena.graph.Triple triple, UUID salt) throws ConversionException {
		return JenaFactory.fromJena(triple, salt);
	}

	/**
	 * Convert from Jena {@link org.apache.jena.graph.Triple} to a Commons RDF
	 * {@link Triple}.
	 * <p>
	 * Note that if any of the triple's nodes {@link Node#isBlank()}, then the factory's 
	 * {@link RDFTermFactory#createBlankNode(String)} will be used, meaning
	 * that care should be taken if reusing an {@link RDFTermFactory} instance
	 * for multiple conversion sessions.
	 * 
	 * @see #fromJena(org.apache.jena.graph.Triple)
	 * @see #fromJena(org.apache.jena.graph.Triple, UUID)
	 *
	 * @param factory {@link RDFTermFactory} to use for creating the {@link Triple} and its
	 * {@link RDFTerm}s.
	 * @param triple
	 *            Jena triple
	 * @return Converted triple
	 * @throws ConversionException
	 *             if any of the triple's nodes are not concrete or the triple
	 *             is a generalized triple
	 */
	public static Triple fromJena(RDFTermFactory factory, org.apache.jena.graph.Triple triple) 
			throws ConversionException{
		if (factory instanceof JenaRDFTermFactory) {
			// No need to convert, just wrap
			return ((JenaRDFTermFactory) factory).fromJena(triple);
		}
		final BlankNodeOrIRI subject;
		final IRI predicate;
		try {
			subject = (BlankNodeOrIRI) fromJena(factory, triple.getSubject());
			predicate = (IRI) fromJena(factory, triple.getPredicate());
		} catch (ClassCastException ex) {
			throw new ConversionException("Can't convert generalized triple: " + triple, ex);
		}
		RDFTerm object = fromJena(factory, triple.getObject());
		return factory.createTriple(subject, predicate, object);
	}

	/**
	 * Adapt an existing Jena {@link org.apache.jena.sparql.core.Quad} to CommonsRDF {@link Quad}.
	 * <p>
	 * If the quad contains any {@link Node#isBlank()}, then any corresponding
	 * {@link BlankNode} will use a {@link UUID} salt from this 
	 * {@link JenaRDFTermFactory} instance
	 * in combination with {@link Node#getBlankNodeId()} 
	 * for the purpose of its {@link BlankNode#uniqueReference()}.
	 * 
	 * @param quad
	 *            Jena quad
	 * @return Adapted quad
	 */	
	public JenaQuad fromJena(org.apache.jena.sparql.core.Quad quad) {
		return JenaFactory.fromJena(quad, getSalt());
	}
	
	/**
	 * Adapt an existing Jena {@link org.apache.jena.sparql.core.Quad} to CommonsRDF {@link Quad}.
	 * <p>
	 * If the quad contains any {@link Node#isBlank()}, then any corresponding
	 * {@link BlankNode} will use the provided {@link UUID} salt
	 * in combination with {@link Node#getBlankNodeId()} 
	 * for the purpose of its {@link BlankNode#uniqueReference()}.
	 * 
	 * @param quad
	 *            Jena quad
	 * @param salt
	 *            A {@link UUID} salt for adapting any {@link BlankNode}s
	 * @return Adapted quad
	 */		
	public static JenaQuad fromJena(org.apache.jena.sparql.core.Quad quad, UUID salt) {
		return JenaFactory.fromJena(quad, salt);
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
	 * {@link BlankNode} will use a {@link UUID} salt from this
	 * {@link JenaRDFTermFactory} instance in combination with
	 * {@link Node#getBlankNodeId()} for the purpose of its
	 * {@link BlankNode#uniqueReference()}.
	 * 
	 * @param graph
	 *            Jena {@link org.apache.jena.graph.Graph} to adapt
	 * @return Adapted {@link JenaGraph}
	 */
	public JenaGraph fromJena(org.apache.jena.graph.Graph graph) {
		return JenaFactory.fromJena(graph, getSalt());
	}

	/**
	 * Adapt an existing Jena {@link org.apache.jena.rdf.model.Model} to CommonsRDF {@link Graph}. 
	 * <p>
	 * This does not ake a copy, changes to the CommonsRDF Graph are reflected in the jena
	 * graph, which is accessible from {@link JenaGraph#asJenaGraph()}.
	 * <p>
	 * If the graph contains any {@link Node#isBlank()}, then any corresponding
	 * {@link BlankNode} will use a {@link UUID} salt from this 
	 * {@link JenaRDFTermFactory} instance
	 * in combination with {@link Node#getBlankNodeId()} 
	 * for the purpose of its {@link BlankNode#uniqueReference()}.
	 * @param model
	 *            Jena {@link org.apache.jena.rdf.model.Model} to adapt
	 * @return Adapted {@link JenaGraph}
	 */
	public JenaGraph fromJena(org.apache.jena.rdf.model.Model model) {
		return JenaFactory.fromJena(model, getSalt());
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
	 * {@link BlankNode} will use the provided {@link UUID} salt in combination
	 * with {@link Node#getBlankNodeId()} for the purpose of its
	 * {@link BlankNode#uniqueReference()}.
	 * 
	 * @param graph
	 *            Jena {@link org.apache.jena.graph.Graph} to adapt
	 * @param salt
	 *            A {@link UUID} salt for adapting any {@link BlankNode}s
	 * @return Adapted {@link JenaGraph}
	 */
	public static JenaGraph fromJena(org.apache.jena.graph.Graph graph, UUID salt) {
		return JenaFactory.fromJena(graph, salt);
	}

	/**
	 * Adapt an existing Jena {@link DatasetGraph} to CommonsRDF {@link Dataset}. 
	 * <p>
	 * This does not
	 * take a copy, changes to the CommonsRDF Dataset are reflected in the jena
	 * dataset graph, which is accessible from {@link JenaDataset#asJenaDatasetGraph()}.
	 * <p>
	 * If the dataset contains any {@link Node#isBlank()}, then any corresponding
	 * {@link BlankNode} will use a {@link UUID} salt from this 
	 * {@link JenaRDFTermFactory} instance
	 * in combination with {@link Node#getBlankNodeId()} 
	 * for the purpose of its {@link BlankNode#uniqueReference()}.
	 * 
	 * @param datasetGraph Jena {@link DatasetGraph} to adapt
	 * @return Adapted {@link JenaDataset} 
	 */
	public JenaDataset fromJena(DatasetGraph datasetGraph) {
		return JenaFactory.fromJena(datasetGraph, getSalt());
	}	
	
	/**
	 * Adapt an existing Jena {@link org.apache.jena.query.Dataset} to CommonsRDF {@link Dataset}. 
	 * <p>
	 * This does not
	 * take a copy, changes to the CommonsRDF Dataset are reflected in the jena
	 * dataset graph, which is accessible from {@link JenaDataset#asJenaDatasetGraph()}.
	 * <p>
	 * If the dataset contains any {@link Node#isBlank()}, then any corresponding
	 * {@link BlankNode} will use a {@link UUID} salt from this 
	 * {@link JenaRDFTermFactory} instance
	 * in combination with {@link Node#getBlankNodeId()} 
	 * for the purpose of its {@link BlankNode#uniqueReference()}.
	 * 
	 * @param datasetGraph Jena {@link org.apache.jena.query.Dataset} to adapt
	 * @return Adapted {@link JenaDataset} 
	 */
	public JenaDataset fromJena(org.apache.jena.query.Dataset datasetGraph) {
		return JenaFactory.fromJena(datasetGraph.asDatasetGraph(), getSalt());
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
	 * corresponding {@link BlankNode} will use the provided {@link UUID} salt
	 * in combination with {@link Node#getBlankNodeId()} for the purpose of its
	 * {@link BlankNode#uniqueReference()}.
	 * 
	 * @param datasetGraph
	 *            Jena {@link DatasetGraph} to adapt
	 * @param salt
	 *            A {@link UUID} salt for adapting any {@link BlankNode}s
	 * @return Adapted {@link JenaDataset}
	 */
	public static JenaDataset fromJena(DatasetGraph datasetGraph, UUID salt) {
		return JenaFactory.fromJena(datasetGraph, salt);
	}	
	
	/**
	 * Convert from Jena to any Commons RDF implementation. This is a copy, even
	 * if the factory is a JenaRDFTermFactory. Use
	 * {@link #fromJena(org.apache.jena.graph.Graph)} for a wrapper.
	 * 
	 * @param factory {@link RDFTermFactory} to use for creating {@link RDFTerm}s
	 * @param graph Jena {@link org.apache.jena.graph.Graph} to copy
	 * @return Converted Graph 
	 */
	public static Graph fromJena(RDFTermFactory factory, org.apache.jena.graph.Graph graph) {
		Graph g = factory.createGraph();
		graph.find(Node.ANY, Node.ANY, Node.ANY).forEachRemaining(t -> {
			g.add(fromJena(factory, t));
		});
		return g;
	}

	/**
	 * Convert from Jena {@link org.apache.jena.sparql.core.Quad} to a Commons
	 * RDF {@link Quad}.
	 * <p>
	 * Note that if any of the quad's nodes {@link Node#isBlank()}, then the
	 * factory's {@link RDFTermFactory#createBlankNode(String)} will be used,
	 * meaning that care should be taken if reusing an {@link RDFTermFactory}
	 * instance for multiple conversion sessions.
	 * 
	 * @see #fromJena(org.apache.jena.sparql.core.Quad)
	 * @see #fromJena(org.apache.jena.sparql.core.Quad, UUID)
	 * @see #fromJenaGeneralized(org.apache.jena.sparql.core.Quad)
	 *
	 * @param factory
	 *            {@link RDFTermFactory} to use for creating the {@link Triple}
	 *            and its {@link RDFTerm}s.
	 * @param quad
	 *            Jena {@link org.apache.jena.sparql.core.Quad} to adapt
	 * @return Converted {@link Quad}
	 * @throws ConversionException
	 *             if any of the quad's nodes are not concrete or the quad
	 *             is a generalized quad
	 */
	public static Quad fromJena(RDFTermFactory factory, org.apache.jena.sparql.core.Quad quad) {
		if (factory instanceof JenaRDFTermFactory) {
			// No need to convert, just wrap
			return ((JenaRDFTermFactory) factory).fromJena(quad);
		}
		BlankNodeOrIRI graphName = (BlankNodeOrIRI) (fromJena(factory, quad.getGraph()));
		BlankNodeOrIRI subject = (BlankNodeOrIRI) (fromJena(factory, quad.getSubject()));
		IRI predicate = (IRI) (fromJena(factory, quad.getPredicate()));
		RDFTerm object = fromJena(factory, quad.getObject());
		return factory.createQuad(graphName, subject, predicate, object);
	}

	/**
	 * Return {@link RDFSyntax} corresponding to a Jena {@link Lang}.
	 * 
	 * @param lang {@link Lang} to convert
	 * @return Matched {@link RDFSyntax}, otherwise {@link Optional#empty()}
	 */
	public static Optional<RDFSyntax> langToRdfSyntax(Lang lang) {
		return RDFSyntax.byMediaType(lang.getContentType().getContentType());
	}

	/**
	 * Return Jena {@link Lang} corresponding to a {@link RDFSyntax}.
	 * 
	 * @param rdfSyntax {@link RDFSyntax} to convert
	 * @return Matched {@link Lang}, otherwise {@link Optional#empty()}
	 */
	public static Optional<Lang> rdfSyntaxToLang(RDFSyntax rdfSyntax) {
		return Optional.ofNullable(RDFLanguages.contentTypeToLang(rdfSyntax.mediaType));
	}

	/**
	 * Create a {@link StreamRDF} instance that inserts the converted
	 * {@link Quad}s. into a the provided {@link Consumer}.
	 * <p>
	 * The returned {@link StreamRDF} can be used for instance with Jena's
	 * {@link RDFDataMgr#parse(StreamRDF, String)}.
	 * 
	 * @param factory
	 *            {@link RDFTermFactory} to use for creating {@link RDFTerm}s
	 *            and {@link Quad}s.
	 * @param consumer
	 *            A {@link Consumer} of {@link Quad}s
	 * @return A {@link StreamRDF} that will stream converted quads to the
	 *         consumer
	 */
	public static StreamRDF streamJenaToCommonsRDF(RDFTermFactory factory, Consumer<Quad> consumer) {
		return new StreamRDFBase() {
			@Override
			public void quad(org.apache.jena.sparql.core.Quad quad) {
				consumer.accept(fromJena(factory, quad));
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
	public StreamRDF streamJenaToGeneralizedTriple(Consumer<TripleLike<RDFTerm, RDFTerm, RDFTerm>> generalizedConsumer) {
		return new StreamRDFBase() {			
			@Override
			public void triple(org.apache.jena.graph.Triple triple) {
				generalizedConsumer.accept(fromJenaGeneralized(triple));
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
	public StreamRDF streamJenaToGeneralizedQuad(Consumer<QuadLike<RDFTerm, RDFTerm, RDFTerm, RDFTerm>> generalizedConsumer) {
		return new StreamRDFBase() {
			@Override
			public void quad(org.apache.jena.sparql.core.Quad quad) {
				generalizedConsumer.accept(fromJenaGeneralized(quad));
			}
		};
	}	
	
	/**
	 * Convert a CommonsRDF Graph to a Jena Graph. If the Graph was from Jena
	 * originally, return that original object else create a copy using Jena
	 * objects.
	 * 
	 * @param graph Commons RDF {@link Graph} to convert
	 * @return Converted Jena {@link org.apache.jena.graph.Graph}
	 */
	public static org.apache.jena.graph.Graph toJena(Graph graph) {
		if (graph instanceof JenaGraph)
			return ((JenaGraph) graph).asJenaGraph();
		org.apache.jena.graph.Graph g = GraphFactory.createGraphMem();
		graph.stream().forEach(t -> g.add(toJena(t)));
		return g;
	}

	/**
	 * Convert a CommonsRDF RDFTerm to a Jena Node. If the RDFTerm was from Jena
	 * originally, return that original object, else create a copy using Jena
	 * objects.
	 * 
	 * @param term Commons RDF {@link RDFTerm} to convert
	 * @return Converted Jena {@link Node}
	 */
	public static Node toJena(RDFTerm term) {
		if (term == null) {
			return null;
		}
		if (term instanceof JenaRDFTerm)
			// TODO: What if it's a JenaBlankNodeImpl with
			// a different salt? Do we need to rewrite the
			// jena blanknode identifier?
			return ((JenaRDFTerm) term).asJenaNode();

		if (term instanceof IRI)
			return NodeFactory.createURI(((IRI) term).getIRIString());

		if (term instanceof Literal) {
			Literal lit = (Literal) term;
			RDFDatatype dt = NodeFactory.getType(lit.getDatatype().getIRIString());
			String lang = lit.getLanguageTag().orElse("");
			return NodeFactory.createLiteral(lit.getLexicalForm(), lang, dt);
		}

		if (term instanceof BlankNode) {
			String id = ((BlankNode) term).uniqueReference();
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
	 * @param triple Commons RDF {@link Triple} to convert
	 * @return Converted Jena {@link org.apache.jena.graph.Triple}
	 */
	public static org.apache.jena.graph.Triple toJena(Triple triple) {
		if (triple instanceof JenaTriple)
			return ((JenaTriple) triple).asJenaTriple();
		return org.apache.jena.graph.Triple.create(
				toJena(triple.getSubject()), 
				toJena(triple.getPredicate()),
				toJena(triple.getObject()));
	}


	/**
	 * Convert a CommonsRDF {@link Quad} to a Jena
	 * {@link org.apache.jena.sparql.core.Quad}.
	 * <p>
	 * If the quad was from Jena originally, return that original object,
	 * otherwise create a copy using Jena objects.
	 *
	 * @param quad Commons RDF {@link Quad} to convert
	 * @return Converted Jena {@link org.apache.jena.sparql.core.Quad}
	 */
	public static org.apache.jena.sparql.core.Quad toJena(Quad quad) {
		if (quad instanceof JenaQuad) {
			return ((JenaQuad) quad).asJenaQuad();
		}
		return org.apache.jena.sparql.core.Quad.create(
				toJena(quad.getGraphName().orElse(null)),
				toJena(quad.getSubject()), 
				toJena(quad.getPredicate()), 
				toJena(quad.getObject()));
	}

	// Some simple validations - full IRI parsing is not cheap.
	private static void validateIRI(String iri) {
		if (iri.contains(" "))
			throw new IllegalArgumentException();
		if (iri.contains("<"))
			throw new IllegalArgumentException();
		if (iri.contains(">"))
			throw new IllegalArgumentException();
	}

	private static void validateLang(String languageTag) {
		if (languageTag.contains(" "))
			throw new IllegalArgumentException("Invalid language tag: " + languageTag);
	}

	/**
	 * Return the {@link UUID} salt used by this factory.
	 * <p>
	 * The salt is used for the purposes of {@link BlankNode} identity, see
	 * {@link BlankNode#uniqueReference()} for details.
	 * <p>
	 * This salt can be used with the constructor 
	 * {@link JenaRDFTermFactory#JenaRDFTermFactory(UUID)} or
	 * methods like {@link #fromJena(Node, UUID)} and 
	 * {@link #fromJena(org.apache.jena.graph.Triple, UUID)}
	 * to ensure consistent {@link BlankNode}s. 
	 * 
	 * @return The {@link UUID} used as salt
	 */
	public UUID getSalt() {
		return salt;
	}

}
