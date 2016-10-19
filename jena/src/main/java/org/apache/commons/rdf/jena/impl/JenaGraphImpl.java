/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

package org.apache.commons.rdf.jena.impl;

import java.io.StringWriter;
import java.util.UUID;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaGraph;
import org.apache.commons.rdf.jena.JenaFactory;
import org.apache.jena.atlas.iterator.Iter;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

class JenaGraphImpl implements JenaGraph {

	private final org.apache.jena.graph.Graph graph;
	private final UUID salt;
	private final transient JenaFactory factory;
	private Model model;

	JenaGraphImpl(org.apache.jena.graph.Graph graph, UUID salt) {
		this.graph = graph;
		this.salt = salt;
		this.factory = new JenaFactory(salt);
	}

	JenaGraphImpl(Model model, UUID salt) {
		this.model = model;
		this.graph = model.getGraph();
		this.salt = salt;
		this.factory = new JenaFactory(salt);
	}

	@Override
	public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		graph.add(org.apache.jena.graph.Triple.create(
				factory.toJena(subject),
				factory.toJena(predicate), 
				factory.toJena(object)));
	}

	@Override
	public void add(Triple triple) {
		graph.add(factory.toJena(triple));
	}

	@Override
	public org.apache.jena.graph.Graph asJenaGraph() {
		return graph;
	}

	@Override
	public void clear() {
		graph.clear();
	}

	@Override
	public void close() {
		graph.close();
	}

	@Override
	public boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return graph.contains(
				factory.toJena(subject), 
				factory.toJena(predicate),
				factory.toJena(object));
	}

	@Override
	public boolean contains(Triple triple) {
		return graph.contains(factory.toJena(triple));
	}

	@Override
	public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		graph.delete(org.apache.jena.graph.Triple.create(
				factory.toJena(subject),
				factory.toJena(predicate), 
				factory.toJena(object)));
	}

	@Override
	public void remove(Triple triple) {
		graph.delete(factory.toJena(triple));
	}

	@Override
	public long size() {
		return graph.size();
	}

	@Override
	public Stream<? extends Triple> stream() {
		JenaFactory factory = new JenaFactory(salt);
		return Iter.asStream(graph.find(null, null, null), true).map(factory::fromJena);
	}

	@Override
	public Stream<? extends Triple> stream(BlankNodeOrIRI s, IRI p, RDFTerm o) {
		JenaFactory factory = new JenaFactory(salt);
		return Iter.asStream(graph.find(toJenaAny(s), toJenaAny(p), toJenaAny(o)), true)
				.map(factory::fromJena);
	}

	private Node toJenaAny(RDFTerm term) {
		if (term == null)
			return Node.ANY;
		return factory.toJena(term);
	}

	@Override
	public String toString() {
		StringWriter sw = new StringWriter();
		RDFDataMgr.write(sw, graph, Lang.NT);
		return sw.toString();
	}

	@Override
	public Model asJenaModel() {
		if (model == null) {
			synchronized(this) {
				// As Model can be used for locks, we should make sure we don't make
				// more than one model
				if (model == null) {
					model = ModelFactory.createModelForGraph(graph);
				}
			}
		}
		return model;
	}

}
