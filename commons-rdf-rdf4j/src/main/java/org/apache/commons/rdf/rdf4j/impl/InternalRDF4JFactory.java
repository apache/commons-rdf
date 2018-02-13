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
package org.apache.commons.rdf.rdf4j.impl;

import java.util.UUID;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.rdf4j.RDF4JBlankNode;
import org.apache.commons.rdf.rdf4j.RDF4JDataset;
import org.apache.commons.rdf.rdf4j.RDF4JGraph;
import org.apache.commons.rdf.rdf4j.RDF4JIRI;
import org.apache.commons.rdf.rdf4j.RDF4JLiteral;
import org.apache.commons.rdf.rdf4j.RDF4JQuad;
import org.apache.commons.rdf.rdf4j.RDF4JTerm;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.apache.commons.rdf.rdf4j.RDF4JTriple;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;

/**
 * Factory for {@link RDF4JTerm} instances.
 * <p>
 * <strong>Internal class:</strong> This "abstract" class is intended for
 * internal use by Commons RDF and may change in any minor update. Use instead
 * {@link RDF4J} methods like {@link RDF4J#createBlankNode()},
 * {@link RDF4J#asRDFTerm(org.eclipse.rdf4j.model.Value)} and
 * {@link RDF4J#asGraph(Repository, Option...)}
 * <p>
 * This class exists as a <code>public</code> bridge between the packages
 * {@link org.apache.commons.rdf.rdf4j} and
 * {@link org.apache.commons.rdf.rdf4j.impl} by exposing the package-public
 * constructors.
 *
 * @see RDF4J
 */
public abstract class InternalRDF4JFactory {

    /**
     * Construct a {@link RDF4JBlankNode} from a RDF4J {@link BNode}.
     *
     * @param bNode
     *            RDF4J {@link BNode} to adapt
     * @param salt
     *            {@link UUID} to use for {@link BlankNode#uniqueReference()} in
     *            combination with {@link BNode#getID()}
     * @return Adapted {@link RDF4JBlankNode}
     */
    public RDF4JBlankNode createBlankNodeImpl(final BNode bNode, final UUID salt) {
        return new BlankNodeImpl(bNode, salt);
    }

    /**
     * Construct a {@link RDF4JIRI} from a RDF4J {@link IRI}.
     *
     * @param iri
     *            RDF4J {@link IRI} to adapt
     * @return Adapted {@link RDF4JIRI}
     */
    public RDF4JIRI createIRIImpl(final IRI iri) {
        return new IRIImpl(iri);
    }

    /**
     * Construct a {@link RDF4JLiteral} from a RDF4J {@link Literal}.
     *
     * @param literal
     *            RDF4J {@link Literal}
     * @return Adapted {@link RDF4JLiteral}
     */
    public RDF4JLiteral createLiteralImpl(final Literal literal) {
        return new LiteralImpl(literal);
    }

    /**
     * Construct a {@link RDF4JGraph} from a RDF4J {@link Model}.
     * <p>
     * Changes in the graph will be reflected in the model, and vice versa.
     *
     * @param model
     *            RDF4J {@link Model} to adapt
     * @param rdf4jTermFactory
     *            factory to use for adapting graph triples
     * @return Adapted {@link RDF4JGraph}
     */
    public RDF4JGraph createModelGraphImpl(final Model model, final RDF4J rdf4jTermFactory) {
        return new ModelGraphImpl(model, rdf4jTermFactory);
    }

    /**
     * Construct a {@link RDF4JQuad} from a RDF4J {@link Statement}.
     *
     * @param statement
     *            RDF4J {@link Statement} to adapt
     * @param salt
     *            {@link UUID} for adapting any {@link BNode}s
     * @return Adapted {@link RDF4JQuad}
     */
    public RDF4JQuad createQuadImpl(final Statement statement, final UUID salt) {
        return new QuadImpl(statement, salt);
    }

    /**
     * Construct a {@link RDF4JDataset} from a RDF4J {@link Repository}.
     * <p>
     * Changes in the dataset will be reflected in the repsitory, and vice
     * versa.
     *
     * @param repository
     *            RDF4J {@link Repository} to adapt
     * @param handleInitAndShutdown
     *            If <code>true</code>, the {@link RDF4JDataset} will initialize
     *            the repository (if needed), and shut it down on
     *            {@link RDF4JDataset#close()}.
     * @param includeInferred
     *            If true, any inferred quads are included in the dataset
     *
     * @return Adapted {@link RDF4JDataset}
     */
    public RDF4JDataset createRepositoryDatasetImpl(final Repository repository, final boolean handleInitAndShutdown,
            final boolean includeInferred) {
        return new RepositoryDatasetImpl(repository, UUID.randomUUID(), handleInitAndShutdown, includeInferred);
    }

    /**
     * Construct a {@link RDF4JGraph} from a RDF4J {@link Model}.
     * <p>
     * Changes in the graph will be reflected in the model, and vice versa.
     *
     * @param repository
     *            RDF4J {@link Repository} to adapt
     * @param handleInitAndShutdown
     *            If <code>true</code>, the {@link RDF4JGraph} will initialize
     *            the repository (if needed), and shut it down on
     *            {@link RDF4JGraph#close()}.
     * @param includeInferred
     *            If true, any inferred quads are included in the dataset
     * @param contextMask
     *            Zero or more {@link Resource}s contexts. The array may contain
     *            the value <code>null</code> for the default graph - however
     *            care must be taken to not provide a null-array
     *            <code>(Resource[]) null</code>.
     * @return Adapted {@link RDF4JGraph}
     */
    public RDF4JGraph createRepositoryGraphImpl(final Repository repository, final boolean handleInitAndShutdown,
            final boolean includeInferred, final Resource... contextMask) {
        return new RepositoryGraphImpl(repository, UUID.randomUUID(), handleInitAndShutdown, includeInferred,
                contextMask);
    }

    /**
     * Construct a {@link RDF4JTriple} from a RDF4J {@link Statement}.
     *
     * @param statement
     *            RDF4J {@link Statement} to adapt
     * @param salt
     *            {@link UUID} for adapting any {@link BNode}s
     * @return Adapted {@link RDF4JTriple}
     */
    public RDF4JTriple createTripleImpl(final Statement statement, final UUID salt) {
        return new TripleImpl(statement, salt);
    }

}
