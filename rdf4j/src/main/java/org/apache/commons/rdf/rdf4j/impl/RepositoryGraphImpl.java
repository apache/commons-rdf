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
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.rdf4j.RDF4JGraph;
import org.apache.commons.rdf.rdf4j.RDF4JTermFactory;
import org.apache.commons.rdf.rdf4j.RDF4JTriple;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;

public class RepositoryGraphImpl implements Graph, RDF4JGraph {

	private Repository repository;
	private boolean includeInferred;
	boolean shouldWeShutdown = false;
	private RDF4JTermFactory rdf4jTermFactory;

	public RepositoryGraphImpl(Repository repository) {
		this(repository, false);
	}

	public RepositoryGraphImpl(Repository repository, boolean includeInferred) {
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

	@Override
	public void add(Triple triple) {
		Statement statement = rdf4jTermFactory.asStatement(triple);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.add(statement);
			conn.commit();
		}
	}

	@Override
	public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
		org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
		Value obj = rdf4jTermFactory.asValue(object);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.add(subj, pred, obj);
			conn.commit();
		}
	}

	@Override
	public boolean contains(Triple triple) {
		Statement statement = rdf4jTermFactory.asStatement(triple);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			return conn.hasStatement(statement, includeInferred);
		}
	}

	@Override
	public boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
		org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
		Value obj = rdf4jTermFactory.asValue(object);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			return conn.hasStatement(subj, pred, obj, includeInferred);
		}
	}

	@Override
	public void remove(Triple triple) {
		Statement statement = rdf4jTermFactory.asStatement(triple);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.remove(statement);
			conn.commit();
		}
	}

	@Override
	public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
		org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
		Value obj = rdf4jTermFactory.asValue(object);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.remove(subj, pred, obj);
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
		try (RepositoryConnection conn = getRepositoryConnection()) {
			// FIXME: The below might contain duplicate statements across
			// multiple contexts
			return conn.size();
		}

	}

	@Override
	public Stream<RDF4JTriple> stream() {
		return stream(null, null, null);
	}

	@Override
	public Stream<RDF4JTriple> stream(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
		org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
		Value obj = rdf4jTermFactory.asValue(object);
		RepositoryConnection conn = getRepositoryConnection();
		// FIXME: Is it OK that we don't close the connection?
		RepositoryResult<Statement> statements = conn.getStatements(subj, pred, obj);
		return Iterations.stream(statements).map(rdf4jTermFactory::asTriple);

	}

	private RepositoryConnection getRepositoryConnection() {
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
