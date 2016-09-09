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
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;

import com.github.jsonldjava.core.RDFDataset;

public class JsonLdUnionGraph extends JsonLdGraphLike<org.apache.commons.rdf.api.Triple> implements Graph {

	JsonLdUnionGraph(String bnodePrefix) {
		super(bnodePrefix);
	}
	
	public JsonLdUnionGraph(RDFDataset rdfDataSet) {
		super(rdfDataSet);
	}
	
	JsonLdUnionGraph(RDFDataset rdfDataSet, String bnodePrefix) {
		super(rdfDataSet, bnodePrefix);
	}


	@Override
	public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		super.add(null, subject, predicate, object);
	}

	@Override
	public boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return super.contains(null, subject, predicate, object);
	}
	
	@Override
	public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		super.remove(null, subject, predicate, object);
	}

	@Override
	public Stream<JsonLdTriple> stream(BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object) {		
		return filteredGraphs(null)
				.flatMap(List::stream)
				.filter(quadFilter(subject, predicate, object))
				.map(factory::createTriple);
	}

	@Override
	JsonLdTriple asTripleOrQuad(com.github.jsonldjava.core.RDFDataset.Quad jsonldQuad) {
		return factory.createTriple(jsonldQuad);
	}
	
	@Override
	public long size() {
		// NOTE: Inefficient as we'll have remove duplicate triples :-( 
		return stream().distinct().count();
	}
}

