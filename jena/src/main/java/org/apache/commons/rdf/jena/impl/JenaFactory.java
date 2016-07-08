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

import java.util.UUID;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.ConversionException;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.sparql.graph.GraphFactory;

public class JenaFactory {
	public static BlankNode createBlankNode() {
		return new BlankNodeImpl(NodeFactory.createBlankNode(), UUID.randomUUID());
	}

	public static BlankNode createBlankNode(String id, UUID salt) {
		return new BlankNodeImpl(NodeFactory.createBlankNode(id), salt);
	}

	public static BlankNode createBlankNode(UUID salt) {
		return new BlankNodeImpl(NodeFactory.createBlankNode(), salt);
	}

	public static Graph createGraph() {
		return createGraph(UUID.randomUUID());
	}

	public static Graph createGraph(UUID salt) {
		return new GraphImpl(GraphFactory.createDefaultGraph(), salt);
	}

	// basic components to commonsrdf backed by Jena.
	public static IRI createIRI(String iriStr) {
		return new IRIImpl(iriStr);
	}

	public static Literal createLiteral(String lexStr) {
		return new LiteralImpl(NodeFactory.createLiteral(lexStr));
	}

	public static Literal createLiteralDT(String lexStr, String datatypeIRI) {
		return new LiteralImpl(NodeFactory.createLiteral(lexStr, NodeFactory.getType(datatypeIRI)));
	}

	public static Literal createLiteralLang(String lexStr, String langTag) {
		return new LiteralImpl(NodeFactory.createLiteral(lexStr, langTag));
	}

	public static Triple createTriple(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return new TripleImpl(subject, predicate, object);
	}

	public static RDFTerm fromJena(Node node, UUID salt) throws ConversionException {
		if (node.isURI())
			return new IRIImpl(node);
		if (node.isLiteral()) {
			return new LiteralImpl(node);
//			String lang = node.getLiteralLanguage();
//			if (lang != null && lang.isEmpty())
//				return createLiteralLang(node.getLiteralLexicalForm(), lang);
//			if (node.getLiteralDatatype().equals(XSDDatatype.XSDstring))
//				return createLiteral(node.getLiteralLexicalForm());
//			return createLiteralDT(node.getLiteralLexicalForm(), node.getLiteralDatatype().getURI());
		}
		if (node.isBlank())
			return new BlankNodeImpl(node, salt);
		throw new ConversionException("Node is not a concrete RDF Term: " + node);
	}

	public static Graph fromJena(org.apache.jena.graph.Graph graph) {
		return new GraphImpl(graph, UUID.randomUUID());
	}

	public static Graph fromJena(org.apache.jena.graph.Graph graph, UUID salt) {
		return new GraphImpl(graph, salt);
	}

	public static Triple fromJena(org.apache.jena.graph.Triple triple, UUID salt) {
		return new TripleImpl(triple, salt);
	}

	public static Quad fromJena(org.apache.jena.sparql.core.Quad quad, UUID salt) {
		return new QuadImpl(quad, salt);
	}
}
