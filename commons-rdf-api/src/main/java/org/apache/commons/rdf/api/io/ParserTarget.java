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
package org.apache.commons.rdf.api.io;

import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;

@FunctionalInterface
public interface ParserTarget<T> extends Consumer<Quad> {

	default T dest() {
		return null;// unknown
	}

	static ParserTarget<Dataset> toDataset(final Dataset ds) {
		return new DatasetParserTarget(ds);
	}

	static ParserTarget<Graph> toGraph(final Graph graph) {
		return new GraphParserTarget(graph, null);
	}

	static ParserTarget<Graph> toGraph(final Graph graph, IRI matchGraphName) {
		return new GraphParserTarget(graph, matchGraphName);
	}

	static ParserTarget<Graph> toUnionGraph(final Graph graph) {
		return q -> graph.add(q.asTriple());
	}
}

class GraphParserTarget implements ParserTarget<Graph> {

	private final boolean anyGraphName;
	private final Graph graph;
	private final IRI matchGraphName;

	GraphParserTarget(Graph graph) {
		this.graph = graph;
		this.matchGraphName = null;
		this.anyGraphName = true;
	}

	GraphParserTarget(Graph graph, IRI matchGraphName) {
		this.graph = graph;
		this.matchGraphName = matchGraphName;
		this.anyGraphName = false;
	}

	@Override
	public Graph dest() {
		return graph;
	}

	@Override
	public void accept(Quad q) {
		if (anyGraphName || q.getGraphName().equals(Optional.ofNullable(matchGraphName))) {
			graph.add(q.asTriple());
		}
	}
}

class DatasetParserTarget implements ParserTarget<Dataset> {

	private final Dataset dataset;

	public DatasetParserTarget(Dataset dataset) {
		this.dataset = dataset;
	}

	@Override
	public Dataset dest() {
		return dataset;
	}

	@Override
	public void accept(Quad q) {
		dataset.add(q);
	}
}
