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

import java.util.UUID;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.QuadLike;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.api.TripleLike;
import org.apache.commons.rdf.simple.Types;
import org.apache.commons.rdf.jsonldjava.JsonLdBlankNode.JsonLdBlankNodeImpl;
import org.apache.commons.rdf.jsonldjava.JsonLdTriple.JsonLdTripleImpl;
import org.apache.commons.rdf.jsonldjava.JsonLdQuad.JsonLdQuadImpl;
import org.apache.commons.rdf.jsonldjava.JsonLdLiteral.JsonLdLiteralImpl;

import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Node;


public final class JsonLdRDFTermFactory implements RDFTermFactory {
	
	public JsonLdRDFTermFactory() {
		// An "outside Graph" bnodePrefix
		this("urn:uuid:" + UUID.randomUUID() + "#b");
	}
	
	JsonLdRDFTermFactory(String bnodePrefix) {
		this.bnodePrefix = bnodePrefix;
	}
	
	String bnodePrefix;
	
	@Override
	public Graph createGraph() throws UnsupportedOperationException {
		return new JsonLdGraph();
	}
	
	@Override
	public JsonLdIRI createIRI(String iri) {
		return new JsonLdIRI.JsonLdIRIImpl(iri);
	}
	
	@Override
	public JsonLdBlankNode createBlankNode() {
		String id = "_:" + UUID.randomUUID().toString();
		return new JsonLdBlankNodeImpl(new RDFDataset.BlankNode(id), bnodePrefix);
	}
	
	@Override
	public JsonLdBlankNode createBlankNode(String name) {
		String id = "_:" + name;
		// TODO: Check if name is valid JSON-LD BlankNode identifier
		return new JsonLdBlankNodeImpl(new RDFDataset.BlankNode(id), bnodePrefix);
	}
	
	@Override
	public JsonLdTriple createTriple(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return new JsonLdTripleImpl(asJsonLdQuad(subject, predicate, object), bnodePrefix);
	}
	
	public Triple asTriple(final RDFDataset.Quad quad) {
		return new JsonLdTripleImpl(quad, bnodePrefix);
	}

	@Override
	public JsonLdQuad createQuad(BlankNodeOrIRI graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object)
			throws IllegalArgumentException, UnsupportedOperationException {
		return new JsonLdQuadImpl(asJsonLdQuad(graphName, subject, predicate, object), bnodePrefix);
	}
	
	@Override
	public JsonLdLiteral createLiteral(String literal) {		
		return new JsonLdLiteralImpl(new RDFDataset.Literal(literal, null, null));
	}
	@Override
	public JsonLdLiteral createLiteral(String literal, IRI dataType) {
		return new JsonLdLiteralImpl(new RDFDataset.Literal(literal, dataType.getIRIString(), null));	}
	@Override
	public JsonLdLiteral createLiteral(String literal, String language) {
		return new JsonLdLiteralImpl(new RDFDataset.Literal(literal, Types.RDF_LANGSTRING.getIRIString(), language));		
	}


	public Node asJsonLdNode(RDFTerm term) {
		if (term instanceof JsonLdTerm) {
			// Return original Node
			return ((JsonLdTerm)term).asNode();
		}
		if (term instanceof IRI) {
			return new RDFDataset.IRI( ((IRI)term).getIRIString() );
		}
		if (term instanceof BlankNode) {
			
			String uniqueReference = ((BlankNode)term).uniqueReference();
			if (uniqueReference.startsWith(bnodePrefix)) {
				// one of our own
				// TODO: Retrieve the original BlankNode
				return new RDFDataset.BlankNode(term.ntriplesString());
			} 
			return new RDFDataset.BlankNode( "_:" + uniqueReference );
		}
		if (term instanceof Literal) {
			Literal literal = (Literal) term;
			return new RDFDataset.Literal(literal.getLexicalForm(), literal.getDatatype().getIRIString(), 
					literal.getLanguageTag().orElse(null));
		}
		throw new IllegalArgumentException("RDFTerm not instanceof IRI, BlankNode or Literal: " + term);
	}
	

	public RDFDataset.Quad asJsonLdQuad(RDFTerm subject, RDFTerm predicate, RDFTerm object) {
		return asJsonLdQuad(null, subject, predicate, object);
	}

	public RDFDataset.Quad asJsonLdQuad(RDFTerm graphName, RDFTerm subject, RDFTerm predicate, RDFTerm object) {
		String graph = null;
		return new RDFDataset.Quad(asJsonLdNode(subject), asJsonLdNode(predicate), asJsonLdNode(object), graph);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public RDFDataset.Quad asJsonLdQuad(TripleLike tripleOrQuad) {
		RDFTerm g = null;	
		if (tripleOrQuad instanceof QuadLike) {
			QuadLike quadLike = (QuadLike) tripleOrQuad;
			g = (RDFTerm) quadLike.getGraphName().orElse(null);
		}		
		return asJsonLdQuad(g, tripleOrQuad.getSubject(), tripleOrQuad.getPredicate(), tripleOrQuad.getObject());
	}
	
	JsonLdTerm asTerm(final Node node, String blankNodePrefix) {	
		if (node == null) {
			return null; // e.g. default graph
		}
		if (node.isIRI()) {
			return new JsonLdIRI.JsonLdIRIImpl(node);
		} else if (node.isBlankNode()) {
			return new JsonLdBlankNodeImpl(node, blankNodePrefix);
		} else if (node.isLiteral()) {
			// TODO: Our own JsonLdLiteral
			if (node.getLanguage() != null) {
				return createLiteral(node.getValue(), node.getLanguage());
			} else {
				return createLiteral(node.getValue(), createIRI(node.getDatatype()));
			}
		} else {
			throw new IllegalArgumentException("Node is neither IRI, BlankNode nor Literal: " + node);
		}
	}	
	
	
}