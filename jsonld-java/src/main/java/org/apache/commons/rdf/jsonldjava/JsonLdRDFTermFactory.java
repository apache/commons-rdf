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
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.simple.Types;

import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Node;

final class JsonLdRDFTermFactory implements RDFTermFactory {
	
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
	public IRI createIRI(String iri) {
		return new JsonLdIRI(iri);
	}
	
	@Override
	public BlankNode createBlankNode() {
		String id = "_:" + UUID.randomUUID().toString();
		return new JsonLdBlankNode(new RDFDataset.BlankNode(id), bnodePrefix);
	}
	
	@Override
	public BlankNode createBlankNode(String name) {
		String id = "_:" + name;
		// TODO: Check if name is valid JSON-LD BlankNode identifier
		return new JsonLdBlankNode(new RDFDataset.BlankNode(id), bnodePrefix);
	}
	
	@Override
	public Triple createTriple(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return new JsonLdTriple(asJsonLdQuad(subject, predicate, object), bnodePrefix);
	}
	
	@Override
	public Literal createLiteral(String literal) {		
		return new JsonLdLiteral(new RDFDataset.Literal(literal, null, null));
	}
	@Override
	public Literal createLiteral(String literal, IRI dataType) {
		return new JsonLdLiteral(new RDFDataset.Literal(literal, dataType.getIRIString(), null));	}
	@Override
	public Literal createLiteral(String literal, String language) {
		return new JsonLdLiteral(new RDFDataset.Literal(literal, Types.RDF_LANGSTRING.getIRIString(), language));		
	}


	private Node asJsonLdNode(RDFTerm term) {
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
	

	public RDFDataset.Quad asJsonLdQuad(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		String graph = null;
		// TODO: Add Quad to commons-rdf
//		if (triple instanceof Quad) {
//			String graph = triple.getGraph().getIRIString();
//		}
		return new RDFDataset.Quad(asJsonLdNode(subject), asJsonLdNode(predicate), asJsonLdNode(object), graph);
	}


	
	public RDFDataset.Quad asJsonLdQuad(Triple triple) {
		return asJsonLdQuad(triple.getSubject(), triple.getPredicate(), triple.getObject());
	}

	RDFTerm asTerm(final Node node, String blankNodePrefix) {		
		if (node.isIRI()) {
			return new JsonLdIRI(node);
		} else if (node.isBlankNode()) {
			return new JsonLdBlankNode(node, blankNodePrefix);
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