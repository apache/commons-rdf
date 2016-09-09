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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNode;
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
import com.github.jsonldjava.core.RDFDataset.Quad;

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

	JsonLdGraphLike(RDFDataset rdfDataset, String bnodePrefix) {
		rdfDataSet = rdfDataset;
		this.bnodePrefix = bnodePrefix;
		this.factory = new JsonLdRDFTermFactory(bnodePrefix);
	}
	
	JsonLdGraphLike(String bnodePrefix) {
		this(new RDFDataset(), bnodePrefix);
	}
	
	@Override
	public void add(T tripleOrQuad) {
		String g = graphNameAsJsonLdString(tripleOrQuad);

		String s = asJsonLdString(tripleOrQuad.getSubject());
		String p = asJsonLdString(tripleOrQuad.getPredicate());
		
		if (tripleOrQuad.getObject() instanceof BlankNodeOrIRI) {
			String o = asJsonLdString((BlankNodeOrIRI)tripleOrQuad.getObject());
			rdfDataSet.addQuad(s,p,o,g);
		} else if(tripleOrQuad.getObject() instanceof Literal) { 
			Literal literal = (Literal) tripleOrQuad.getObject();
			String language = literal.getLanguageTag().orElse(null);
			String datatype = literal.getDatatype().getIRIString();
			rdfDataSet.addQuad(s,p,literal.getLexicalForm(), datatype, language, g);
		}
	}

	@Override
	public void clear() {
		rdfDataSet.clear();
	}
	
	public void close() {
		// Drop the memory reference, but don't clear it
		rdfDataSet = null;			
	}	

	@Override
	public boolean contains(T tripleOrQuad) {		
		return stream().anyMatch(Predicate.isEqual(tripleOrQuad));
	}

	public RDFDataset getRdfDataSet() {
		return rdfDataSet;
	}
	
	@Override
	public void remove(T t) {
		if (t instanceof org.apache.commons.rdf.api.Quad) {
			org.apache.commons.rdf.api.Quad q = (org.apache.commons.rdf.api.Quad) t;
			remove(q.getGraphName(), q.getSubject(), q.getPredicate(), q.getObject());
		} else {
			remove(Optional.empty(), t.getSubject(), t.getPredicate(), t.getObject());
		}
	}
	
	@Override
	public long size() {		
		return rdfDataSet.graphNames().parallelStream().map(rdfDataSet::getQuads).collect(Collectors.summingLong(List::size));
	}
	
	@Override
	public Stream<? extends T> stream() {
		return rdfDataSet.graphNames().parallelStream()
				.map(rdfDataSet::getQuads)
				.flatMap(List<RDFDataset.Quad>::parallelStream)
				.map(this::asTripleOrQuad);
	}
	
	void add(BlankNodeOrIRI graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		String g = asJsonLdString(graphName);
		String s = asJsonLdString(subject);
		String p = asJsonLdString(predicate);		
		if (object instanceof BlankNodeOrIRI) {
			String o = asJsonLdString((BlankNodeOrIRI)object);
			rdfDataSet.addQuad(s,p,o,g);
		} else if(object instanceof Literal) { 
			Literal literal = (Literal) object;
			String language = literal.getLanguageTag().orElse(null);
			String datatype = literal.getDatatype().getIRIString();
			rdfDataSet.addQuad(s,p,literal.getLexicalForm(), datatype, language, g);
		}		
	}

	String asJsonLdString(BlankNodeOrIRI blankNodeOrIRI) {
		if (blankNodeOrIRI == null) {
			return null;
		}
		if (blankNodeOrIRI instanceof IRI) {
			return ((IRI)blankNodeOrIRI).getIRIString();
		} else if (blankNodeOrIRI instanceof BlankNode) {
			BlankNode blankNode = (BlankNode) blankNodeOrIRI;
			String ref = blankNode.uniqueReference();
			if (ref.startsWith(bnodePrefix)) { 
				// One of ours (but possibly not a JsonLdBlankNode) -  
				// we can use the suffix directly
				return ref.replace(bnodePrefix, "_:");
			} else {
				// Map to unique bnode identifier, e.g. _:0dbd92ee-ab1a-45e7-bba2-7ade54f87ec5
				UUID uuid = UUID.nameUUIDFromBytes(ref.getBytes(StandardCharsets.UTF_8));
				return "_:"+ uuid;
			}
		} else {
			throw new IllegalArgumentException("Expected a BlankNode or IRI, not: " + blankNodeOrIRI);
		}
	}
	
	/**
	 * Convert JsonLd Quad to a Commons RDF {@link Triple} or {@link org.apache.commons.rdf.api.Quad}
	 * 
	 * 
	 * @see JsonLdRDFTermFactory#createTriple(Quad)
	 * @see JsonLdRDFTermFactory#createQuad(Quad)
	 * @param jsonldQuad jsonld quad to convert
	 * @return converted {@link TripleLike}
	 */
	abstract T asTripleOrQuad(RDFDataset.Quad jsonldQuad);

	// This will be made public in JsonLdDataset
	// and is used by the other  methods.
	boolean contains(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI s, IRI p, RDFTerm o) {
		return filteredGraphs(graphName).flatMap(List::stream).anyMatch(quadFilter(s,p,o));
	}

	Stream<List<Quad>> filteredGraphs(Optional<BlankNodeOrIRI> graphName) {
		return rdfDataSet.graphNames().parallelStream()
				// if graphName == null (wildcard), select all graphs, 
	 			// otherwise check its jsonld string
			    // (including @default for default graph)
				.filter(g -> graphName == null ||
						g.equals(graphName.map(this::asJsonLdString).orElse("@default")))
				// remove the quads which match our filter (which could have nulls as wildcards) 
				.map(rdfDataSet::getQuads);
	}

	String graphNameAsJsonLdString(T tripleOrQuad) {
		if (tripleOrQuad instanceof org.apache.commons.rdf.api.Quad) {
			org.apache.commons.rdf.api.Quad quad = (org.apache.commons.rdf.api.Quad)tripleOrQuad;
			return quad.getGraphName().map(this::asJsonLdString).orElse("@default");			
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
