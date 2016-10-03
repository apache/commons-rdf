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

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

// To avoid confusion, avoid importing
// classes that are in both
// commons.rdf and openrdf.model (e.g. IRI)
import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
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
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * RDF4J implementation of RDFTermFactory
 * <p>
 * The {@link #RDF4JTermFactory()} constructor uses a {@link SimpleValueFactory}
 * to create corresponding RDF4J {@link Value} instances. Alternatively, this
 * factory can be constructed with a different {@link ValueFactory} using
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
 * To adapt a RDF4J {@link Repository} as a {@link Dataset} or {@link Graph},
 * use {@link #asRDFTermDataset(Repository)} or
 * {@link #asRDFTermGraph(Repository)}.
 * <p>
 * {@link #asTriple(Statement)} can be used to convert a RDF4J {@link Statement}
 * to a Commons RDF {@link Triple}, and equivalent {@link #asQuad(Statement)} to
 * convert a {@link Quad}.
 * <p>
 * To convert any {@link Triple} or {@link Quad} to to RDF4J {@link Statement},
 * use {@link #asStatement(TripleLike)}. This recognises previously converted
 * {@link RDF4JTriple}s and {@link RDF4JQuad}s without re-converting their
 * {@link RDF4JTripleLike#asStatement()}.
 * <p>
 * Likewise, {@link #asValue(RDFTerm)} can be used to convert any Commons RDF
 * {@link RDFTerm} to a corresponding RDF4J {@link Value}. This recognises
 * previously converted {@link RDF4JTerm}s without re-converting their
 * {@link RDF4JTerm#asValue()}.
 * <p>
 * For the purpose of {@link BlankNode} equivalence, this factory contains an
 * internal {@link UUID} salt that is used by adapter methods like
 * {@link #asQuad(Statement)}, {@link #asTriple(Statement)},
 * {@link #asRDFTerm(Value)} as well as {@link #createBlankNode(String)}. As
 * RDF4J {@link BNode} instances from multiple repositories or models may have
 * the same {@link BNode#getID()}, converting them with the above methods might
 * cause accidental {@link BlankNode} equivalence. Note that the {@link Graph}
 * and {@link Dataset} adapter methods like
 * {@link #asRDFTermDataset(Repository)} and {@link #asRDFTermGraph(Model)}
 * therefore uses a unique {@link RDF4JTermFactory} internally. An alternative
 * is to use the static methods {@link #asRDFTerm(Value, UUID)},
 * {@link #asQuad(Statement, UUID)} or {@link #asTriple(Statement, UUID)} with
 * a provided {@link UUID} salt.
 *
 */
public class RDF4JTermFactory implements RDFTermFactory {

	/**
	 * Adapt a RDF4J {@link Value} as a Commons RDF {@link RDFTerm}.
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
	 *            The RDF4J {@link Value} to convert.
	 * @param salt
	 *            A {@link UUID} salt to use for uniquely mapping any
	 *            {@link BNode}s. The salt should typically be the same for
	 *            multiple statements in the same {@link Repository} or
	 *            {@link Model} to ensure {@link BlankNode#equals(Object)} and
	 *            {@link BlankNode#uniqueReference()} works as intended.
	 * @param <T>
	 *            The subclass of {@link Value}, e.g. {@link BNode}
	 * @return A {@link RDFTerm} that corresponds to the RDF4J value
	 * @throws IllegalArgumentException
	 *             if the value is not a BNode, Literal or IRI
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Value> RDF4JTerm<T> asRDFTerm(final T value, UUID salt) {
		if (value instanceof BNode) {
			return (RDF4JTerm<T>) new BlankNodeImpl((BNode) value, salt);
		}
		if (value instanceof org.eclipse.rdf4j.model.Literal) {
			return (RDF4JTerm<T>) new LiteralImpl((org.eclipse.rdf4j.model.Literal) value);
		}
		if (value instanceof org.eclipse.rdf4j.model.IRI) {
			return (RDF4JTerm<T>) new IRIImpl((org.eclipse.rdf4j.model.IRI) value);
		}
		throw new IllegalArgumentException("Value is not a BNode, Literal or IRI: " + value.getClass());
	}

	private final UUID salt;

	private final ValueFactory valueFactory;

	public RDF4JTermFactory() {
		this(SimpleValueFactory.getInstance(), UUID.randomUUID());
	}

	public RDF4JTermFactory(ValueFactory valueFactory) {
		this(valueFactory, UUID.randomUUID());
	}

	public RDF4JTermFactory(ValueFactory valueFactory, UUID salt) {
		this.valueFactory = valueFactory;
		this.salt = salt;
	}
	
	/**
	 * Adapt a RDF4J {@link Statement} as a Commons RDF {@link Quad}.
	 * <p>
	 * For the purpose of {@link BlankNode} equivalence, this
	 * method will use an internal salt UUID that is unique per instance of
	 * {@link RDF4JTermFactory}.
	 * <p>
	 * <strong>NOTE:</strong> If combining RDF4J {@link Statement}s
	 * multiple repositories or models, then their {@link BNode}s
	 * may have the same {@link BNode#getID()}, which with this method
	 * would become equivalent according to {@link BlankNode#equals(Object)} and
	 * {@link BlankNode#uniqueReference()},
	 * unless a separate {@link RDF4JTermFactory}
	 * instance is used per RDF4J repository/model.
	 *
	 * @see #asQuad(Statement, UUID)
	 * @param statement
	 *            The statement to convert
	 * @return A {@link RDF4JQuad} that is equivalent to the statement
	 */
	public RDF4JQuad asQuad(final Statement statement) {
		return new QuadImpl(statement, salt);
	}

	/**
	 * Adapt a RDF4J {@link Statement} as a Commons RDF {@link Quad}.
	 *
	 * @see #asQuad(Statement)
	 * @param statement
	 *            The statement to convert
	 * @param salt
	 *            A {@link UUID} salt to use for uniquely mapping any
	 *            {@link BNode}s. The salt should typically be the same for
	 *            multiple statements in the same {@link Repository} or
	 *            {@link Model} to ensure {@link BlankNode#equals(Object)} and
	 *            {@link BlankNode#uniqueReference()} works as intended.
	 * @return A {@link RDF4JQuad} that is equivalent to the statement
	 */
	public static RDF4JQuad asQuad(final Statement statement, UUID salt) {
		return new QuadImpl(statement, salt);
	}


	/**
	 *
	 * Adapt a RDF4J {@link Value} as a Commons RDF {@link RDFTerm}.
	 * <p>
	 * The value will be of the same kind as the term, e.g. a
	 * {@link org.eclipse.rdf4j.model.BNode} is converted to a
	 * {@link org.apache.commons.rdf.api.BlankNode}, a
	 * {@link org.eclipse.rdf4j.model.IRI} is converted to a
	 * {@link org.apache.commons.rdf.api.IRI} and a
	 * {@link org.eclipse.rdf4j.model.Literal}. is converted to a
	 * {@link org.apache.commons.rdf.api.Literal}
	 * <p>
	 * For the purpose of {@link BlankNode} equivalence, this
	 * method will use an internal salt UUID that is unique per instance of
	 * {@link RDF4JTermFactory}.
	 * <p>
	 * <strong>NOTE:</strong> If combining RDF4J values from
	 * multiple repositories or models, then their {@link BNode}s
	 * may have the same {@link BNode#getID()}, which with this method
	 * would become equivalent according to {@link BlankNode#equals(Object)} and
	 * {@link BlankNode#uniqueReference()},
	 * unless a separate {@link RDF4JTermFactory}
	 * instance is used per RDF4J repository/model.
	 *
	 * @param value The RDF4J {@link Value} to convert.
	 * @param <T>
	 *            The subclass of {@link Value}, e.g. {@link BNode}
	 * @return A {@link RDFTerm} that corresponds to the RDF4J value
	 * @throws IllegalArgumentException if the value is not a BNode, Literal or IRI
	 */
	public <T extends Value> RDF4JTerm<T> asRDFTerm(T value) {
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
		return new RepositoryDatasetImpl(repository, false, false);
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
		return new RepositoryDatasetImpl(repository, false, includeInferred);
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
	 * The graph will only include triples in the default graph 
	 * (equivalent to context <code>new Resource[0]{null})</code> in RDF4J).
	 * <p>
	 * Changes to the graph are reflected in the repository, and vice versa.
	 *
	 * @param repository
	 *            RDF4J {@link Repository} to connect to.
	 * @return A {@link Graph} backed by the RDF4J repository.
	 */
	public RDF4JGraph asRDFTermGraph(Repository repository) {
		return new RepositoryGraphImpl(repository, false, false);
	}

	/**
	 * Adapt an RDF4J {@link Repository} as a Commons RDF {@link Graph}.
	 * <p>
	 * The graph will include triples in any contexts (e.g. the union graph).
	 * <p>
	 * Changes to the graph are reflected in the repository, and vice versa.
	 * Triples added to the graph are added to the default context, 
	 * e.g. an RDF4J context of new <code>Resource[1]{null})</code>.
	 *
	 * @param repository
	 *            RDF4J {@link Repository} to connect to.
	 * @return A union {@link Graph} backed by the RDF4J repository.
	 */
	public RDF4JGraph asRDFTermGraphUnion(Repository repository) {
		return new RepositoryGraphImpl(repository, false, true);
	}
	
	/**
	 * Adapt an RDF4J {@link Repository} as a Commons RDF {@link Graph}.
	 * <p>
	 * The graph will include triples in the specified contexts.
	 * <p>
	 * Changes to the graph are reflected in the repository, and vice versa.
	 * Triples added/removed to the graph are reflected in all the specified
	 * contexts.
	 *
	 * @param repository
	 *            RDF4J {@link Repository} to connect to.
	 * @param contexts
	 * 
	 * @return A {@link Graph} backed by the RDF4J repository.
	 */
	public RDF4JGraph asRDFTermGraph(Repository repository, BlankNodeOrIRI... contexts) {
		if (contexts.length == 0) {
			throw new IllegalArgumentException("At least one context must be specified. Use asRDFTermGraphUnion for union graph.");
		}
		Resource[] resources = new Resource[contexts.length];
		for (int i=0; i<contexts.length; i++) {
			resources[i] = (Resource) asValue(contexts[i]);
		}
		return new RepositoryGraphImpl(repository, false, true, resources);
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

	/**
	 * Adapt a Commons RDF {@link Triple} or {@link Quad} as a RDF4J
	 * {@link Statement}.
	 * <p>
	 * If the <code>tripleLike</code> argument is an {@link RDF4JTriple} or
	 * a {@link RDF4JQuad}, then its {@link RDF4JTripleLike#asStatement()} is
	 * returned as-is. Note that this means that a {@link RDF4JTriple} would
	 * preserve its {@link Statement#getContext()}, and that any
	 * {@link BlankNode}s would be deemed equivalent in RDF4J
	 * if they have the same {@link BNode#getID()}.
	 *
	 * @param tripleLike
	 *            A {@link Triple} or {@link Quad} to adapt
	 * @return A corresponding {@link Statement}
	 */
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

		return getValueFactory().createStatement(subject, predicate, object, context);
	}

	/**
	 * Adapt a RDF4J {@link Statement} as a Commons RDF {@link Triple}.
	 * <p>
	 * For the purpose of {@link BlankNode} equivalence, this method will use an
	 * internal salt UUID that is unique per instance of
	 * {@link RDF4JTermFactory}.
	 * <p>
	 * <strong>NOTE:</strong> If combining RDF4J statements from multiple
	 * repositories or models, then their {@link BNode}s may have the same
	 * {@link BNode#getID()}, which with this method would become equivalent
	 * according to {@link BlankNode#equals(Object)} and
	 * {@link BlankNode#uniqueReference()}, unless a separate
	 * {@link RDF4JTermFactory} instance is used per RDF4J repository/model.
	 *
	 * @param statement
	 *            The RDF4J {@link Statement} to adapt.
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
			return getValueFactory().createIRI(iri.getIRIString());
		}
		if (term instanceof org.apache.commons.rdf.api.Literal) {
			org.apache.commons.rdf.api.Literal literal = (org.apache.commons.rdf.api.Literal) term;
			String label = literal.getLexicalForm();
			if (literal.getLanguageTag().isPresent()) {
				String lang = literal.getLanguageTag().get();
				return getValueFactory().createLiteral(label, lang);
			}
			org.eclipse.rdf4j.model.IRI dataType = (org.eclipse.rdf4j.model.IRI) asValue(literal.getDatatype());
			return getValueFactory().createLiteral(label, dataType);
		}
		if (term instanceof BlankNode) {
			// This is where it gets tricky to support round trips!
			BlankNode blankNode = (BlankNode) term;
			// FIXME: The uniqueReference might not be a valid BlankNode
			// identifier..
			// does it have to be in RDF4J?
			return getValueFactory().createBNode(blankNode.uniqueReference());
		}
		throw new IllegalArgumentException("RDFTerm was not an IRI, Literal or BlankNode: " + term.getClass());
	}

	@Override
	public RDF4JBlankNode createBlankNode() throws UnsupportedOperationException {
		BNode bnode = getValueFactory().createBNode();
		return (RDF4JBlankNode) asRDFTerm(bnode);
	}

	@Override
	public RDF4JBlankNode createBlankNode(String name) throws UnsupportedOperationException {
		BNode bnode = getValueFactory().createBNode(name);
		return (RDF4JBlankNode) asRDFTerm(bnode);
	}

	@Override
	public Dataset createDataset() {
		Sail sail = new MemoryStore();
		Repository repository = new SailRepository(sail);
		return asRDFTermDataset(repository);
	}

	@Override
	public RDF4JGraph createGraph() throws UnsupportedOperationException {
		return asRDFTermGraph(new LinkedHashModel());
	}

	@Override
	public RDF4JIRI createIRI(String iri) throws IllegalArgumentException, UnsupportedOperationException {
		return (RDF4JIRI) asRDFTerm(getValueFactory().createIRI(iri));
	}

	@Override
	public RDF4JLiteral createLiteral(String lexicalForm)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.eclipse.rdf4j.model.Literal lit = getValueFactory().createLiteral(lexicalForm);
		return (RDF4JLiteral) asRDFTerm(lit);
	}

	@Override
	public org.apache.commons.rdf.api.Literal createLiteral(String lexicalForm, org.apache.commons.rdf.api.IRI dataType)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.eclipse.rdf4j.model.IRI iri = getValueFactory().createIRI(dataType.getIRIString());
		org.eclipse.rdf4j.model.Literal lit = getValueFactory().createLiteral(lexicalForm, iri);
		return (org.apache.commons.rdf.api.Literal) asRDFTerm(lit);
	}

	@Override
	public org.apache.commons.rdf.api.Literal createLiteral(String lexicalForm, String languageTag)
			throws IllegalArgumentException, UnsupportedOperationException {
		org.eclipse.rdf4j.model.Literal lit = getValueFactory().createLiteral(lexicalForm, languageTag);
		return (org.apache.commons.rdf.api.Literal) asRDFTerm(lit);
	}

	@Override
	public RDF4JTriple createTriple(BlankNodeOrIRI subject, org.apache.commons.rdf.api.IRI predicate, RDFTerm object)
			throws IllegalArgumentException, UnsupportedOperationException {
		final Statement statement = getValueFactory().createStatement(
				(org.eclipse.rdf4j.model.Resource) asValue(subject),
				(org.eclipse.rdf4j.model.IRI) asValue(predicate),
				asValue(object));
		return asTriple(statement);
	}

	@Override
	public Quad createQuad(BlankNodeOrIRI graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object)
			throws IllegalArgumentException, UnsupportedOperationException {
		final Statement statement = getValueFactory().createStatement(
				(org.eclipse.rdf4j.model.Resource) asValue(subject),
				(org.eclipse.rdf4j.model.IRI) asValue(predicate),
				asValue(object),
				(org.eclipse.rdf4j.model.Resource)asValue(graphName));
		return asQuad(statement);
	}

	public ValueFactory getValueFactory() {
		return valueFactory;
	}

}
