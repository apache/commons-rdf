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

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory;

import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Node;
import com.github.jsonldjava.core.RDFDataset.Quad;

public class JsonLDGraph implements Graph {
	
	private static UUID SALT = UUID.randomUUID(); 
	
	private RDFDataset rdfDataSet = new RDFDataset();

	/**
	 * If true, include all Quad statements as Triples. If false, 
	 * only Quads in the default graph (<code>null</code>) are
	 * included.
	 */
	private boolean unionGraph = false;

	@Override
	public void close() throws Exception {
		rdfDataSet = null;		
	}

	@Override
	public void add(Triple triple) {
		// Quad q = asJsonLdQuad(triple);
		// rdfDataSet.addQuad(q);
		
		add(triple.getSubject(), triple.getPredicate(), triple.getObject());
	}
	private static RDFTermFactory rdfTermFactory = new SimpleRDFTermFactory();

	private Triple asTriple(final RDFDataset.Quad quad) {
		return new Triple() {
			
			@Override
			public BlankNodeOrIRI getSubject() {
				return (BlankNodeOrIRI) asTerm(quad.getSubject());
			}
			
			@Override
			public IRI getPredicate() {
				return (IRI) asTerm(quad.getPredicate());
			}
			
			@Override
			public RDFTerm getObject() {
				return asTerm(quad.getObject());
			}
			
			@Override
			public boolean equals(Object obj) {
				if (! (obj instanceof Triple)) {
					return false;
				}
				Triple other = (Triple) obj;
				return getSubject().equals(other.getSubject()) && 
						getPredicate().equals(other.getPredicate()) && 
						getObject().equals(other.getObject());
				
			}
			
			@Override
			public int hashCode() {
				return Objects.hash(getSubject(), getPredicate(), getObject());
			}
			
		};
	}
	
	private String bnodePrefix() {
		return "urn:uuid:" + SALT + "#" +  "g"+ System.identityHashCode(rdfDataSet);
	}
	
	private RDFTerm asTerm(final Node node) {		
		if (node.isIRI()) {
			return new IRI() {				
				@Override
				public String ntriplesString() {
					return "<" + node.getValue() + ">";
				}
				
				@Override
				public String getIRIString() {
					return node.getValue();
				}
				
				@Override
				public int hashCode() {
					return node.getValue().hashCode();
				}
				
				@Override
				public boolean equals(Object obj) {
					if (! (obj instanceof IRI)) {
						return false;
					} 
					IRI other = (IRI) obj;
					return node.getValue().equals(other.getIRIString());
				}
			};
		} else if (node.isBlankNode()) {
			return new BlankNode() {				
				@Override
				public String ntriplesString() {
					return node.getValue();
				}
				
				@Override
				public String uniqueReference() {					
					return bnodePrefix() + node.getValue();
				}
				
				@Override
				public boolean equals(Object obj) {
					if (! ( obj instanceof BlankNode)) {
						return false;
					}
					BlankNode other = (BlankNode) obj;
					return uniqueReference().equals(other.uniqueReference());
				}
				
				@Override
				public int hashCode() {
					return uniqueReference().hashCode();
				}				
			};
		} else if (node.isLiteral()) {			
			if (node.getLanguage() != null) {
				return rdfTermFactory.createLiteral(node.getValue(), node.getLanguage());
			} else {
				return rdfTermFactory.createLiteral(node.getValue(), rdfTermFactory.createIRI(node.getDatatype()));
			}
		} else {
			throw new IllegalArgumentException("Node is neither IRI, BlankNode nor Literal: " + node);
		}
	}
	
	private RDFDataset.Quad asJsonLdQuad(Triple triple) {
		Node subject = asJsonLdNode(triple.getSubject());
		Node predicate = asJsonLdNode(triple.getPredicate());
		Node object = asJsonLdNode(triple.getObject());
		String graph = null;
		// TODO: Add Quad to commons-rdf
//		if (triple instanceof Quad) {
//			String graph = triple.getGraph().getIRIString();
//		}
		return new RDFDataset.Quad(subject, predicate, object, graph);
		
	}

	private Node asJsonLdNode(RDFTerm term) {
		if (term instanceof IRI) {
			return new RDFDataset.IRI( ((IRI)term).getIRIString() );
		}
		if (term instanceof BlankNode) {
			
			String uniqueReference = ((BlankNode)term).uniqueReference();
			if (uniqueReference.startsWith(bnodePrefix())) {
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

	@Override
	public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		String subjectStr;
		if (subject instanceof BlankNode) { 
			subjectStr = subject.ntriplesString();
		} else if (subject instanceof IRI){
			subjectStr = ((IRI)subject).getIRIString();
		} else { 
			throw new IllegalStateException("Subject was neither IRI or BlankNode: " + subject);
		}
		
		String predicateStr = predicate.getIRIString();
		
		if (object instanceof Literal) {
			Literal literal = (Literal) object;
			rdfDataSet.addTriple(subjectStr, predicateStr, literal.getLexicalForm(), literal.getDatatype().getIRIString(), literal.getLanguageTag().orElse(null));			
		} else if (object instanceof BlankNode) {
			rdfDataSet.addTriple(subjectStr, predicateStr, object.ntriplesString());
		} else if (object instanceof IRI) { 
			rdfDataSet.addTriple(subjectStr, predicateStr, ((IRI)object).getIRIString());
		} else { 
			throw new IllegalStateException("Object was neither IRI, BlankNode nor Literal: " + object);
		}				
	}

	@Override
	public boolean contains(Triple triple) {
		return getTriples().anyMatch(Predicate.isEqual(triple));
	}

	@Override
	public boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return getTriples(subject, predicate, object).findAny().isPresent();
	}

	@Override
	public void remove(Triple triple) {
		remove(triple.getSubject(), triple.getPredicate(), triple.getObject());
	}

	@Override
	public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {		
		Predicate<? super Quad> filter = quadFilter(subject, predicate, object);
		if (! unionGraph) {
			rdfDataSet.getQuads("@default").removeIf(filter);
		} else {
			rdfDataSet.graphNames().parallelStream().map(rdfDataSet::getQuads).map(t -> t.removeIf(filter));
		}
	}

	private Predicate<? super Quad> quadFilter(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Optional<Node> subjectNode = Optional.ofNullable(subject).map(this::asJsonLdNode);
		Optional<Node> predicateNode = Optional.ofNullable(predicate).map(this::asJsonLdNode);
		Optional<Node> objectNode = Optional.ofNullable(object).map(this::asJsonLdNode);
		
		return q -> {
		    if (subjectNode.isPresent() && subjectNode.get().compareTo(q.getSubject()) != 0) {
		        return false;
		    }
		    if (predicateNode.isPresent() && predicateNode.get().compareTo(q.getPredicate()) != 0) {	          
		        return false;
		    }
		    if (objectNode.isPresent() && objectNode.get().compareTo(q.getObject()) != 0) {
		        return false;
		    }
		    return true;			
		};
	}

	@Override
	public void clear() {
		if (unionGraph) {
			// Delete all quads
			rdfDataSet.clear();
		} else {
			// Only the @default quads removed
			rdfDataSet.getQuads("@default").clear();
		}
	}

	@Override
	public long size() {
		if (! unionGraph) {
			return rdfDataSet.getQuads("@default").size();
		} else {
			// Summarize graph.size() for all graphs
			return rdfDataSet.graphNames().parallelStream().map(rdfDataSet::getQuads).collect(Collectors.summingLong(List::size));
		}
	}

	@Override
	public Stream<? extends Triple> getTriples() {
		if (! unionGraph) {
			return rdfDataSet.getQuads("@default").parallelStream().map(this::asTriple);
		}
		return rdfDataSet.graphNames().parallelStream().map(rdfDataSet::getQuads).flatMap(List<Quad>::parallelStream).map(this::asTriple);
	}

	@Override
	public Stream<? extends Triple> getTriples(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		// RDFDataSet has no optimizations to help us, so we'll dispatch to filter()
        return getTriples().filter(t -> {
            if (subject != null && !t.getSubject().equals(subject)) {
                return false;
            }
            if (predicate != null && !t.getPredicate().equals(predicate)) {
                return false;
            }
            if (object != null && !t.getObject().equals(object)) {
                return false;
            }
            return true;
        });
	}
}
