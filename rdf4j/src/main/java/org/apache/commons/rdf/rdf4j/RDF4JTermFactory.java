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

import java.util.UUID;

// To avoid confusion, avoid importing 
// classes that are in both
// commons.rdf and openrdf.model (e.g. IRI)
import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.api.TripleLike;
import org.apache.commons.rdf.rdf4j.impl.BlankNodeImpl;
import org.apache.commons.rdf.rdf4j.impl.IRIImpl;
import org.apache.commons.rdf.rdf4j.impl.LiteralImpl;
import org.apache.commons.rdf.rdf4j.impl.ModelGraphImpl;
import org.apache.commons.rdf.rdf4j.impl.QuadImpl;
import org.apache.commons.rdf.rdf4j.impl.RepositoryDatasetImpl;
import org.apache.commons.rdf.rdf4j.impl.RepositoryGraphImpl;
import org.apache.commons.rdf.rdf4j.impl.TripleImpl;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;

/**
 * RDF4J implementation of RDFTermFactory
 * <p>
 * The {@link #RDF4JTermFactory()} constructor uses a {@link SimpleValueFactory}
 * to create corresponding RDF4J {@link Value} instances. Alternatively, this
 * factory can be constructed with a {@link ValueFactory} using
 * {@link #RDF4JTermFactory(ValueFactory)}.
 * <p>
 * {@link #asRDFTerm(Value)} can be used to convert any RDF4J {@link Value} to
 * an RDFTerm. Note that adapted {@link BNode}s are considered equal if they are
 * converted with the same {@link RDF4JTermFactory} instance and have the same
 * {@link BNode#getID()}.
 * <p>
 * {@link #createGraph()} creates a new Graph backed by {@link LinkedHashModel}.
 * To use other models, see {@link #asRDFTermGraph(Model)}.
 * <p>
 * {@link #asTriple(Statement)} can be used to convert a RDF4J {@link Statement}
 * to a Commons RDF {@link Triple}.
 * <p>
 * {@link #asStatement(Triple)} can be used to convert any Commons RDF
 * {@link Triple} to a RDF4J {@link Statement}. <p {@link #asValue(RDFTerm)} can
 * be used to convert any Commons RDF {@link RDFTerm} to a corresponding RDF4J
 * {@link Value}.
 * 
 */
public class RDF4JTermFactory implements RDFTermFactory {

	/**
	 * 
	 * Adapt a RDF4J {@link Value} as a Commons RDF {@link RDFTerm}.
	 * <p>
	 * <p>
	 * The value will be of the same kind as the term, e.g. a
	 * {@link org.eclipse.rdf4j.model.BNode} is converted to a
	 * {@link org.apache.commons.rdf.api.BlankNode}, a
	 * {@link org.eclipse.rdf4j.model.IRI} is converted to a
	 * {@link org.apache.commons.rdf.api.IRI} and a
	 * {@link org.eclipse.rdf4j.model.Literal}. is converted to a
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
	 * Adapt a RDF4J {@link Statement} as a Commons RDF {@link Triple}.
	 * 
	 * @param statement
	 * @return
	 */
	public static RDF4JTriple asTriple(final Statement statement, UUID salt) {
		return new TripleImpl(statement, salt);
	}

	private UUID salt = UUID.randomUUID();

	private ValueFactory valueFactory;

	public RDF4JTermFactory() {
		this.valueFactory = SimpleValueFactory.getInstance();
	}

	public RDF4JTermFactory(ValueFactory valueFactory) {
		this.valueFactory = valueFactory;
	}

	/**
	 * Adapt a RDF4J {@link Statement} as a Commons RDF {@link Quad}.
	 * 
	 * @param statement
	 * @return A {@link RDF4JQuad} that is equivalent to the statement
	 */
	public RDF4JQuad asQuad(final Statement statement) {
		return new QuadImpl(statement, salt);
	}

	/**
	 * 
	 * Adapt a RDF4J {@link Value} as a Commons RDF {@link RDFTerm}.
	 * <p>
	 * <p>
	 * The value will be of the same kind as the term, e.g. a
	 * {@link org.eclipse.rdf4j.model.BNode} is converted to a
	 * {@link org.apache.commons.rdf.api.BlankNode}, a
	 * {@link org.eclipse.rdf4j.model.IRI} is converted to a
	 * {@link org.apache.commons.rdf.api.IRI} and a
	 * {@link org.eclipse.rdf4j.model.Literal}. is converted to a
	 * {@link org.apache.commons.rdf.api.Literal}
	 * 
	 * @param value
	 * @return
	 */
	public RDF4JTerm<?> asRDFTerm(final org.eclipse.rdf4j.model.Value value) {
		return asRDFTerm(value, salt);
	}

	/**
	 * Adapt an RDF4J {@link Repository} as a Commons RDF {@link Dataset}.
	 * <p>
	 * Changes to the dataset are reflected in the repository, and vice versa.
	 * 
	 * @param repository
	 *            RDF4J {@link Repository} to connect to.
	 * @return A {@link Dataset} backed by the RDF4J repository.
	 */
	public RDF4JDataset asRDFTermDataset(Repository repository) {
		return new RepositoryDatasetImpl(repository);
	}

	/**
	 * Adapt an RDF4J {@link Repository} as a Commons RDF {@link Dataset}.
	 * <p>
	 * Changes to the dataset are reflected in the repository, and vice versa.
	 * 
	 * @param repository
	 *            RDF4J {@link Repository} to connect to.
	 * @param includeInferred
	 *            If true, any inferred quads are included in the dataset
	 * @return A {@link Dataset} backed by the RDF4J repository.
	 */
	public RDF4JDataset asRDFTermDataset(Repository repository, boolean includeInferred) {
		return new RepositoryDatasetImpl(repository, includeInferred);
	}
	
	/**
	 * Adapt an RDF4J {@link Model} as a Commons RDF {@link Graph}.
	 * <p>
	 * Changes to the graph are reflected in the model, and vice versa.
	 * 
	 * @param model
	 *            RDF4J {@link Model} to adapt.
	 * @return Adapted {@link Graph}.
	 */
	public RDF4JGraph asRDFTermGraph(Model model) {
		return new ModelGraphImpl(model);
	}

	/**
	 * Adapt an RDF4J {@link Repository} as a Commons RDF {@link Graph}.
	 * <p>
	 * The graph will include triples in any contexts (e.g. the union graph).
	 * <p>
	 * Changes to the graph are reflected in the repository, and vice versa.
	 * 
	 * @param repository
	 *            RDF4J {@link Repository} to connect to.
	 * @return A {@link Graph} backed by the RDF4J repository.
	 */
	public RDF4JGraph asRDFTermGraph(Repository repository) {
		return new RepositoryGraphImpl(repository, false, true);
	}

	/**
	 * Adapt an RDF4J {@link Repository} as a Commons RDF {@link Graph}.
	 * <p>
	 * The graph will include triples in any contexts (e.g. the union graph).
	 * <p>
	 * Changes to the graph are reflected in the repository, and vice versa.
	 * 
	 * @param repository
	 *            RDF4J {@link Repository} to connect to.
	 * @param includeInferred
	 *            If true, any inferred triples are included in the graph
	 * @param unionGraph
	 *            If true, triples from any context is included in the graph,
	 *            otherwise only triples in the default context
	 *            <code>null</code>.
	 * @return A {@link Graph} backed by the RDF4J repository.
	 */
	public RDF4JGraph asRDFTermGraph(Repository repository, boolean includeInferred, boolean unionGraph) {
		return new RepositoryGraphImpl(repository, includeInferred, unionGraph);
	}

	public Statement asStatement(TripleLike<BlankNodeOrIRI, org.apache.commons.rdf.api.IRI, RDFTerm> tripleLike) {
		if (tripleLike instanceof RDF4JTripleLike) {
			// Return original statement - this covers both RDF4JQuad and
			// RDF4JTriple
			RDF4JTripleLike rdf4jTriple = (RDF4JTripleLike) tripleLike;
			return rdf4jTriple.asStatement();
		}

		org.eclipse.rdf4j.model.Resource subject = (org.eclipse.rdf4j.model.Resource) asValue(tripleLike.getSubject());
		org.eclipse.rdf4j.model.IRI predicate = (org.eclipse.rdf4j.model.IRI) asValue(tripleLike.getPredicate());
		Value object = asValue(tripleLike.getObject());

		org.eclipse.rdf4j.model.Resource context = null;
		if (tripleLike instanceof Quad) {
			Quad quad = (Quad) tripleLike;
			context = (org.eclipse.rdf4j.model.Resource) asValue(quad.getGraphName().orElse(null));
		}

		return valueFactory.createStatement(subject, predicate, object, context);
	}

	/**
	 * Adapt a RDF4J {@link Statement} as a Commons RDF {@link Triple}.
	 * 
	 * @param statement
	 * @return A {@link RDF4JTriple} that is equivalent to the statement
	 */
	public RDF4JTriple asTriple(final Statement statement) {
		return new TripleImpl(statement, salt);
	}

	/**
	 * Adapt a Commons RDF {@link RDFTerm} as a RDF4J {@link Value}.
	 * <p>
	 * The value will be of the same kind as the term, e.g. a
	 * {@link org.apache.commons.rdf.api.BlankNode} is converted to a
	 * {@link org.eclipse.rdf4j.model.BNode}, a
	 * {@link org.apache.commons.rdf.api.IRI} is converted to a
	 * {@link org.eclipse.rdf4j.model.IRI} and a
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
	 * @param term
	 *            RDFTerm to adapt to RDF4J Value
	 * @return Adapted RDF4J {@link Value}
	 */
	public Value asValue(RDFTerm term) {
		if (term == null) {
			return null;
		}
		if (term instanceof RDF4JTerm) {
			// One of our own - avoid converting again.
			// (This is crucial to avoid double-escaping in BlankNode)
			return ((RDF4JTerm<?>) term).asValue();
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
			org.eclipse.rdf4j.model.IRI dataType = (org.eclipse.rdf4j.model.IRI) asValue(literal.getDatatype());
			return valueFactory.createLiteral(label, dataType);
		}
		if (term instanceof BlankNode) {
			// This is where it gets tricky to support round trips!
			BlankNode blankNode = (BlankNode) term;
			// FIXME: The uniqueReference might not be a valid BlankNode
			// identifier..
			// does it have to be in RDF4J?
			return valueFactory.createBNode(blankNode.uniqueReference());
		}
		throw new IllegalArgumentException("RDFTerm was not an IRI, Literal or BlankNode: " + term.getClass());
	}

	@Override
	public RDF4JBlankNode createBlankNode() throws UnsupportedOperationException {
		BNode bnode = valueFactory.createBNode();
		return (RDF4JBlankNode) asRDFTerm(bnode);
	}

	@Override
	public RDF4JBlankNode createBlankNode(String name) throws UnsupportedOperationException {
		BNode bnode = valueFactory.createBNode(name);
		return (RDF4JBlankNode) asRDFTerm(bnode);
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
	public RDF4JLiteral createLiteral(String lexicalForm)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.eclipse.rdf4j.model.Literal lit = valueFactory.createLiteral(lexicalForm);
		return (RDF4JLiteral) asRDFTerm(lit);
	}

	@Override
	public org.apache.commons.rdf.api.Literal createLiteral(String lexicalForm, org.apache.commons.rdf.api.IRI dataType)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.eclipse.rdf4j.model.IRI iri = valueFactory.createIRI(dataType.getIRIString());
		org.eclipse.rdf4j.model.Literal lit = valueFactory.createLiteral(lexicalForm, iri);
		return (org.apache.commons.rdf.api.Literal) asRDFTerm(lit);
	}

	@Override
	public org.apache.commons.rdf.api.Literal createLiteral(String lexicalForm, String languageTag)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.eclipse.rdf4j.model.Literal lit = valueFactory.createLiteral(lexicalForm, languageTag);
		return (org.apache.commons.rdf.api.Literal) asRDFTerm(lit);
	}

	@Override
	public RDF4JTriple createTriple(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object)
			throws IllegalArgumentException, UnsupportedOperationException {
		final Statement statement = valueFactory.createStatement((org.eclipse.rdf4j.model.Resource) asValue(subject),
				(org.eclipse.rdf4j.model.IRI) asValue(predicate), asValue(object));
		return asTriple(statement);
	}

}
