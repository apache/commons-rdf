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
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.rdf4j.RDF4JDataset;
import org.apache.commons.rdf.rdf4j.RDF4JQuad;
import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;

public class RepositoryDatasetImpl extends AbstractRepositoryGraphLike<Quad> implements RDF4JDataset, Dataset {

	public RepositoryDatasetImpl(Repository repository, boolean includeInferred) {
		super(repository, includeInferred);
	}

	public RepositoryDatasetImpl(Repository repository) {
		super(repository);
	}


	@Override
	public void add(Quad tripleLike) {
		Statement statement = rdf4jTermFactory.asStatement(tripleLike);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.add(statement);
			conn.commit();
		}
	}


	@Override
	public boolean contains(Quad tripleLike) {
		Statement statement = rdf4jTermFactory.asStatement(tripleLike);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			return conn.hasStatement(statement, includeInferred);
		}
	}

	@Override
	public void remove(Quad tripleLike) {
		Statement statement = rdf4jTermFactory.asStatement(tripleLike);
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
	public void add(BlankNodeOrIRI graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource context = (Resource) rdf4jTermFactory.asValue(graphName);
		Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
		org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
		Value obj = rdf4jTermFactory.asValue(object);
		try (RepositoryConnection conn = getRepositoryConnection()) {
			conn.add(subj, pred, obj,  context);
			conn.commit();
		}
	}

	@Override
	public boolean contains(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
		org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
		Value obj = rdf4jTermFactory.asValue(object);
		Resource[] contexts = asContexts(graphName);
		
		try (RepositoryConnection conn = getRepositoryConnection()) {
			return conn.hasStatement(subj, pred, obj, includeInferred, contexts);
		}
	}

	private Resource[] asContexts(Optional<BlankNodeOrIRI> graphName) {
		Resource[] contexts;
		if (graphName == null) {
			// no contexts == any contexts
			 contexts = new Resource[0];
		} else {	
			BlankNodeOrIRI g = graphName.orElse(null);
			Resource context = (Resource) rdf4jTermFactory.asValue(g);
			contexts = new Resource[] { context };
		}
		return contexts;
	}

	@Override
	public void remove(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
		org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
		Value obj = rdf4jTermFactory.asValue(object);
		Resource[] contexts = asContexts(graphName);

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
	public Stream<RDF4JQuad> stream(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Resource subj = (Resource) rdf4jTermFactory.asValue(subject);
		org.eclipse.rdf4j.model.IRI pred = (org.eclipse.rdf4j.model.IRI) rdf4jTermFactory.asValue(predicate);
		Value obj = rdf4jTermFactory.asValue(object);
		Resource[] contexts = asContexts(graphName);
		
		RepositoryConnection conn = getRepositoryConnection();
		// NOTE: connection will be closed outside by the Iterations.stream()
		RepositoryResult<Statement> statements = conn.getStatements(subj, pred, obj, includeInferred, contexts);
		return Iterations.stream(statements).map(this::asTripleLike);
	}

	@Override
	protected RDF4JQuad asTripleLike(Statement s) {
		return rdf4jTermFactory.asQuad(s);
	}

	@Override
	public Graph getGraph() {
		// default context only
		return new RepositoryGraphImpl(repository, includeInferred, (Resource)null);		
	}

	@Override
	public Optional<Graph> getGraph(BlankNodeOrIRI graphName) {
		// NOTE: May be null to indicate default context
		Resource context = (Resource) rdf4jTermFactory.asValue(graphName);		
		return Optional.of(new RepositoryGraphImpl(repository, includeInferred, context));		
	}

	@Override
	public Stream<BlankNodeOrIRI> getGraphNames() {
		RepositoryConnection conn = getRepositoryConnection();
		RepositoryResult<Resource> contexts = conn.getContextIDs();
		// NOTE: connection will be closed outside by the Iterations.stream()
		return Iterations.stream(contexts).map(g -> (BlankNodeOrIRI) rdf4jTermFactory.asRDFTerm(g));
	}
	
}
