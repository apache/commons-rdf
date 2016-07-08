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
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.impl.JenaFactory;
import org.apache.jena.datatypes.RDFDatatype;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFBase;
import org.apache.jena.sparql.graph.GraphFactory;

/**
 * RDFTermFactory with Jena-backed objects.
 * <p>
 * This factory can also convert existing objects from/to Jena with methods like
 * {@link #fromJena(org.apache.jena.graph.Graph)} and {@link #toJena(Graph)}.
 * 
 * @see RDFTermFactory
 */
public final class RDFTermFactoryJena implements RDFTermFactory {

	private UUID salt;

	public RDFTermFactoryJena() {
		this.salt = UUID.randomUUID();
	}

	public RDFTermFactoryJena(UUID salt) {
		this.salt = salt;
	}

	@Override
	public BlankNode createBlankNode() {
		return JenaFactory.createBlankNode(salt);
	}

	@Override
	public BlankNode createBlankNode(String name) {
		return JenaFactory.createBlankNode(name, salt);
	}

	@Override
	public Graph createGraph() {
		return JenaFactory.createGraph(salt);
	}

	@Override
	public IRI createIRI(String iri) {
		validateIRI(iri);
		return JenaFactory.createIRI(iri);
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

	@Override
	public Literal createLiteral(String lexicalForm) {
		return JenaFactory.createLiteral(lexicalForm);
	}

	@Override
	public Literal createLiteral(String lexicalForm, IRI dataType) {
		return JenaFactory.createLiteralDT(lexicalForm, dataType.getIRIString());
	}

	@Override
	public Literal createLiteral(String lexicalForm, String languageTag) {
		validateLang(languageTag);
		return JenaFactory.createLiteralLang(lexicalForm, languageTag);
	}

	private static void validateLang(String languageTag) {
		if (languageTag.contains(" "))
			throw new IllegalArgumentException("Invalid language tag: " + languageTag);
	}

	@Override
	public Triple createTriple(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return JenaFactory.createTriple(subject, predicate, object);
	}

	/**
	 * Convert a CommonsRDF RDFTerm to a Jena Node. If the RDFTerm was from Jena
	 * originally, return that original object, else create a copy using Jena
	 * objects.
	 */
	public static Node toJena(RDFTerm term) {
		if (term == null) {
			return null;
		}
		if (term instanceof JenaNode)
			// TODO: What if it's a BlankNodeImpl with
			// a different salt? Do we need to rewrite the
			// jena blanknode identifier?
			return ((JenaNode) term).asJenaNode();

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
		conversionError("Not a concrete RDF Term: " + term);
		return null;
	}

	/**
	 * Convert a CommonsRDF Triple to a Jena Triple. If the Triple was from Jena
	 * originally, return that original object else create a copy using Jena
	 * objects.
	 */
	public static org.apache.jena.graph.Triple toJena(Triple triple) {
		if (triple instanceof JenaTriple)
			return ((JenaTriple) triple).asJenaTriple();
		return new org.apache.jena.graph.Triple(toJena(triple.getSubject()), toJena(triple.getPredicate()),
				toJena(triple.getObject()));
	}

	/**
	 * Convert a CommonsRDF Graph to a Jena Graph. If the Graph was from Jena
	 * originally, return that original object else create a copy using Jena
	 * objects.
	 */
	public static org.apache.jena.graph.Graph toJena(Graph graph) {
		if (graph instanceof JenaGraph)
			return ((JenaGraph) graph).asJenaGraph();
		org.apache.jena.graph.Graph g = GraphFactory.createGraphMem();
		graph.stream().forEach(t -> g.add(toJena(t)));
		return g;
	}

	/**
	 * Adapt an existing Jena Node to CommonsRDF {@link RDFTerm}.
	 * 
	 * @param salt
	 */
	public static RDFTerm fromJena(Node node, UUID salt) {
		return JenaFactory.fromJena(node, salt);
	}

	/**
	 * Adapt an existing Jena Node to CommonsRDF {@link RDFTerm}.
	 * 
	 * @param salt
	 */
	public RDFTerm fromJena(Node node) {
		return JenaFactory.fromJena(node, salt);
	}

	/**
	 * Adapt an existing Jena Triple to CommonsRDF {@link Triple}.
	 * 
	 * @param triple
	 *            Jena triple
	 */
	public Triple fromJena(org.apache.jena.graph.Triple triple) {
		return JenaFactory.fromJena(triple, salt);
	}

	/**
	 * Adapt an existing Jena Triple to CommonsRDF {@link Triple}.
	 * 
	 * @param triple
	 *            Jena triple
	 * @param salt
	 *            A {@link UUID} salt for adapting any {@link BlankNode}s
	 */
	public static Triple fromJena(org.apache.jena.graph.Triple triple, UUID salt) {
		return JenaFactory.fromJena(triple, salt);
	}

	public Quad fromJena(org.apache.jena.sparql.core.Quad quad) {
		return JenaFactory.fromJena(quad, salt);
	}

	public static Quad fromJena(org.apache.jena.sparql.core.Quad quad, UUID salt) {
		return JenaFactory.fromJena(quad, salt);
	}

	/**
	 * Adapt an existring Jena Graph to CommonsRDF {@link Graph}. This does not
	 * take a copy. Changes to the CommonsRDF Graph are reflected in the jena
	 * graph.
	 */
	public static Graph fromJena(org.apache.jena.graph.Graph graph) {
		// NOTE: This generates a new UUID salt per graph
		return JenaFactory.fromJena(graph);
	}

	/**
	 * Convert from Jena {@link Node} to any RDFCommons implementation
	 * 
	 * @param salt
	 */
	public static RDFTerm fromJena(RDFTermFactory factory, Node node) {
		if (node == null) {
			return null;
		}
		if (factory instanceof RDFTermFactoryJena) {
			// No need to convert, just wrap
			return ((RDFTermFactoryJena) factory).fromJena(node);
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
			return factory.createBlankNode(node.getBlankNodeLabel());
		throw new ConversionException("Node is not a concrete RDF Term: " + node);
	}

	/**
	 * Convert from Jena {@link org.apache.jena.graph.Triple} to any RDFCommons
	 * implementation
	 */
	public static Triple fromJena(RDFTermFactory factory, org.apache.jena.graph.Triple triple) {
		if (factory instanceof RDFTermFactoryJena) {
			// No need to convert, just wrap
			return ((RDFTermFactoryJena) factory).fromJena(triple);
		}
		BlankNodeOrIRI subject = (BlankNodeOrIRI) (fromJena(factory, triple.getSubject()));
		IRI predicate = (IRI) (fromJena(factory, triple.getPredicate()));
		RDFTerm object = fromJena(factory, triple.getObject());
		return factory.createTriple(subject, predicate, object);
	}

	/**
	 * Convert from Jena to any RDFCommons implementation. This is a copy, even
	 * if the factory is a RDFTermFactoryJena. Use
	 * {@link #fromJena(org.apache.jena.graph.Graph)} for a wrapper.
	 */
	public static Graph fromJena(RDFTermFactory factory, org.apache.jena.graph.Graph graph) {
		if (factory instanceof RDFTermFactoryJena) {
			// No need to convert, just wrap
			return fromJena(graph);
		}

		Graph g = factory.createGraph();
		graph.find(Node.ANY, Node.ANY, Node.ANY).forEachRemaining(t -> {
			g.add(fromJena(factory, t));
		});
		return g;
	}

	/**
	 * Create a {@link StreamRDF} that inserts into any RDFCommons
	 * implementation of Graph
	 */
	public static StreamRDF streamJenaToCommonsRDF(RDFTermFactory factory, Consumer<Quad> consumer) {
		return new StreamRDFBase() {
			@Override
			public void quad(org.apache.jena.sparql.core.Quad quad) {
				consumer.accept(fromJena(factory, quad));
			}
		};
	}

	public static Quad fromJena(RDFTermFactory factory, org.apache.jena.sparql.core.Quad quad) {
		if (factory instanceof RDFTermFactoryJena) {
			// No need to convert, just wrap
			return ((RDFTermFactoryJena) factory).fromJena(quad);
		}
		BlankNodeOrIRI graphName = (BlankNodeOrIRI) (fromJena(factory, quad.getGraph()));
		BlankNodeOrIRI subject = (BlankNodeOrIRI) (fromJena(factory, quad.getSubject()));
		IRI predicate = (IRI) (fromJena(factory, quad.getPredicate()));
		RDFTerm object = fromJena(factory, quad.getObject());
		return factory.createQuad(graphName, subject, predicate, object);
	}

	public static void conversionError(String msg) {
		throw new ConversionException(msg);
	}

	public static Optional<Lang> rdfSyntaxToLang(RDFSyntax rdfSyntax) {
		return Optional.ofNullable(RDFLanguages.contentTypeToLang(rdfSyntax.mediaType));
	}

	public static Optional<RDFSyntax> langToRdfSyntax(Lang lang) {
		return RDFSyntax.byMediaType(lang.getContentType().getContentType());
	}

}
