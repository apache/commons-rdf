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
import org.apache.commons.rdf.jena.JenaDataset;
import org.apache.commons.rdf.jena.JenaGraph;
import org.apache.commons.rdf.jena.JenaIRI;
import org.apache.commons.rdf.jena.JenaLiteral;
import org.apache.commons.rdf.jena.JenaQuad;
import org.apache.commons.rdf.jena.JenaQuadLike;
import org.apache.commons.rdf.jena.JenaRDFTerm;
import org.apache.commons.rdf.jena.JenaRDFTermFactory;
import org.apache.commons.rdf.jena.JenaTriple;
import org.apache.commons.rdf.jena.JenaTripleLike;
import org.apache.commons.rdf.jena.JenaVariable;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.sparql.core.DatasetGraph;
import org.apache.jena.sparql.core.DatasetGraphFactory;
import org.apache.jena.sparql.graph.GraphFactory;

/**
 * Construct Jena implementations of Commons RDF.
 * <p>
 * This class is primarily an internal helper class, users should instead use
 * {@link JenaRDFTermFactory}.
 * <p>
 * For the purpose of blank node identity, some of 
 * these methods require a {@link UUID} to use as a salt.
 * See {@link BlankNode#uniqueReference()} for details.
 * 
 */
public class JenaFactory {

	public static JenaBlankNode createBlankNode(String id, UUID salt) {
		return new JenaBlankNodeImpl(NodeFactory.createBlankNode(id), salt);
	}

	public static JenaBlankNode createBlankNode(UUID salt) {
		return new JenaBlankNodeImpl(NodeFactory.createBlankNode(), salt);
	}
	public static JenaDataset createDataset(UUID salt) {
		DatasetGraph dg = DatasetGraphFactory.createGeneral();
		// Or which createMethod() -- a bit confusing with lots of choice..
		return new JenaDatasetImpl(dg, salt);
	}

	public static JenaGraph createGraph(UUID salt) {
		return new JenaGraphImpl(GraphFactory.createDefaultGraph(), salt);
	}

	public static JenaIRI createIRI(String iriStr) {
		return new JenaIRIImpl(iriStr);
	}

	public static JenaLiteral createLiteral(String lexStr) {
		return new JenaLiteralImpl(NodeFactory.createLiteral(lexStr));
	}

	public static JenaLiteral createLiteralDT(String lexStr, String datatypeIRI) {
		return new JenaLiteralImpl(NodeFactory.createLiteral(lexStr, NodeFactory.getType(datatypeIRI)));
	}

	public static JenaLiteral createLiteralLang(String lexStr, String langTag) {
		return new JenaLiteralImpl(NodeFactory.createLiteral(lexStr, langTag));
	}

	public static JenaTriple createTriple(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return new JenaTripleImpl(subject, predicate, object);
	}

	public static JenaQuad createQuad(BlankNodeOrIRI subject, IRI predicate, RDFTerm object, BlankNodeOrIRI graphName) {
		return new JenaQuadImpl(subject, predicate, object, Optional.ofNullable(graphName));
	}
	
	public static JenaVariable createVariable(String name) {
		return new JenaVariableImpl(NodeFactory.createVariable(name));
	}
	
	public static JenaAny createAnyVariable() {
		return JenaAnyImpl.Singleton.instance;
	}

	public static JenaTripleLike<RDFTerm,RDFTerm,RDFTerm> createGeneralizedTriple(RDFTerm subject, RDFTerm predicate, RDFTerm object) {
		return new JenaGeneralizedQuad<RDFTerm,RDFTerm,RDFTerm,RDFTerm>(subject, predicate, object);
	}

	public static JenaQuadLike<RDFTerm,RDFTerm,RDFTerm,RDFTerm> createGeneralizedQuad(RDFTerm subject, RDFTerm predicate, RDFTerm object, RDFTerm graphName) {
		return new JenaGeneralizedQuad<RDFTerm,RDFTerm,RDFTerm,RDFTerm>(subject, predicate, object, Optional.ofNullable(graphName));
	}
	
	public static JenaRDFTerm fromJena(Node node, UUID salt) throws ConversionException {
		if (! node.isConcrete()) {
			throw new ConversionException("Node is not a concrete RDF Term: " + node); 
		}
		return fromJenaGeneralized(node, salt);		
	}

	public static JenaRDFTerm fromJenaGeneralized(Node node, UUID salt) {
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
			return JenaAnyImpl.Singleton.instance;
		}
		if (node.isVariable()) {
			return new JenaVariableImpl(node);
		}
		throw new IllegalArgumentException("Unrecognized node type: " + node);
	}

	public static JenaGraph fromJena(org.apache.jena.graph.Graph graph, UUID salt) {
		return new JenaGraphImpl(graph, salt);
	}

	public static JenaGraph fromJena(Model model, UUID salt) {
		return new JenaGraphImpl(model, salt);
	}

	public static JenaDataset fromJena(DatasetGraph datasetGraph, UUID salt) {
		return new JenaDatasetImpl(datasetGraph, salt);
	}
	
	public static JenaTriple fromJena(org.apache.jena.graph.Triple triple, UUID salt) {
		return new JenaTripleImpl(triple, salt);
	}

	public static JenaTripleLike<RDFTerm, RDFTerm, RDFTerm> fromJenaGeneralized(org.apache.jena.graph.Triple triple, UUID salt) {
		return new JenaGeneralizedQuad<RDFTerm,RDFTerm,RDFTerm,RDFTerm>(triple, salt);
	}

	public static JenaQuadLike<RDFTerm,RDFTerm,RDFTerm,RDFTerm> fromJenaGeneralized(org.apache.jena.sparql.core.Quad quad, UUID salt) {
		return new JenaGeneralizedQuad<RDFTerm,RDFTerm,RDFTerm,RDFTerm>(quad, salt);
	}
	
	public static JenaQuad fromJena(org.apache.jena.sparql.core.Quad quad, UUID salt) {
		return new JenaQuadImpl(quad, salt);
	}

}
