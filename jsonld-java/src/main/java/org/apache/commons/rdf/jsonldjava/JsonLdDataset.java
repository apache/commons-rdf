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
package org.apache.commons.rdf.jsonldjava;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;

import com.github.jsonldjava.core.RDFDataset;

public class JsonLdDataset extends JsonLdGraphLike<org.apache.commons.rdf.api.Quad> implements Dataset {

	JsonLdDataset(String bnodePrefix) {
		super(bnodePrefix);
	}

	JsonLdDataset(RDFDataset rdfDataset, String bnodePrefix) {
		super(rdfDataset, bnodePrefix);
	}

	public JsonLdDataset(RDFDataset rdfDataSet) {
		super(rdfDataSet);
	}

	@Override
	public void add(BlankNodeOrIRI graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		super.add(graphName, subject, predicate, object);
	}

	@Override
	public boolean contains(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return super.contains(graphName, subject, predicate, object);
	}
	
	@Override
	public void remove(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		super.remove(graphName, subject, predicate, object);
	}	

	@Override
	public Graph getGraph() {
		return new JsonLdGraph(rdfDataSet, Optional.empty(), bnodePrefix);
	}

	@Override
	public Optional<Graph> getGraph(BlankNodeOrIRI graphName) {
		if (graphName == null) {
			return Optional.of(getGraph());
		}
		return getGraphNames()
				.map(g -> (Graph)new JsonLdGraph(rdfDataSet, Optional.of(g), bnodePrefix))
				.findAny();
	}

	@Override
	public Stream<BlankNodeOrIRI> getGraphNames() {
		return rdfDataSet.graphNames().parallelStream().filter(Predicate.isEqual("@default").negate())
				.map(s -> s.startsWith("_:") ? new RDFDataset.BlankNode(s) : new RDFDataset.IRI(s))
				.map(n -> (BlankNodeOrIRI) factory.asTerm(n, bnodePrefix));		
	}

	@Override
	public Stream<? extends Quad> stream(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object) {		
		return filteredGraphs(graphName)
				.flatMap(List::stream)
				.filter(quadFilter(subject, predicate, object))
				.map(factory::createQuad);
	}

	@Override
	Quad asTripleOrQuad(com.github.jsonldjava.core.RDFDataset.Quad jsonldQuad) {
		return factory.createQuad(jsonldQuad);
	}

}
