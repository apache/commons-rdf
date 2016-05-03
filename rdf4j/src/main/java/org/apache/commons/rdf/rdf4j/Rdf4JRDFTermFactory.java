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
package org.apache.commons.rdf.rdf4j;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.simple.Types;
import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Model;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.turtle.TurtleUtil;

public class Rdf4JRDFTermFactory implements RDFTermFactory {
	private static final String QUOTE = "\"";

	ValueFactory valueFactory = SimpleValueFactory.getInstance();
	
	String salt = "urn:uuid:" + UUID.randomUUID() + "#";
	
	@Override
	public BlankNode createBlankNode() throws UnsupportedOperationException {
		BNode bnode = valueFactory.createBNode();
		return (BlankNode)asRDFTerm(bnode);
	}
	
	@Override
	public BlankNode createBlankNode(String name) throws UnsupportedOperationException {
		BNode bnode = valueFactory.createBNode(name);
		return (BlankNode)asRDFTerm(bnode);
	}
	
	@Override
	public org.apache.commons.rdf.api.Literal createLiteral(String lexicalForm) throws IllegalArgumentException, UnsupportedOperationException {
		org.openrdf.model.Literal lit = valueFactory.createLiteral(lexicalForm);
		return (org.apache.commons.rdf.api.Literal)asRDFTerm(lit);
	}

	@Override
	public org.apache.commons.rdf.api.Literal createLiteral(String lexicalForm, org.apache.commons.rdf.api.IRI dataType)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.openrdf.model.IRI iri = valueFactory.createIRI(dataType.getIRIString());
		org.openrdf.model.Literal lit = valueFactory.createLiteral(lexicalForm, iri);
		return (org.apache.commons.rdf.api.Literal)asRDFTerm(lit);
	}
	
	@Override
	public org.apache.commons.rdf.api.Literal createLiteral(String lexicalForm, String languageTag)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.openrdf.model.Literal lit = valueFactory.createLiteral(lexicalForm, languageTag);
		return (org.apache.commons.rdf.api.Literal)asRDFTerm(lit);
	}
	
	@Override
	public org.apache.commons.rdf.api.IRI createIRI(String iri) throws IllegalArgumentException, UnsupportedOperationException {
		return (org.apache.commons.rdf.api.IRI) asRDFTerm(valueFactory.createIRI(iri));
	}
	
	@Override
	public org.apache.commons.rdf.api.Graph createGraph() throws UnsupportedOperationException {
		return asRDFTermGraph(new LinkedHashModel());
	}
	
	@Override
	public Triple createTriple(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object)
			throws IllegalArgumentException, UnsupportedOperationException {
		final Statement statement = valueFactory.createStatement(
				(org.openrdf.model.Resource) asValue(subject), 
				(org.openrdf.model.IRI) asValue(predicate), 
				asValue(object));
		return new Triple() {			
			@Override
			public BlankNodeOrIRI getSubject() {
				return (BlankNodeOrIRI) asRDFTerm(statement.getSubject());
			}
			@Override
			public org.apache.commons.rdf.api.IRI getPredicate() {
				return (org.apache.commons.rdf.api.IRI) asRDFTerm(statement.getPredicate());
			}
			@Override
			public RDFTerm getObject() {
				return asRDFTerm(statement.getObject());
			}
		};
	}	
	
	private Value asValue(RDFTerm object) {
		if (object instanceof org.apache.commons.rdf.api.IRI) {
			org.apache.commons.rdf.api.IRI iri = (org.apache.commons.rdf.api.IRI) object;
			return valueFactory.createIRI(iri.getIRIString());
		}
		if (object instanceof org.apache.commons.rdf.api.Literal) {
			org.apache.commons.rdf.api.Literal literal = (org.apache.commons.rdf.api.Literal) object;
			String label = literal.getLexicalForm();
			if (literal.getLanguageTag().isPresent()) {
				String lang = literal.getLanguageTag().get();
				return valueFactory.createLiteral(label, lang);
			}
			org.openrdf.model.IRI dataType = (org.openrdf.model.IRI ) asValue(literal.getDatatype());
			return valueFactory.createLiteral(label, dataType);
		}
		if (object instanceof BlankNode) {
			// This is where it gets tricky to support round trips!			
			BlankNode blankNode = (BlankNode) object;
			// FIXME: The uniqueReference might not be a valid BlankNode identifier..
			// does it have to be?
			return valueFactory.createBNode(blankNode.uniqueReference());
		}
		throw new IllegalArgumentException("RDFTerm was not an IRI, Literal or BlankNode: " + object.getClass());
	}

	private org.apache.commons.rdf.api.Graph asRDFTermGraph(Model model) {
		return new org.apache.commons.rdf.api.Graph() {
			
			@Override
			public long size() {
				int size = model.size();
				if (size < Integer.MAX_VALUE) {
					return size;
				} else {
					// Collection.size() can't help us, we'll have to count
					return model.stream().count();
				}				
			}
			
			@Override
			public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void remove(Triple triple) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public Stream<? extends Triple> getTriples(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object) {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public Stream<? extends Triple> getTriples() {
				// TODO Auto-generated method stub
				return null;
			}
			
			@Override
			public boolean contains(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public boolean contains(Triple triple) {
				// TODO Auto-generated method stub
				return false;
			}
			
			@Override
			public void clear() {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void add(Triple triple) {
				// TODO Auto-generated method stub
				
			}
		};
	}

	private RDFTerm asRDFTerm(final org.openrdf.model.Value value) {
		if (value instanceof BNode) {
			BNode bNode = (BNode) value;
			
			return new BlankNode() {
				@Override
				public String ntriplesString() {
					return "_:" + bNode.getID();
				}
				
				@Override
				public String uniqueReference() {
					return salt + bNode.getID();
				}
				@Override
				public int hashCode() {
					return uniqueReference().hashCode();
				}
				public boolean equals(Object obj) { 
					if (obj instanceof BlankNode) {
						BlankNode blankNode = (BlankNode) obj;
						return uniqueReference().equals(blankNode.uniqueReference());								
					}
					return false;
				}
				
			};
		}
		if (value instanceof org.openrdf.model.Literal) {
			org.openrdf.model.Literal literal = (org.openrdf.model.Literal) value; 
			return new org.apache.commons.rdf.api.Literal() {
				@Override
				public String ntriplesString() {
					// TODO: Use a more efficient StringBuffer
					String escaped = QUOTE + TurtleUtil.encodeString(literal.getLabel()) + QUOTE;
					if (literal.getLanguage().isPresent()) {
						return escaped + "@" + literal.getLanguage();
					}
					if (literal.getDatatype().equals(Types.XSD_STRING)) { 
						return escaped;
					}
					return escaped + "^^" + literal.getDatatype();
				}
				@Override
				public String getLexicalForm() {
					return literal.getLabel();
				}
				@Override
				public org.apache.commons.rdf.api.IRI getDatatype() {
					return (org.apache.commons.rdf.api.IRI) asRDFTerm(literal.getDatatype());
				}
				@Override
				public Optional<String> getLanguageTag() {
					return literal.getLanguage();
				} 
				@Override
				public String toString() {
					return ntriplesString();
				}
				@Override
				public boolean equals(Object obj) {
					if (obj instanceof org.apache.commons.rdf.api.Literal) {
						org.apache.commons.rdf.api.Literal other = (org.apache.commons.rdf.api.Literal) obj;
						return getLexicalForm().equals(other.getLexicalForm()) &&
								getDatatype().equals(other.getDatatype()) && 
								getLanguageTag().equals(other.getLanguageTag());
						
					}
					return false;
				}
				public int hashCode() {
					return Objects.hash(literal.getLabel(), literal.getDatatype(), literal.getLanguage());
				}
				
			};
		}
		if (value instanceof org.openrdf.model.IRI) {
			org.openrdf.model.IRI iri = (org.openrdf.model.IRI) value;
			return new org.apache.commons.rdf.api.IRI() {
				@Override
				public String ntriplesString() {
					return "<" + iri.toString() +  ">";
				}
				@Override
				public String getIRIString() {
					return iri.toString();
				}
				
				@Override
				public String toString() {
					return iri.toString();
				}				
				public int hashCode() {
					// Same definition
					return iri.hashCode();
				}				
			};
		}
		throw new IllegalArgumentException("Value is not a BNode, Literal or IRI: " + value.getClass());		
	}
	
}
