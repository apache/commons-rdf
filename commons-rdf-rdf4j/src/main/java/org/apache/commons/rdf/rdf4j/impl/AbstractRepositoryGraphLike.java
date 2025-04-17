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

import java.util.Optional;
import java.util.UUID;

import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.api.TripleLike;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.apache.commons.rdf.rdf4j.RDF4JGraphLike;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

abstract class AbstractRepositoryGraphLike<T extends TripleLike> implements RDF4JGraphLike<T> {

    private final Repository repository;
    private final boolean includeInferred;
    private final boolean handleInitAndShutdown;
    private final RDF4J rdf4jTermFactory;
    private final UUID salt;

    AbstractRepositoryGraphLike(final Repository repository, final UUID salt, final boolean handleInitAndShutdown,
            final boolean includeInferred) {
        this.repository = repository;
        this.salt = salt;
        this.includeInferred = includeInferred;
        this.handleInitAndShutdown = handleInitAndShutdown;
        if (handleInitAndShutdown && !repository.isInitialized()) {
            repository.initialize();
        }
        rdf4jTermFactory = new RDF4J(repository.getValueFactory(), salt);
    }

    /**
     * Delegates to {@link RDF4J}{@link #asStatement(TripleLike)}
     * 
     * @param tripleLike A {@link Triple} or {@link Quad} to adapt
     * @return A corresponding {@link Statement}
     */
    protected Statement asStatement(final TripleLike tripleLike) {
        return rdf4jTermFactory.asStatement(tripleLike);
    }
    
    @Override
    public Optional<Model> asModel() {
        return Optional.empty();
    }

    @Override
    public Optional<Repository> asRepository() {
        return Optional.of(repository);
    }

    protected abstract T asTripleLike(Statement s);

    @Override
    public void close() throws Exception {
        if (handleInitAndShutdown) {
            repository.shutDown();
        }
        // else: repository was initialized outside, so we should not shut it
        // down
    }

    protected boolean getHandleInitAndShutdown() {
        return handleInitAndShutdown;
    }

    protected boolean getIncludeInferred() {
        return includeInferred;
    }

    protected RDF4J getRdf4jTermFactory() {
        return rdf4jTermFactory;
    }

    protected Repository getRepository() {
        return repository;
    }

    protected RepositoryConnection getRepositoryConnection() {
        return repository.getConnection();
    }

    protected UUID getSalt() {
        return salt;
    }

}
