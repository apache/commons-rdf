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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.GraphLike;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
// NOTE: To avod confusion, don't importing either of the Quad
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.api.TripleLike;

import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Node;

/**
 * Common abstract {@link GraphLike}.
 * <p>
 * Specialised by {@link JsonLdGraph}, {@link JsonLdUnionGraph} and {@link JsonLdDataset}. 
 *
 * @param <T> specialisation of {@link TripleLike}, e.g. {@link Triple} or {@link Quad}
 */
abstract class JsonLdGraphLike<T extends TripleLike<BlankNodeOrIRI, IRI, RDFTerm>>
	implements GraphLike<T, BlankNodeOrIRI, IRI, RDFTerm> {
	
	/** 
	 * Used by {@link #bnodePrefix()} to get a unique UUID per JVM run
	 */
	private static UUID SALT = UUID.randomUUID();
	
	/**
	 * Prefix to use in blank node identifiers
	 */
	final String bnodePrefix;

	final JsonLdRDFTermFactory factory;

	/**
	 * The underlying JSON-LD {@link RDFDataset}.
	 * <p>
	 * Note: This is NOT final as it is reset to <code>null</code> by {@link #close()}
	 * (to free memory).
	 */
	RDFDataset rdfDataSet;
	
	JsonLdGraphLike(RDFDataset rdfDataSet) {
		this(rdfDataSet, "urn:uuid:" + SALT + "#" +  "g"+ System.identityHashCode(rdfDataSet));
	}

	JsonLdGraphLike(RDFDataset rdfDataSet, String bnodePrefix) {
		this.rdfDataSet = Objects.requireNonNull(rdfDataSet);
		this.bnodePrefix = Objects.requireNonNull(bnodePrefix);
		this.factory = new JsonLdRDFTermFactory(bnodePrefix);
	}
	
	JsonLdGraphLike(String bnodePrefix) {
		this(new RDFDataset(), bnodePrefix);
	}
	
	@Override
	public void add(T t) {
		// add triples to default graph by default
		BlankNodeOrIRI graphName = null;
		if (t instanceof org.apache.commons.rdf.api.Quad) {
			org.apache.commons.rdf.api.Quad q = (org.apache.commons.rdf.api.Quad)t;
			graphName = q.getGraphName().orElse(null);
		}
		add(graphName, t.getSubject(), t.getPredicate(), t.getObject());
	}	

	void add(BlankNodeOrIRI graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		String g = factory.asJsonLdString(graphName);
		String s = factory.asJsonLdString(subject);
		String p = factory.asJsonLdString(predicate);		
		if (object instanceof BlankNodeOrIRI) {
			String o = factory.asJsonLdString((BlankNodeOrIRI)object);
			rdfDataSet.addQuad(s,p,o,g);
		} else if(object instanceof Literal) { 
			Literal literal = (Literal) object;
			String language = literal.getLanguageTag().orElse(null);
			String datatype = literal.getDatatype().getIRIString();
			rdfDataSet.addQuad(s,p,literal.getLexicalForm(), datatype, language, g);
		}		
	}
		
	public void close() {
		// Drop the memory reference, but don't clear it
		rdfDataSet = null;			
	}	

	@Override
	public void clear() {		
		filteredGraphs(null).forEach(s -> s.clear());
		//   In theory we could use
		//rdfDataSet.clear();
		//   but then we would need to also do
		//rdfDataSet.put("@default",  new ArrayList());
		//   .. both of which seems to be touching too much on JsonLd-Java's internal structure
	}

	@Override
	public boolean contains(T tripleOrQuad) {		
		return stream().anyMatch(Predicate.isEqual(tripleOrQuad));
	}

	public RDFDataset getRdfDataSet() {
		return rdfDataSet;
	}

	
	@Override
	public Stream<? extends T> stream() {
		return rdfDataSet.graphNames().parallelStream()
				.map(rdfDataSet::getQuads)
				.flatMap(List<RDFDataset.Quad>::parallelStream)
				.map(this::asTripleOrQuad);
	}
	
	/**
	 * Convert JsonLd Quad to a Commons RDF {@link Triple} or {@link org.apache.commons.rdf.api.Quad}
	 * 
	 * 
	 * @see JsonLdRDFTermFactory#asTriple(Quad)
	 * @see JsonLdRDFTermFactory#asQuad(Quad)
	 * @param jsonldQuad jsonld quad to convert
	 * @return converted {@link TripleLike}
	 */
	abstract T asTripleOrQuad(RDFDataset.Quad jsonldQuad);

	// This will be made public in JsonLdDataset
	// and is used by the other  methods.
	boolean contains(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI s, IRI p, RDFTerm o) {
		return filteredGraphs(graphName).flatMap(List::stream).anyMatch(quadFilter(s,p,o));
	}

	Stream<List<RDFDataset.Quad>> filteredGraphs(Optional<BlankNodeOrIRI> graphName) {
		return rdfDataSet.graphNames().parallelStream()
				// if graphName == null (wildcard), select all graphs, 
	 			// otherwise check its jsonld string
			    // (including @default for default graph)
				.filter(g -> graphName == null ||
						g.equals(graphName.map(factory::asJsonLdString).orElse("@default")))
				// remove the quads which match our filter (which could have nulls as wildcards) 
				.map(rdfDataSet::getQuads);
	}

	String graphNameAsJsonLdString(T tripleOrQuad) {		
		if (tripleOrQuad instanceof org.apache.commons.rdf.api.Quad) {
			org.apache.commons.rdf.api.Quad quad = (org.apache.commons.rdf.api.Quad)tripleOrQuad;
			return quad.getGraphName().map(factory::asJsonLdString).orElse("@default");			
		}		
		return "@default";
	}
	
	
	Predicate<RDFDataset.Quad> quadFilter(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Optional<Node> subjectNode = Optional.ofNullable(subject).map(factory::asJsonLdNode);
		Optional<Node> predicateNode = Optional.ofNullable(predicate).map(factory::asJsonLdNode);
		Optional<Node> objectNode = Optional.ofNullable(object).map(factory::asJsonLdNode);
		
		return q -> {
		    if (subjectNode.isPresent() && subjectNode.get().compareTo(q.getSubject()) != 0) {
		        return false;
		    }
		    if (predicateNode.isPresent() && predicateNode.get().compareTo(q.getPredicate()) != 0) {	          
		        return false;
		    }
		    if (objectNode.isPresent() && objectNode.get().compareTo(q.getObject()) != 0) {
		        return false;
		    }
		    return true;			
		};
	}
	
	// NOTE: This is made public in JsonLdDataset and is used by the other remove methods.
	void remove(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		// remove the quads which match our filter (which could have nulls as wildcards) 
		filteredGraphs(graphName).forEach(t -> t.removeIf(quadFilter(subject, predicate, object)));
	}
	
}