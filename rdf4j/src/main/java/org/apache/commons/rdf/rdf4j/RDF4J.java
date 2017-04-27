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
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

// To avoid confusion, avoid importing
// classes that are in both
// commons.rdf and openrdf.model (e.g. IRI, Literal)
import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.api.TripleLike;
import org.apache.commons.rdf.rdf4j.impl.InternalRDF4JFactory;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.memory.MemoryStore;

/**
 * RDF4J implementation of RDF.
 * <p>
 * The {@link #RDF4J()} constructor uses a {@link SimpleValueFactory} to create
 * corresponding RDF4J {@link Value} instances. Alternatively, this factory can
 * be constructed with a different {@link ValueFactory} using
 * {@link #RDF4J(ValueFactory)}.
 * <p>
 * {@link #asRDFTerm(Value)} can be used to convert any RDF4J {@link Value} to
 * an RDFTerm. Note that adapted {@link BNode}s are considered equal if they are
 * converted with the same {@link RDF4J} instance and have the same
 * {@link BNode#getID()}.
 * <p>
 * {@link #createGraph()} creates a new Graph backed by {@link LinkedHashModel}.
 * To use other models, see {@link #asGraph(Model)}.
 * <p>
 * To adapt a RDF4J {@link Repository} as a {@link Dataset} or {@link Graph},
 * use {@link #asDataset(Repository, Option...)} or
 * {@link #asGraph(Repository, Option...)}.
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
 * {@link #asDataset(Repository, Option...)} and
 * {@link #asGraph(Repository, Option...)} therefore uses a unique {@link RDF4J}
 * internally.
 * 
 * @see RDF
 *
 */
public final class RDF4J implements RDF {

    /**
     * InternalRDF4JFactory is deliberately abstract
     */
    private static InternalRDF4JFactory rdf4jFactory = new InternalRDF4JFactory() {
    };

    public enum Option {
        /**
         * The Graph/Dataset should include any inferred statements
         */
        includeInferred,
        /**
         * The graph/dataset should handle {@link Repository#initialize()} (if
         * needed) and {@link Repository#shutDown()} on {@link Graph#close()} /
         * {@link Dataset#close()}.
         */
        handleInitAndShutdown
    }

    private final UUID salt;

    private final ValueFactory valueFactory;

    /**
     * Construct an {@link RDF4J}.
     * 
     */
    public RDF4J() {
        this(SimpleValueFactory.getInstance(), UUID.randomUUID());
    }

    /**
     * Construct an {@link RDF4J}.
     * <p>
     * This constructor is intended for use with the value factory from
     * {@link Repository#getValueFactory()} when using Repository-based graphs
     * and datasets.
     * 
     * @param valueFactory
     *            The RDF4J {@link ValueFactory} to use
     */
    public RDF4J(final ValueFactory valueFactory) {
        this(valueFactory, UUID.randomUUID());
    }

    /**
     * Construct an {@link RDF4J}.
     * <p>
     * This constructor may be used if reproducible
     * {@link BlankNode#uniqueReference()} in {@link BlankNode} is desirable.
     * 
     * @param salt
     *            An {@link UUID} salt to be used by any created
     *            {@link BlankNode}s for the purpose of
     *            {@link BlankNode#uniqueReference()}
     */
    public RDF4J(final UUID salt) {
        this(SimpleValueFactory.getInstance(), salt);
    }

    /**
     * Construct an {@link RDF4J}.
     * <p>
     * This constructor may be used if reproducible
     * {@link BlankNode#uniqueReference()} in {@link BlankNode} is desirable.
     * 
     * @param valueFactory
     *            The RDF4J {@link ValueFactory} to use
     * @param salt
     *            An {@link UUID} salt to be used by any created
     *            {@link BlankNode}s for the purpose of
     *            {@link BlankNode#uniqueReference()}
     */
    public RDF4J(final ValueFactory valueFactory, final UUID salt) {
        this.valueFactory = valueFactory;
        this.salt = salt;
    }

    /**
     * Adapt a RDF4J {@link Statement} as a Commons RDF {@link Quad}.
     * <p>
     * For the purpose of {@link BlankNode} equivalence, this method will use an
     * internal salt UUID that is unique per instance of {@link RDF4J}.
     * <p>
     * <strong>NOTE:</strong> If combining RDF4J {@link Statement}s multiple
     * repositories or models, then their {@link BNode}s may have the same
     * {@link BNode#getID()}, which with this method would become equivalent
     * according to {@link BlankNode#equals(Object)} and
     * {@link BlankNode#uniqueReference()}, unless a separate {@link RDF4J}
     * instance is used per RDF4J repository/model.
     *
     * @param statement
     *            The statement to convert
     * @return A {@link RDF4JQuad} that is equivalent to the statement
     */
    public RDF4JQuad asQuad(final Statement statement) {
        return rdf4jFactory.createQuadImpl(statement, salt);
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
     * For the purpose of {@link BlankNode} equivalence, this method will use an
     * internal salt UUID that is unique per instance of {@link RDF4J}.
     * <p>
     * <strong>NOTE:</strong> If combining RDF4J values from multiple
     * repositories or models, then their {@link BNode}s may have the same
     * {@link BNode#getID()}, which with this method would become equivalent
     * according to {@link BlankNode#equals(Object)} and
     * {@link BlankNode#uniqueReference()}, unless a separate {@link RDF4J}
     * instance is used per RDF4J repository/model.
     *
     * @param value
     *            The RDF4J {@link Value} to convert.
     * @return A {@link RDFTerm} that corresponds to the RDF4J value
     * @throws IllegalArgumentException
     *             if the value is not a BNode, Literal or IRI
     */
    public RDF4JTerm asRDFTerm(final Value value) {
        return asRDFTerm(value, salt);
    }

    /**
     *
     * Adapt a RDF4J
     * {@link org.eclipse.rdf4j.model.BNode} as a Commons RDF
     * {@link org.apache.commons.rdf.api.BlankNode}
     * <p>
     * For the purpose of {@link BlankNode} equivalence, this method will use an
     * internal salt UUID that is unique per instance of {@link RDF4J}.
     * <p>
     * <strong>NOTE:</strong> If combining RDF4J values from multiple
     * repositories or models, then their {@link BNode}s may have the same
     * {@link BNode#getID()}, which with this method would become equivalent
     * according to {@link BlankNode#equals(Object)} and
     * {@link BlankNode#uniqueReference()}, unless a separate {@link RDF4J}
     * instance is used per RDF4J repository/model.
     *
     * @param value
     *            The RDF4J {@link BNode} to convert.
     * @return A {@link RDF4JBlankNode} that corresponds to the RDF4J BNode
     */
    public RDF4JBlankNode asRDFTerm(final BNode value) {
        return rdf4jFactory.createBlankNodeImpl(value, salt);
    }

    /**
     *
     * Adapt a RDF4J
     * {@link org.eclipse.rdf4j.model.Literal} as a Commons RDF
     * {@link org.apache.commons.rdf.api.Literal}
     * <p>
     * @param value
     *            The RDF4J {@link Literal} to convert.
     * @return A {@link RDF4JLiteral} that corresponds to the RDF4J literal
     */
    public RDF4JLiteral asRDFTerm(final Literal value) {
        return rdf4jFactory.createLiteralImpl(value);
    }

    /**
     *
     * Adapt a RDF4J
     * {@link org.eclipse.rdf4j.model.IRI} as a Commons RDF
     * {@link org.apache.commons.rdf.api.IRI}
     * <p>
     * @param value
     *            The RDF4J {@link Value} to convert.
     * @return A {@link RDF4JIRI} that corresponds to the RDF4J IRI
     */
    public RDF4JIRI asRDFTerm(final org.eclipse.rdf4j.model.IRI value) {
        return rdf4jFactory.createIRIImpl(value);
    }

    /**
     *
     * Adapt a RDF4J
     * {@link org.eclipse.rdf4j.model.Resource} as a Commons RDF
     * {@link org.apache.commons.rdf.api.BlankNodeOrIRI}
     * <p>
     * @param value
     *            The RDF4J {@link Value} to convert.
     * @return A {@link RDF4JBlankNodeOrIRI} that corresponds to the RDF4J Resource
     */
    public RDF4JBlankNodeOrIRI asRDFTerm(final org.eclipse.rdf4j.model.Resource value) {
        if(value instanceof IRI){
            return asRDFTerm((IRI)value);
        } else if (value instanceof BNode){
            return asRDFTerm((BNode)value);
        }
        throw new IllegalArgumentException("Value is not a BNode or IRI: " + value.getClass());
    }

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
     * @return A {@link RDFTerm} that corresponds to the RDF4J value
     * @throws IllegalArgumentException
     *             if the value is not a BNode, Literal or IRI
     */
    public static RDF4JTerm asRDFTerm(final Value value, final UUID salt) {
        if (value instanceof BNode) {
            return rdf4jFactory.createBlankNodeImpl((BNode) value, salt);
        }
        if (value instanceof org.eclipse.rdf4j.model.Literal) {
            return rdf4jFactory.createLiteralImpl((org.eclipse.rdf4j.model.Literal) value);
        }
        if (value instanceof org.eclipse.rdf4j.model.IRI) {
            return rdf4jFactory.createIRIImpl((org.eclipse.rdf4j.model.IRI) value);
        }
        throw new IllegalArgumentException("Value is not a BNode, Literal or IRI: " + value.getClass());
    }

    /**
     * Adapt an RDF4J {@link Repository} as a Commons RDF {@link Dataset}.
     * <p>
     * Changes to the dataset are reflected in the repository, and vice versa.
     * <p>
     * <strong>Note:</strong> Some operations on the {@link RDF4JDataset}
     * requires the use of try-with-resources to close underlying
     * {@link RepositoryConnection}s, including {@link RDF4JDataset#iterate()},
     * {@link RDF4JDataset#stream()} and {@link RDF4JDataset#getGraphNames()}.
     *
     * @param repository
     *            RDF4J {@link Repository} to connect to.
     * @param options
     *            Zero or more {@link Option}
     * @return A {@link Dataset} backed by the RDF4J repository.
     */
    public RDF4JDataset asDataset(final Repository repository, final Option... options) {
        final EnumSet<Option> opts = optionSet(options);
        return rdf4jFactory.createRepositoryDatasetImpl(repository, opts.contains(Option.handleInitAndShutdown),
                opts.contains(Option.includeInferred));
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
    public RDF4JGraph asGraph(final Model model) {
        return rdf4jFactory.createModelGraphImpl(model, this);
    }

    /**
     * Adapt an RDF4J {@link Repository} as a Commons RDF {@link Graph}.
     * <p>
     * The graph will only include triples in the default graph (equivalent to
     * context <code>new Resource[0]{null})</code> in RDF4J).
     * <p>
     * Changes to the graph are reflected in the repository, and vice versa.
     * <p>
     * <strong>Note:</strong> Some operations on the {@link RDF4JGraph} requires
     * the use of try-with-resources to close underlying
     * {@link RepositoryConnection}s, including {@link RDF4JGraph#iterate()} and
     * {@link RDF4JGraph#stream()}.
     *
     * @param repository
     *            RDF4J {@link Repository} to connect to.
     * @param options
     *            Zero or more {@link Option}
     * @return A {@link Graph} backed by the RDF4J repository.
     */
    public RDF4JGraph asGraph(final Repository repository, final Option... options) {
        final EnumSet<Option> opts = optionSet(options);
        return rdf4jFactory.createRepositoryGraphImpl(repository, opts.contains(Option.handleInitAndShutdown),
                opts.contains(Option.includeInferred), new Resource[] { null }); // default
                                                                                 // graph
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
     * @param options
     *            Zero or more {@link Option}
     * @return A union {@link Graph} backed by the RDF4J repository.
     */
    public RDF4JGraph asGraphUnion(final Repository repository, final Option... options) {
        final EnumSet<Option> opts = optionSet(options);
        return rdf4jFactory.createRepositoryGraphImpl(repository, opts.contains(Option.handleInitAndShutdown),
                opts.contains(Option.includeInferred), new Resource[] {}); // union
                                                                           // graph

    }

    /**
     * Adapt an RDF4J {@link Repository} as a Commons RDF {@link Graph}.
     * <p>
     * The graph will include triples in the specified contexts.
     * <p>
     * Changes to the graph are reflected in the repository, and vice versa.
     * Triples added/removed to the graph are reflected in all the specified
     * contexts.
     * <p>
     * <strong>Note:</strong> Some operations on the {@link RDF4JGraph} requires
     * the use of try-with-resources to close underlying
     * {@link RepositoryConnection}s, including {@link RDF4JGraph#iterate()} and
     * {@link RDF4JGraph#stream()}.
     *
     * @param repository
     *            RDF4J {@link Repository} to connect to.
     * @param contexts
     *            A {@link Set} of {@link BlankNodeOrIRI} specifying the graph
     *            names to use as a context. The set may include the value
     *            <code>null</code> to indicate the default graph. The empty set
     *            indicates any context, e.g. the <em>union graph</em>.
     * @param option
     *            Zero or more {@link Option}s
     * @return A {@link Graph} backed by the RDF4J repository.
     */
    public RDF4JGraph asGraph(final Repository repository, final Set<? extends BlankNodeOrIRI> contexts, final Option... option) {
        final EnumSet<Option> opts = optionSet(option);
        /** NOTE: asValue() deliberately CAN handle <code>null</code> */
        final Resource[] resources = contexts.stream().map(g -> (Resource) asValue(g)).toArray(Resource[]::new);
        return rdf4jFactory.createRepositoryGraphImpl(Objects.requireNonNull(repository),
                opts.contains(Option.handleInitAndShutdown), opts.contains(Option.includeInferred), resources);
    }

    /**
     * Adapt a Commons RDF {@link Triple} or {@link Quad} as a RDF4J
     * {@link Statement}.
     * <p>
     * If the <code>tripleLike</code> argument is an {@link RDF4JTriple} or a
     * {@link RDF4JQuad}, then its {@link RDF4JTripleLike#asStatement()} is
     * returned as-is. Note that this means that a {@link RDF4JTriple} would
     * preserve its {@link Statement#getContext()}, and that any
     * {@link BlankNode}s would be deemed equivalent in RDF4J if they have the
     * same {@link BNode#getID()}.
     *
     * @param tripleLike
     *            A {@link Triple} or {@link Quad} to adapt
     * @return A corresponding {@link Statement}
     */
    public Statement asStatement(final TripleLike tripleLike) {
        if (tripleLike instanceof RDF4JTripleLike) {
            // Return original statement - this covers both RDF4JQuad and
            // RDF4JTriple
            return ((RDF4JTripleLike) tripleLike).asStatement();
        }

        final org.eclipse.rdf4j.model.Resource subject = (org.eclipse.rdf4j.model.Resource) asValue(tripleLike.getSubject());
        final org.eclipse.rdf4j.model.IRI predicate = (org.eclipse.rdf4j.model.IRI) asValue(tripleLike.getPredicate());
        final Value object = asValue(tripleLike.getObject());

        org.eclipse.rdf4j.model.Resource context = null;
        if (tripleLike instanceof Quad) {
            final Quad quad = (Quad) tripleLike;
            context = (org.eclipse.rdf4j.model.Resource) asValue(quad.getGraphName().orElse(null));
        }

        return getValueFactory().createStatement(subject, predicate, object, context);
    }

    /**
     * Adapt a RDF4J {@link Statement} as a Commons RDF {@link Triple}.
     * <p>
     * For the purpose of {@link BlankNode} equivalence, this method will use an
     * internal salt UUID that is unique per instance of {@link RDF4J}.
     * <p>
     * <strong>NOTE:</strong> If combining RDF4J statements from multiple
     * repositories or models, then their {@link BNode}s may have the same
     * {@link BNode#getID()}, which with this method would become equivalent
     * according to {@link BlankNode#equals(Object)} and
     * {@link BlankNode#uniqueReference()}, unless a separate {@link RDF4J}
     * instance is used per RDF4J repository/model.
     *
     * @param statement
     *            The RDF4J {@link Statement} to adapt.
     * @return A {@link RDF4JTriple} that is equivalent to the statement
     */
    public RDF4JTriple asTriple(final Statement statement) {
        return rdf4jFactory.createTripleImpl(statement, salt);
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
    public Value asValue(final RDFTerm term) {
        if (term == null) {
            return null;
        }
        if (term instanceof RDF4JTerm) {
            // One of our own - avoid converting again.
            // (This is crucial to avoid double-escaping in BlankNode)
            return ((RDF4JTerm) term).asValue();
        }
        if (term instanceof org.apache.commons.rdf.api.IRI) {
            final org.apache.commons.rdf.api.IRI iri = (org.apache.commons.rdf.api.IRI) term;
            return getValueFactory().createIRI(iri.getIRIString());
        }
        if (term instanceof org.apache.commons.rdf.api.Literal) {
            final org.apache.commons.rdf.api.Literal literal = (org.apache.commons.rdf.api.Literal) term;
            final String label = literal.getLexicalForm();
            if (literal.getLanguageTag().isPresent()) {
                final String lang = literal.getLanguageTag().get();
                return getValueFactory().createLiteral(label, lang);
            }
            final org.eclipse.rdf4j.model.IRI dataType = (org.eclipse.rdf4j.model.IRI) asValue(literal.getDatatype());
            return getValueFactory().createLiteral(label, dataType);
        }
        if (term instanceof BlankNode) {
            // This is where it gets tricky to support round trips!
            final BlankNode blankNode = (BlankNode) term;
            // FIXME: The uniqueReference might not be a valid BlankNode
            // identifier..
            // does it have to be in RDF4J?
            return getValueFactory().createBNode(blankNode.uniqueReference());
        }
        throw new IllegalArgumentException("RDFTerm was not an IRI, Literal or BlankNode: " + term.getClass());
    }

    @Override
    public RDF4JBlankNode createBlankNode() {
        final BNode bnode = getValueFactory().createBNode();
        return asRDFTerm(bnode);
    }

    @Override
    public RDF4JBlankNode createBlankNode(final String name) {
        final BNode bnode = getValueFactory().createBNode(name);
        return asRDFTerm(bnode);
    }

    /**
     * {@inheritDoc}
     * <p>
     * <strong>Note:</strong> Some operations on the {@link RDF4JDataset}
     * requires the use of try-with-resources to close underlying
     * {@link RepositoryConnection}s, including {@link RDF4JDataset#iterate()},
     * {@link RDF4JDataset#stream()} and {@link RDF4JDataset#getGraphNames()}.
     * 
     */
    @Override
    public RDF4JDataset createDataset() {
        final Sail sail = new MemoryStore();
        final Repository repository = new SailRepository(sail);
        return rdf4jFactory.createRepositoryDatasetImpl(repository, true, false);
    }

    @Override
    public RDF4JGraph createGraph() {
        return asGraph(new LinkedHashModel());
    }

    @Override
    public RDF4JIRI createIRI(final String iri) throws IllegalArgumentException {
        return asRDFTerm(getValueFactory().createIRI(iri));
    }

    @Override
    public RDF4JLiteral createLiteral(final String lexicalForm) throws IllegalArgumentException {
        final org.eclipse.rdf4j.model.Literal lit = getValueFactory().createLiteral(lexicalForm);
        return asRDFTerm(lit);
    }

    @Override
    public org.apache.commons.rdf.api.Literal createLiteral(final String lexicalForm, final org.apache.commons.rdf.api.IRI dataType)
            throws IllegalArgumentException {
        final org.eclipse.rdf4j.model.IRI iri = getValueFactory().createIRI(dataType.getIRIString());
        final org.eclipse.rdf4j.model.Literal lit = getValueFactory().createLiteral(lexicalForm, iri);
        return asRDFTerm(lit);
    }

    @Override
    public org.apache.commons.rdf.api.Literal createLiteral(final String lexicalForm, final String languageTag)
            throws IllegalArgumentException {
        final org.eclipse.rdf4j.model.Literal lit = getValueFactory().createLiteral(lexicalForm, languageTag);
        return asRDFTerm(lit);
    }

    @Override
    public RDF4JTriple createTriple(final BlankNodeOrIRI subject, final org.apache.commons.rdf.api.IRI predicate, final RDFTerm object)
            throws IllegalArgumentException {
        final Statement statement = getValueFactory().createStatement(
                (org.eclipse.rdf4j.model.Resource) asValue(subject), (org.eclipse.rdf4j.model.IRI) asValue(predicate),
                asValue(object));
        return asTriple(statement);
    }

    @Override
    public Quad createQuad(final BlankNodeOrIRI graphName, final BlankNodeOrIRI subject, final org.apache.commons.rdf.api.IRI predicate,
            final RDFTerm object) throws IllegalArgumentException {
        final Statement statement = getValueFactory().createStatement(
                (org.eclipse.rdf4j.model.Resource) asValue(subject), (org.eclipse.rdf4j.model.IRI) asValue(predicate),
                asValue(object), (org.eclipse.rdf4j.model.Resource) asValue(graphName));
        return asQuad(statement);
    }

    public ValueFactory getValueFactory() {
        return valueFactory;
    }

    private EnumSet<Option> optionSet(final Option... options) {
        final EnumSet<Option> opts = EnumSet.noneOf(Option.class);
        opts.addAll(Arrays.asList(options));
        return opts;
    }

}
