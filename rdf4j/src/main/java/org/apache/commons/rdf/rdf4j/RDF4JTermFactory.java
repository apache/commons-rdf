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

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;


// To avoid confusion, avoid importing 
// classes that are in both
// commons.rdf and openrdf.model (e.g. IRI)
import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.turtle.TurtleUtil;

/**
 * RDF4J implementation of RDFTermFactory
 * <p>
 * The {@link #RDF4JTermFactory()} constructor
 * uses a {@link SimpleValueFactory} to create corresponding
 * RDF4J {@link Value} instances. Alternatively, 
 * this factory can be constructed with a {@link ValueFactory} using
 * {@link #RDF4JTermFactory(ValueFactory)}.  
 * <p>
 * {@link #asRDFTerm(Value)} can be used to convert any RDF4J {@link Value}
 * to an RDFTerm. Note that adapted {@link BNode}s are considered equal if they
 * are converted with the same {@link RDF4JTermFactory} instance and have the same
 * {@link BNode#getID()}. 
 * <p>
 * {@link #createGraph()} creates a new Graph backed by {@link LinkedHashModel}. To 
 * use other models, see {@link #asRDFTermGraph(Model)}.
 * <p>
 * {@link #asTriple(Statement)} can be used to convert a RDF4J {@link Statement} to
 * a Commons RDF {@link Triple}.
 * <p>
 * {@link #asStatement(Triple)} can be used to convert any Commons RDF {@link Triple} 
 * to a RDF4J {@link Statement}.
 * <p
 * {@link #asValue(RDFTerm)} can be used to convert any Commons RDF {@link RDFTerm} to 
 * a corresponding RDF4J {@link Value}.
 * 
 */
public class RDF4JTermFactory implements RDFTermFactory {
		
	private UUID salt = UUID.randomUUID();

	private ValueFactory valueFactory;
	
	public RDF4JTermFactory() {
		this.valueFactory = SimpleValueFactory.getInstance();
	}
	
	public RDF4JTermFactory(ValueFactory valueFactory) { 
		this.valueFactory = valueFactory;
	}
	
	/**
	 * 
	 * Adapt a RDF4J {@link Value} as a Commons RDF 
	 * {@link RDFTerm}.
	 * <p>
	 * <p>
	 * The value will be of the same kind as the term, e.g. a
	 * {@link org.eclipse.rdf4j.model.BNode} is converted to a
	 * {@link org.apache.commons.rdf.api.BlankNode}, 
	 * a {@link org.eclipse.rdf4j.model.IRI}
	 * is converted to a {@link org.apache.commons.rdf.api.IRI}
	 * and a {@link org.eclipse.rdf4j.model.Literal}.
	 * is converted to a 
	 * {@link org.apache.commons.rdf.api.Literal} 
	 * 
	 * @param value
	 * @return
	 */
	public RDF4JTerm<?> asRDFTerm(final org.eclipse.rdf4j.model.Value value) {		
		return asRDFTerm(value, salt);	
	}

	/**
	 * 
	 * Adapt a RDF4J {@link Value} as a Commons RDF 
	 * {@link RDFTerm}.
	 * <p>
	 * <p>
	 * The value will be of the same kind as the term, e.g. a
	 * {@link org.eclipse.rdf4j.model.BNode} is converted to a
	 * {@link org.apache.commons.rdf.api.BlankNode}, 
	 * a {@link org.eclipse.rdf4j.model.IRI}
	 * is converted to a {@link org.apache.commons.rdf.api.IRI}
	 * and a {@link org.eclipse.rdf4j.model.Literal}.
	 * is converted to a 
	 * {@link org.apache.commons.rdf.api.Literal} 
	 * 
	 * @param value
	 * @return
	 */
	public static RDF4JTerm<?> asRDFTerm(final org.eclipse.rdf4j.model.Value value, UUID salt) {		
		if (value instanceof BNode) {
			return new BlankNodeImpl((BNode) value, salt);
		}
		if (value instanceof org.eclipse.rdf4j.model.Literal) {
			return new LiteralImpl((org.eclipse.rdf4j.model.Literal) value);
		}
		if (value instanceof org.eclipse.rdf4j.model.IRI) {
			return new IRIImpl((org.eclipse.rdf4j.model.IRI) value);
		}
		throw new IllegalArgumentException("Value is not a BNode, Literal or IRI: " + value.getClass());		
	}	
	
	/**
	 * Adapt an RDF4J {@link Model} as a 
	 * Commons RDF {@link Graph}.
	 * <p>
	 * Changes to the graph are reflected in the model, and
	 * vice versa. 
	 * 
	 * @param model RDF4J {@link Model} to adapt.
	 * @return Adapted {@link Graph}.
	 */
	public RDF4JGraph asRDFTermGraph(Model model) {
		return new GraphImpl(model);
	}
	
	public Statement asStatement(Triple triple) {
		return valueFactory.createStatement(
				(org.eclipse.rdf4j.model.Resource) asValue(triple.getSubject()), 
				(org.eclipse.rdf4j.model.IRI) asValue(triple.getPredicate()), 
				asValue(triple.getObject()));
	}	
	
	/**
	 * Adapt a RDF4J {@link Statement} as a Commons RDF 
	 * {@link Triple}.
	 * 
	 * @param statement
	 * @return
	 */
	public RDF4JTriple asTriple(final Statement statement) {
		return new TripleImpl(statement, salt);
	}

	/**
	 * Adapt a RDF4J {@link Statement} as a Commons RDF 
	 * {@link Triple}.
	 * 
	 * @param statement
	 * @return
	 */
	public static RDF4JTriple asTriple(final Statement statement, UUID salt) {
		return new TripleImpl(statement, salt);
	}
	
	/**
	 * Adapt a Commons RDF {@link RDFTerm} as a RDF4J {@link Value}.
	 * <p>
	 * The value will be of the same kind as the term, e.g. a
	 * {@link org.apache.commons.rdf.api.BlankNode} is converted to a
	 * {@link org.eclipse.rdf4j.model.BNode}, a {@link org.apache.commons.rdf.api.IRI}
	 * is converted to a {@link org.eclipse.rdf4j.model.IRI} and a
	 * {@link org.apache.commons.rdf.api.Literal} is converted to a
	 * {@link org.eclipse.rdf4j.model.Literal}.
	 * <p>
	 * If the provided {@link RDFTerm} is <code>null</code>, then the returned
	 * value is <code>null</code>.
	 * <p>
	 * If the provided term is an instance of {@link RDF4JTerm}, then the
	 * {@link RDF4JTerm#asValue()} is returned without any conversion. Note that
	 * this could mean that a {@link Value} from a different kind of 
	 * {@link ValueFactory} could be returned.
	 * 
	 * @param term RDFTerm to adapt to RDF4J Value 
	 * @return Adapted RDF4J {@link Value}
	 */
	public Value asValue(RDFTerm term) {		
		if (term == null) { 
			return null;
		}
		if (term instanceof RDF4JTerm) {
			// One of our own - avoid converting again.
			// (This is crucial to avoid double-escaping in BlankNode)
			return ((RDF4JTerm<?>)term).asValue();
		}
		if (term instanceof org.apache.commons.rdf.api.IRI) {
			org.apache.commons.rdf.api.IRI iri = (org.apache.commons.rdf.api.IRI) term;
			return valueFactory.createIRI(iri.getIRIString());
		}
		if (term instanceof org.apache.commons.rdf.api.Literal) {
			org.apache.commons.rdf.api.Literal literal = (org.apache.commons.rdf.api.Literal) term;
			String label = literal.getLexicalForm();
			if (literal.getLanguageTag().isPresent()) {
				String lang = literal.getLanguageTag().get();
				return valueFactory.createLiteral(label, lang);
			}
			org.eclipse.rdf4j.model.IRI dataType = (org.eclipse.rdf4j.model.IRI ) asValue(literal.getDatatype());
			return valueFactory.createLiteral(label, dataType);
		}
		if (term instanceof BlankNode) {
			// This is where it gets tricky to support round trips!			
			BlankNode blankNode = (BlankNode) term;
			// FIXME: The uniqueReference might not be a valid BlankNode identifier..
			// does it have to be?
			return valueFactory.createBNode(blankNode.uniqueReference());
		}
		throw new IllegalArgumentException("RDFTerm was not an IRI, Literal or BlankNode: " + term.getClass());
	}

	@Override
	public BlankNodeImpl createBlankNode() throws UnsupportedOperationException {
		BNode bnode = valueFactory.createBNode();
		return (BlankNodeImpl)asRDFTerm(bnode);
	}

	@Override
	public BlankNodeImpl createBlankNode(String name) throws UnsupportedOperationException {
		BNode bnode = valueFactory.createBNode(name);
		return (BlankNodeImpl)asRDFTerm(bnode);
	}

	@Override
	public RDF4JGraph createGraph() throws UnsupportedOperationException {
		return asRDFTermGraph(new LinkedHashModel());
	}
	
	@Override
	public RDF4JIRI createIRI(String iri) throws IllegalArgumentException, UnsupportedOperationException {
		return (RDF4JIRI) asRDFTerm(valueFactory.createIRI(iri));
	}

	@Override
	public LiteralImpl createLiteral(String lexicalForm) throws IllegalArgumentException, UnsupportedOperationException {
		org.eclipse.rdf4j.model.Literal lit = valueFactory.createLiteral(lexicalForm);
		return (LiteralImpl)asRDFTerm(lit);
	}

	@Override
	public org.apache.commons.rdf.api.Literal createLiteral(String lexicalForm, org.apache.commons.rdf.api.IRI dataType)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.eclipse.rdf4j.model.IRI iri = valueFactory.createIRI(dataType.getIRIString());
		org.eclipse.rdf4j.model.Literal lit = valueFactory.createLiteral(lexicalForm, iri);
		return (org.apache.commons.rdf.api.Literal)asRDFTerm(lit);
	}

	@Override
	public org.apache.commons.rdf.api.Literal createLiteral(String lexicalForm, String languageTag)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.eclipse.rdf4j.model.Literal lit = valueFactory.createLiteral(lexicalForm, languageTag);
		return (org.apache.commons.rdf.api.Literal)asRDFTerm(lit);
	}

	@Override
	public RDF4JTriple createTriple(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object)
			throws IllegalArgumentException, UnsupportedOperationException {
		final Statement statement = valueFactory.createStatement(
				(org.eclipse.rdf4j.model.Resource) asValue(subject), 
				(org.eclipse.rdf4j.model.IRI) asValue(predicate), 
				asValue(object));
		return asTriple(statement);
	}

	
	private static final class BlankNodeImpl extends RDFTermImpl<BNode>
		implements RDF4JBlankNode {
		
		private transient int hashCode = 0;
		private long saltUUIDmost;
		private long saltUUIDleast;
		
		BlankNodeImpl(BNode bNode, UUID salt) {
			super(bNode);			
			// Space-efficient storage of salt UUID
			saltUUIDmost = salt.getMostSignificantBits();
			saltUUIDleast = salt.getLeastSignificantBits();
		}
		
		public boolean equals(Object obj) { 
			if (obj == this) { 
				return true;
			}
			// NOTE: Do NOT use Bnode.equals() as it has a more generous
			// equality based only on the value.getID();			
			if (obj instanceof BlankNode) {
				BlankNode blankNode = (BlankNode) obj;
				return uniqueReference().equals(blankNode.uniqueReference());								
			}
			return false;
		}
	
		@Override
		public int hashCode() {
			if (hashCode != 0) {
				return hashCode;
			}
			return hashCode = uniqueReference().hashCode();
		}
	
		@Override
		public String ntriplesString() {
			if (isValidBlankNodeLabel(value.getID())) { 
				return "_:" + value.getID();
			} else {
				return "_:" + UUID.nameUUIDFromBytes(value.getID().getBytes(StandardCharsets.UTF_8));
			}
		}
	
		@Override
		public String uniqueReference() {
			UUID uuid = new UUID(saltUUIDmost, saltUUIDleast);
			return "urn:uuid:" + uuid + "#" + value.getID();
		}
	
		private boolean isValidBlankNodeLabel(String id) {
			// FIXME: Replace with a regular expression?			
			if (id.isEmpty()) { 
				return false;
			}
			if (! TurtleUtil.isBLANK_NODE_LABEL_StartChar(id.codePointAt(0)))  {
				return false;
			}
			for (int i=1; i<id.length(); i++) { 
				if (! TurtleUtil.isBLANK_NODE_LABEL_Char(id.codePointAt(i))) { 
					return false;
				}
			}
			return true;
		}
	}

	private final static class GraphImpl implements RDF4JGraph {
		
		private Model model;
		private RDF4JTermFactory rdf4jTermFactory;
	
		GraphImpl(Model model) {
			this.model = model;	
			this.rdf4jTermFactory = new RDF4JTermFactory();
		}

		GraphImpl(Model model, RDF4JTermFactory rdf4jTermFactory) {
			this.model = model;	
			this.rdf4jTermFactory = rdf4jTermFactory;
		}
		
		@Override
		public void add(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object) {
			model.add(
					(Resource)rdf4jTermFactory.asValue(subject), 
					(org.eclipse.rdf4j.model.IRI)rdf4jTermFactory.asValue(predicate), 
					rdf4jTermFactory.asValue(object));				
		}
		
		@Override
		public void add(Triple triple) {
			model.add(rdf4jTermFactory.asStatement(triple));
		}
	
		public Model asModel() { 
			return model;
		}
	
		@Override
		public void clear() {
			model.clear();
		}
	
		@Override
		public boolean contains(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object) {
			return model.contains(
					(Resource)rdf4jTermFactory.asValue(subject), 
					(org.eclipse.rdf4j.model.IRI)rdf4jTermFactory.asValue(predicate), 
					rdf4jTermFactory.asValue(object));
		}
	
		@Override
		public boolean contains(Triple triple) {
			return model.contains(rdf4jTermFactory.asStatement(triple));
		}
	
		@Override
		public Stream<RDF4JTriple> stream() {
			return model.parallelStream().map(rdf4jTermFactory::asTriple);
		}
	
		@Override
		public Stream<RDF4JTriple> stream(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object) {
			return model.filter(
					(Resource)rdf4jTermFactory.asValue(subject), 
					(org.eclipse.rdf4j.model.IRI)rdf4jTermFactory.asValue(predicate), 
					rdf4jTermFactory.asValue(object)).parallelStream()
				.map(rdf4jTermFactory::asTriple);
		}
	
		@Override
		public void remove(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object) {
			model.remove(
					(Resource)rdf4jTermFactory.asValue(subject), 
					(org.eclipse.rdf4j.model.IRI)rdf4jTermFactory.asValue(predicate), 
					rdf4jTermFactory.asValue(object));
			
		}
	
		@Override
		public void remove(Triple triple) { 
			model.remove(rdf4jTermFactory.asStatement(triple));
		}
	
		@Override
		public long size() {
			int size = model.size();
			if (size < Integer.MAX_VALUE) {
				return size;
			} else {
				// Collection.size() can't help us, we'll have to count
				return model.parallelStream().count();
			}				
		}
	}

	private final static class IRIImpl extends RDFTermImpl<org.eclipse.rdf4j.model.IRI> 
		implements RDF4JIRI {
	
		IRIImpl(org.eclipse.rdf4j.model.IRI iri) {
			super(iri);			
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == this) { return true; }
			if (obj instanceof IRIImpl) {
				IRIImpl impl = (IRIImpl) obj; 
				return asValue().equals(impl.asValue());
			}
			if (obj instanceof org.apache.commons.rdf.api.IRI) {
				org.apache.commons.rdf.api.IRI iri = (org.apache.commons.rdf.api.IRI) obj;
				return value.toString().equals(iri.getIRIString());
			}
			return false;
		}
	
		@Override
		public String getIRIString() {
			return value.toString();
		}
	
		public int hashCode() {
			// Same definition
			return value.hashCode();
		}
	
		@Override
		public String ntriplesString() {
			return "<" + value.toString() +  ">";
		}
		@Override
		public String toString() {
			return value.toString();
		}
		
	}

	private final static class LiteralImpl 
		extends RDFTermImpl<org.eclipse.rdf4j.model.Literal>
	    implements RDF4JLiteral {		
	
		private static final String QUOTE = "\"";
		
		LiteralImpl(org.eclipse.rdf4j.model.Literal literal) {
			super(literal);			
		}
		@Override
		public boolean equals(Object obj) {
			if (obj == this) { return true; }
			if (obj instanceof org.apache.commons.rdf.api.Literal) {
				org.apache.commons.rdf.api.Literal other = (org.apache.commons.rdf.api.Literal) obj;
				return getLexicalForm().equals(other.getLexicalForm()) &&
						getDatatype().equals(other.getDatatype()) && 
						getLanguageTag().equals(other.getLanguageTag());
				
			}
			return false;
		}
	
		@Override
		public org.apache.commons.rdf.api.IRI getDatatype() {
			return new IRIImpl(value.getDatatype());
		}
	
		@Override
		public Optional<String> getLanguageTag() {
			return value.getLanguage();
		}
	
		@Override
		public String getLexicalForm() {
			return value.getLabel();
		}
	
		public int hashCode() {
			return Objects.hash(value.getLabel(), value.getDatatype(), value.getLanguage());
		}
	
		@Override
		public String ntriplesString() {
			// TODO: Use a more efficient StringBuffer
			String escaped = QUOTE + TurtleUtil.encodeString(value.getLabel()) + QUOTE;
			if (value.getLanguage().isPresent()) {
				return escaped + "@" + value.getLanguage().get();
			}
			if (value.getDatatype().equals(XMLSchema.STRING)) { 
				return escaped;
			}
			return escaped + "^^<" + TurtleUtil.encodeURIString(value.getDatatype().toString()) + ">";
		}
	
		@Override
		public String toString() {
			return ntriplesString();
		}
	}	

	private static abstract class RDFTermImpl<T extends Value> implements RDF4JTerm<T> {
		T value;
	
		RDFTermImpl(T value) {
			this.value = value;			
		}
		
		public T asValue() { 
			return value;
		}
	}
	
	private final static class TripleImpl implements Triple, RDF4JTriple {
		private final Statement statement;	
		private UUID salt;
		
		TripleImpl(Statement statement, UUID salt) {
			this.statement = statement;
			this.salt = salt;
		}
	
		public Statement asStatement() { 
			return statement;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Triple) {
				Triple triple = (Triple) obj;
				return getSubject().equals(triple.getSubject()) &&
						getPredicate().equals(triple.getPredicate()) && 
						getObject().equals(triple.getObject());
			}
			return false;
		}
	
		@Override
		public RDFTerm getObject() {
			return asRDFTerm(statement.getObject(), salt);
		}
	
		@Override
		public org.apache.commons.rdf.api.IRI getPredicate() {
			return (org.apache.commons.rdf.api.IRI) asRDFTerm(statement.getPredicate(), null);
		}
		
		@Override
		public BlankNodeOrIRI getSubject() {
			return (BlankNodeOrIRI) asRDFTerm(statement.getSubject(), salt);
		}
	
		@Override
		public int hashCode() {
			return Objects.hash(getSubject(), getPredicate(), getObject());
		}
		
		@Override
		public String toString() {
			return statement.toString();
		}
}

	
}
