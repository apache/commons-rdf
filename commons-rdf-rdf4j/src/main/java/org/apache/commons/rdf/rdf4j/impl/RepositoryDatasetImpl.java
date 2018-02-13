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

import java.util.ConcurrentModificationException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.rdf4j.ClosableIterable;
import org.apache.commons.rdf.rdf4j.RDF4JDataset;
import org.apache.commons.rdf.rdf4j.RDF4JQuad;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;

class RepositoryDatasetImpl extends AbstractRepositoryGraphLike<Quad> implements RDF4JDataset {

    RepositoryDatasetImpl(final Repository repository, final UUID salt, final boolean handleInitAndShutdown, final boolean includeInferred) {
        super(repository, salt, handleInitAndShutdown, includeInferred);
    }

    @Override
    public void add(final Quad tripleLike) {
        final Statement statement = rdf4jTermFactory.asStatement(tripleLike);
        try (RepositoryConnection conn = getRepositoryConnection()) {
            conn.add(statement);
            conn.commit();
        }
    }

    @Override
    public boolean contains(final Quad tripleLike) {
        final Statement statement = rdf4jTermFactory.asStatement(tripleLike);
        try (RepositoryConnection conn = getRepositoryConnection()) {
            return conn.hasStatement(statement, includeInferred);
        }
    }

    @Override
    public void remove(final Quad tripleLike) {
        final Statement statement = rdf4jTermFactory.asStatement(tripleLike);
        try (RepositoryConnection conn = getRepositoryConnection()) {
            conn.remove(statement);
            conn.commit();
        }
    }

    @Override
    public void clear() {
        try (RepositoryConnection conn = getRepositoryConnection()) {
            conn.clear();
            conn.commit();
        }
    }

    @Override
    public long size() {
        if (includeInferred) {
            // We'll need to count them all
            return stream().count();
        }
        // else: Ask directly
        try (RepositoryConnection conn = getRepositoryConnection()) {
            return conn.size();
        }
    }

    @Override
    public void add(final BlankNodeOrIRI graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        final Resource context = (Resource) rdf4jTermFactory.asValue(graphName);
        final Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
        final org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
        final Value obj = rdf4jTermFactory.asValue(object);
        try (RepositoryConnection conn = getRepositoryConnection()) {
            conn.add(subj, pred, obj, context);
            conn.commit();
        }
    }

    @Override
    public boolean contains(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        final Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
        final org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
        final Value obj = rdf4jTermFactory.asValue(object);
        final Resource[] contexts = asContexts(graphName);
        try (RepositoryConnection conn = getRepositoryConnection()) {
            return conn.hasStatement(subj, pred, obj, includeInferred, contexts);
        }
    }

    private Resource[] asContexts(final Optional<BlankNodeOrIRI> graphName) {
        Resource[] contexts;
        if (graphName == null) {
            // no contexts == any contexts
            contexts = new Resource[0];
        } else {
            final BlankNodeOrIRI g = graphName.orElse(null);
            final Resource context = (Resource) rdf4jTermFactory.asValue(g);
            contexts = new Resource[] { context };
        }
        return contexts;
    }

    @Override
    public void remove(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        final Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
        final org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
        final Value obj = rdf4jTermFactory.asValue(object);
        final Resource[] contexts = asContexts(graphName);

        try (RepositoryConnection conn = getRepositoryConnection()) {
            conn.remove(subj, pred, obj, contexts);
            conn.commit();
        }
    }

    @Override
    public Stream<RDF4JQuad> stream() {
        return stream(null, null, null, null);
    }

    @Override
    public Stream<RDF4JQuad> stream(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate,
            final RDFTerm object) {
        final Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
        final org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
        final Value obj = rdf4jTermFactory.asValue(object);
        final Resource[] contexts = asContexts(graphName);

        // NOTE: We can't do the usual try..with closing of the
        // RepositoryConnection here as it will have to be closed outside
        // by the user of the returned stream
        final RepositoryConnection conn = getRepositoryConnection();
        Stream<RDF4JQuad> stream = null;
        try {
            final RepositoryResult<Statement> statements = conn.getStatements(subj, pred, obj, includeInferred, contexts);
            // NOTE: Iterations.stream should close RepositoryResult as long as
            // our caller closes the stream
            stream = Iterations.stream(statements).map(rdf4jTermFactory::asQuad);
        } finally {
            if (stream == null) {
                // Some exception before we made the stream, close connection
                // here
                conn.close();
            }
        }
        // Make sure the RepositoryConnection is closed
        return stream == null ? null : stream.onClose(conn::close);

    }

    @Override
    public ClosableIterable<Quad> iterate() throws ConcurrentModificationException, IllegalStateException {
        return iterate(null, null, null, null);
    }

    @Override
    public ClosableIterable<Quad> iterate(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate,
            final RDFTerm object) throws ConcurrentModificationException, IllegalStateException {
        final Resource[] contexts = asContexts(graphName);
        final Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
        final org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
        final Value obj = rdf4jTermFactory.asValue(object);
        return new ConvertedStatements<>(this::getRepositoryConnection, rdf4jTermFactory::asQuad, subj, pred, obj,
                contexts);
    }

    @Override
    protected RDF4JQuad asTripleLike(final Statement s) {
        return rdf4jTermFactory.asQuad(s);
    }

    @Override
    public Graph getGraph() {
        // default context only
        // NOTE: We carry over the 'salt' as the graph's BlankNode should be
        // equal to our BlankNodes
        return new RepositoryGraphImpl(repository, salt, false, includeInferred, (Resource) null);
    }

    @Override
    public Optional<Graph> getGraph(final BlankNodeOrIRI graphName) {
        // NOTE: May be null to indicate default context
        final Resource context = (Resource) rdf4jTermFactory.asValue(graphName);
        // NOTE: We carry over the 'salt' as the graph's BlankNode should be
        // equal to our BlankNodes
        return Optional.of(new RepositoryGraphImpl(repository, salt, false, includeInferred, context));
    }

    @Override
    public Stream<BlankNodeOrIRI> getGraphNames() {
       final RepositoryConnection conn = getRepositoryConnection();
       final RepositoryResult<Resource> contexts = conn.getContextIDs();
        return Iterations.stream(contexts).map(g -> (BlankNodeOrIRI) rdf4jTermFactory.asRDFTerm(g))
                .onClose(conn::close);
    }

}
