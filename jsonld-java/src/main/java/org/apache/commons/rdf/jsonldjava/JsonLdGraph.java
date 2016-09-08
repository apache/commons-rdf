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
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.Triple;

import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Node;
import com.github.jsonldjava.core.RDFDataset.Quad;

public class JsonLdGraph implements Graph {
	
	/** 
	 * Used by {@link #bnodePrefix()} to get a unique UUID per JVM run
	 */
	private static UUID SALT = UUID.randomUUID();
	
	/**
	 * The underlying JSON-LD {@link RDFDataset}.
	 */
	private RDFDataset rdfDataSet;

	public RDFDataset getRdfDataSet() {
		return rdfDataSet;
	}

	private JsonLdRDFTermFactory rdfTermFactory;

	/**
	 * If true, include all Quad statements as Triples. If false, 
	 * only Quads in the default graph (<code>null</code>) are
	 * included.
	 */
	private boolean unionGraph = false;

	public JsonLdGraph() {
		this(new RDFDataset(), false);
	}
	
	public JsonLdGraph(RDFDataset rdfDataset) {
		this(rdfDataset, false);
	}

	public JsonLdGraph(RDFDataset rdfDataset, boolean unionGraph) {
		this.rdfDataSet = rdfDataset;	
		this.unionGraph = unionGraph;
	}

	@Override
	public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		String subjectStr;
		if (subject instanceof BlankNode) { 
			subjectStr = subject.ntriplesString();
		} else if (subject instanceof IRI){
			subjectStr = ((IRI)subject).getIRIString();
		} else { 
			throw new IllegalStateException("Subject was neither IRI or BlankNode: " + subject);
		}
		
		String predicateStr = predicate.getIRIString();
		
		if (object instanceof Literal) {
			Literal literal = (Literal) object;
			rdfDataSet.addTriple(subjectStr, predicateStr, literal.getLexicalForm(), literal.getDatatype().getIRIString(), literal.getLanguageTag().orElse(null));			
		} else if (object instanceof BlankNode) {
			rdfDataSet.addTriple(subjectStr, predicateStr, object.ntriplesString());
		} else if (object instanceof IRI) { 
			rdfDataSet.addTriple(subjectStr, predicateStr, ((IRI)object).getIRIString());
		} else { 
			throw new IllegalStateException("Object was neither IRI, BlankNode nor Literal: " + object);
		}				
	}

	@Override
	public void add(Triple triple) {
		// Quad q = asJsonLdQuad(triple);
		// rdfDataSet.addQuad(q);
		
		add(triple.getSubject(), triple.getPredicate(), triple.getObject());
	}

	@Override
	public void clear() {
		if (unionGraph) {
			// Delete all quads
			rdfDataSet.clear();
		} else {
			// Only the @default quads removed
			rdfDataSet.getQuads("@default").clear();
		}
	}
	@Override
	public void close() {
		// Drop the memory reference, but don't clear it
		rdfDataSet = null;		
	}
	
	@Override
	public boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		return stream(subject, predicate, object).findAny().isPresent();
	}

	@Override
	public boolean contains(Triple triple) {
		return stream().anyMatch(Predicate.isEqual(triple));
	}

	public RDFTermFactory getContext() {
		// Note: This does not need to be synchronized, it's OK 
		// if you get a few accidental copies as the
		// same bnodePrefix() is passed to each
		if (rdfTermFactory == null) {
			rdfTermFactory = new JsonLdRDFTermFactory(bnodePrefix());
		}
		return rdfTermFactory;
	}

	@Override
	public Stream<? extends Triple> stream() {
		if (! unionGraph) {
			return rdfDataSet.getQuads("@default").parallelStream().map(this::asTriple);
		}
		return rdfDataSet.graphNames().parallelStream().map(rdfDataSet::getQuads).flatMap(List<Quad>::parallelStream).map(this::asTriple);
	}

	@Override
	public Stream<? extends Triple> stream(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		// RDFDataSet has no optimizations to help us, so we'll dispatch to filter()
        return stream().filter(t -> {
            if (subject != null && !t.getSubject().equals(subject)) {
                return false;
            }
            if (predicate != null && !t.getPredicate().equals(predicate)) {
                return false;
            }
            if (object != null && !t.getObject().equals(object)) {
                return false;
            }
            return true;
        });
	}

	@Override
	public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {		
		Predicate<? super Quad> filter = quadFilter(subject, predicate, object);
		if (! unionGraph) {
			rdfDataSet.getQuads("@default").removeIf(filter);
		} else {
			rdfDataSet.graphNames().parallelStream().map(rdfDataSet::getQuads).map(t -> t.removeIf(filter));
		}
	}

	@Override
	public void remove(Triple triple) {
		remove(triple.getSubject(), triple.getPredicate(), triple.getObject());
	}

	@Override
	public long size() {
		if (! unionGraph) {
			return rdfDataSet.getQuads("@default").size();
		} else {
			// Summarize graph.size() for all graphs
			return rdfDataSet.graphNames().parallelStream().map(rdfDataSet::getQuads).collect(Collectors.summingLong(List::size));
		}
	}

	private Node asJsonLdNode(RDFTerm term) {
		if (term instanceof IRI) {
			return new RDFDataset.IRI( ((IRI)term).getIRIString() );
		}
		if (term instanceof BlankNode) {
			
			String uniqueReference = ((BlankNode)term).uniqueReference();
			if (uniqueReference.startsWith(bnodePrefix())) {
				// one of our own
				// TODO: Retrieve the original BlankNode
				return new RDFDataset.BlankNode(term.ntriplesString());
			} 
			return new RDFDataset.BlankNode( "_:" + uniqueReference );
		}
		if (term instanceof Literal) {
			Literal literal = (Literal) term;
			return new RDFDataset.Literal(literal.getLexicalForm(), literal.getDatatype().getIRIString(), 
					literal.getLanguageTag().orElse(null));
		}
		throw new IllegalArgumentException("RDFTerm not instanceof IRI, BlankNode or Literal: " + term);
	}

	private Triple asTriple(final RDFDataset.Quad quad) {
		return new JsonLdTriple(quad, bnodePrefix());
	}

	public String bnodePrefix() {
		return "urn:uuid:" + SALT + "#" +  "g"+ System.identityHashCode(rdfDataSet);
	}

	private Predicate<? super Quad> quadFilter(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Optional<Node> subjectNode = Optional.ofNullable(subject).map(this::asJsonLdNode);
		Optional<Node> predicateNode = Optional.ofNullable(predicate).map(this::asJsonLdNode);
		Optional<Node> objectNode = Optional.ofNullable(object).map(this::asJsonLdNode);
		
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
}
