package org.apache.commons.rdf.jsonldjava;

import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;

import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Node;
import com.github.jsonldjava.core.RDFDataset.Quad;

public class JsonLDGraph implements Graph {
	private RDFDataset rdfDataSet;

	/**
	 * If true, include all Quad statements as Triples. If false, 
	 * only Quads in the default graph (<code>null</code>) are
	 * included.
	 */
	private boolean unionGraph = false;

	@Override
	public void close() throws Exception {
		rdfDataSet = null;		
	}

	@Override
	public void add(Triple triple) {
		Quad q = asJsonLdQuad(triple);
	}

	private Quad asJsonLdQuad(Triple triple) {
		Node subject = asJsonLdNode(triple.getSubject());
		Node predicate = asJsonLdNode(triple.getPredicate());
		Node object = asJsonLdNode(triple.getObject());
		String graph = null;
		// TODO: Add Quad to commons-rdf
//		if (triple instanceof Quad) {
//			String graph = triple.getGraph().getIRIString();
//		}
		return new Quad(subject, predicate, object, graph);
		
	}

	private Node asJsonLdNode(RDFTerm object) {
		if (object instanceof IRI) {
			return new RDFDataset.IRI( ((IRI)object).getIRIString() );
		}
		if (object instanceof BlankNode) {
			return new RDFDataset.BlankNode( ((BlankNode)object).uniqueReference() );
		}
		if (object instanceof Literal) {
			Literal literal = (Literal) object;
			return new RDFDataset.Literal(literal.getLexicalForm(), literal.getDatatype().getIRIString(), 
					literal.getLanguageTag().get());
		}
		throw new IllegalArgumentException("RDFTerm not instanceof IRI, BlankNode or Literal: " + object);
	}

	@Override
	public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		new RDFDataset.Quad(asJsonLdNode(subject), asJsonLdNode(predicate), asJsonLdNode(object), null); 
		
	}

	@Override
	public boolean contains(Triple triple) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void remove(Triple triple) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Stream<? extends Triple> getTriples() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<? extends Triple> getTriples(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		// TODO Auto-generated method stub
		return null;
	}
}
