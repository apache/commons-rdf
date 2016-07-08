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
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.jena.ConversionException;
import org.apache.commons.rdf.jena.JenaAny;
import org.apache.commons.rdf.jena.JenaBlankNode;
import org.apache.commons.rdf.jena.JenaGraph;
import org.apache.commons.rdf.jena.JenaIRI;
import org.apache.commons.rdf.jena.JenaLiteral;
import org.apache.commons.rdf.jena.JenaQuad;
import org.apache.commons.rdf.jena.JenaQuadLike;
import org.apache.commons.rdf.jena.JenaRDFTerm;
import org.apache.commons.rdf.jena.JenaTriple;
import org.apache.commons.rdf.jena.JenaTripleLike;
import org.apache.commons.rdf.jena.JenaVariable;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.graph.GraphFactory;

public class JenaFactory {
	public static BlankNode createBlankNode() {
		return new BlankNodeImpl(NodeFactory.createBlankNode(), UUID.randomUUID());
	}

	public static JenaBlankNode createBlankNode(String id, UUID salt) {
		return new BlankNodeImpl(NodeFactory.createBlankNode(id), salt);
	}

	public static JenaBlankNode createBlankNode(UUID salt) {
		return new BlankNodeImpl(NodeFactory.createBlankNode(), salt);
	}

	public static JenaGraph createGraph() {
		return createGraph(UUID.randomUUID());
	}

	public static JenaGraph createGraph(UUID salt) {
		return new GraphImpl(GraphFactory.createDefaultGraph(), salt);
	}

	public static JenaIRI createIRI(String iriStr) {
		return new IRIImpl(iriStr);
	}

	public static JenaLiteral createLiteral(String lexStr) {
		return new LiteralImpl(NodeFactory.createLiteral(lexStr));
	}

	public static JenaLiteral createLiteralDT(String lexStr, String datatypeIRI) {
		return new LiteralImpl(NodeFactory.createLiteral(lexStr, NodeFactory.getType(datatypeIRI)));
	}

	public static JenaLiteral createLiteralLang(String lexStr, String langTag) {
		return new LiteralImpl(NodeFactory.createLiteral(lexStr, langTag));
	}

	public static JenaTriple createTriple(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return new TripleImpl(subject, predicate, object);
	}

	public static JenaQuad createQuad(BlankNodeOrIRI subject, IRI predicate, RDFTerm object, BlankNodeOrIRI graphName) {
		return new QuadImpl(subject, predicate, object, Optional.ofNullable(graphName));
	}
	
	public static JenaVariable createVariable(String name) {
		return new VariableImpl(NodeFactory.createVariable(name));
	}
	
	public static JenaAny createVariable() {
		return AnyImpl.Singleton.instance;
	}

	public static JenaTripleLike<RDFTerm,RDFTerm,RDFTerm> createGeneralizedTriple(RDFTerm subject, RDFTerm predicate, RDFTerm object) {
		return new GeneralizedQuadImpl<RDFTerm,RDFTerm,RDFTerm,RDFTerm>(subject, predicate, object);
	}

	public static JenaQuadLike<RDFTerm,RDFTerm,RDFTerm,RDFTerm> createGeneralizedQuad(RDFTerm subject, RDFTerm predicate, RDFTerm object, RDFTerm graphName) {
		return new GeneralizedQuadImpl<RDFTerm,RDFTerm,RDFTerm,RDFTerm>(subject, predicate, object, Optional.ofNullable(graphName));
	}
	
	public static JenaRDFTerm fromJena(Node node, UUID salt) throws ConversionException {
		if (! node.isConcrete()) {
			throw new ConversionException("Node is not a concrete RDF Term: " + node); 
		}
		return fromJenaGeneralized(node, salt);		
	}

	public static JenaRDFTerm fromJenaGeneralized(Node node, UUID salt) {
		if (node.isURI()) {
			return new IRIImpl(node);
		}
		if (node.isLiteral()) {
			return new LiteralImpl(node);
		}
		if (node.isBlank()) {
			return new BlankNodeImpl(node, salt);
		}
		if (node.equals(Node.ANY)) {
			return AnyImpl.Singleton.instance;
		}
		if (node.isVariable()) {
			return new VariableImpl(node);
		}
		throw new IllegalArgumentException("Unrecognized node type: " + node);
	}
	
	public static JenaGraph fromJena(org.apache.jena.graph.Graph graph) {
		return new GraphImpl(graph, UUID.randomUUID());
	}

	public static JenaGraph fromJena(org.apache.jena.graph.Graph graph, UUID salt) {
		return new GraphImpl(graph, salt);
	}

	public static JenaTriple fromJena(org.apache.jena.graph.Triple triple, UUID salt) {
		return new TripleImpl(triple, salt);
	}

	public static JenaTripleLike<RDFTerm, RDFTerm, RDFTerm> fromJenaGeneralized(org.apache.jena.graph.Triple triple, UUID salt) {
		return new GeneralizedQuadImpl<RDFTerm,RDFTerm,RDFTerm,RDFTerm>(triple, salt);
	}

	public static JenaQuadLike<RDFTerm,RDFTerm,RDFTerm,RDFTerm> fromJenaGeneralized(org.apache.jena.sparql.core.Quad quad, UUID salt) {
		return new GeneralizedQuadImpl<RDFTerm,RDFTerm,RDFTerm,RDFTerm>(quad, salt);
	}
	
	public static Quad fromJena(org.apache.jena.sparql.core.Quad quad, UUID salt) {
		return new QuadImpl(quad, salt);
	}
}
