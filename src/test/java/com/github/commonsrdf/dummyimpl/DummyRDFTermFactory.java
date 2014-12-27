package com.github.commonsrdf.dummyimpl;

import com.github.commonsrdf.api.BlankNode;
import com.github.commonsrdf.api.BlankNodeOrIRI;
import com.github.commonsrdf.api.Graph;
import com.github.commonsrdf.api.IRI;
import com.github.commonsrdf.api.Literal;
import com.github.commonsrdf.api.RDFTerm;
import com.github.commonsrdf.api.RDFTermFactory;
import com.github.commonsrdf.api.Triple;

public class DummyRDFTermFactory implements RDFTermFactory {

	@Override
	public Graph createGraph() {
		return new GraphImpl();
	}

	@Override
	public IRI createIRI(String iri) {
		return new IRIImpl(iri);
	}

	@Override
	public BlankNode createBlankNode() {
		return new BlankNodeImpl();
	}

	@Override
	public BlankNode createBlankNode(String identifier) {
		return new BlankNodeImpl(identifier);
	}

	@Override
	public Literal createLiteral(String literal) {
		return new LiteralImpl(literal);
	}

	@Override
	public Literal createLiteral(String literal, IRI dataType) {
		return new LiteralImpl(literal, dataType);
	}

	@Override
	public Literal createLiteral(String literal, String language) {
		return new LiteralImpl(literal, language);
	}

	@Override
	public Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object) {
		return new TripleImpl(subject, predicate, object);
	}
}
