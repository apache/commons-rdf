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
import org.apache.commons.rdf.api.Triple;

import com.github.jsonldjava.core.RDFDataset;

/**
 * A <strong>union graph</em> representation of a JsonLd {@link RDFDataset}.
 * <p>
 * A union graph contains all the triples of the dataset, irregardless of their
 * graph names.
 * <p>
 * {@link #add(Triple)} and {@link #add(BlankNodeOrIRI, IRI, RDFTerm)} 
 * will add the triple to the default graph
 * (e.g. <code>@default</code> in JSON-LD), while
 * the remaining methods (including
 * {@link #remove(Triple)} or {@link #remove(BlankNodeOrIRI, IRI, RDFTerm)}) 
 * relate to triples from <strong>all</strong> graphs.
 * <p>
 * <strong>Note:</strong>
 * Some operations like {@link #stream()} and {@link #size()} are
 * inefficient as they skip any duplicate triples from multiple
 * graphs.
 */
class JsonLdUnionGraph extends JsonLdGraphLike<org.apache.commons.rdf.api.Triple> implements Graph {

	JsonLdUnionGraph(String bnodePrefix) {
		super(bnodePrefix);
	}
	
	JsonLdUnionGraph(RDFDataset rdfDataSet) {
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
	public void remove(Triple t) {
		// Remove from ALL graphs, not just default graph
		super.remove(null, t.getSubject(), t.getPredicate(), t.getObject());
	}
	
	@Override
	public Stream<JsonLdTriple> stream(BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object) {				
		return filteredGraphs(null)
				.flatMap(List::stream)
				.filter(quadFilter(subject, predicate, object))
				.map(factory::asTriple)
				// Make sure we don't have duplicate triples
				// NOTE: This can be quite inefficient
				.distinct();
	}
	
	@Override
	public Stream<? extends Triple> stream() {		
		// NOTE: inefficient as we have to remove duplicate triples 
		// in different graphs :-(
		return super.stream().distinct();
	}

	@Override
	JsonLdTriple asTripleOrQuad(com.github.jsonldjava.core.RDFDataset.Quad jsonldQuad) {
		return factory.asTriple(jsonldQuad);
	}
	
	@Override
	public long size() {
		// Note: Our specialized stream() already removes duplicates using .distinct()
		return stream().count();
	}

}
