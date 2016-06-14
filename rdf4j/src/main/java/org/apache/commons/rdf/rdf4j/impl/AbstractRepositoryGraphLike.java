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

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.TripleLike;
import org.apache.commons.rdf.rdf4j.RDF4JGraphLike;
import org.apache.commons.rdf.rdf4j.RDF4JTermFactory;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;

public abstract class AbstractRepositoryGraphLike<T extends TripleLike<BlankNodeOrIRI, IRI, RDFTerm>>
		implements RDF4JGraphLike<T> {

	protected Repository repository;
	protected boolean includeInferred;
	protected boolean shouldWeShutdown = false;
	protected RDF4JTermFactory rdf4jTermFactory;

	public AbstractRepositoryGraphLike(Repository repository) {
		this(repository, false);
	}

	public AbstractRepositoryGraphLike(Repository repository, boolean includeInferred) {
		this.repository = repository;
		this.includeInferred = includeInferred;
		if (!repository.isInitialized()) {
			repository.initialize();
			shouldWeShutdown = true;
		}
		rdf4jTermFactory = new RDF4JTermFactory(repository.getValueFactory());
	}

	@Override
	public void close() throws Exception {
		if (shouldWeShutdown) {
			repository.shutDown();
		}
		// else: repository was initialized outside, so we should not shut it
		// down
	}

	
	protected abstract T asTripleLike(Statement s);

	protected RepositoryConnection getRepositoryConnection() {
		return repository.getConnection();
	}

	public Optional<Repository> asRepository() {
		return Optional.of(repository);
	}

	@Override
	public Optional<Model> asModel() {
		return Optional.empty();
	}

}
