package com.github.commonsrdf.dummyimpl;

import com.github.commonsrdf.api.BlankNodeOrIRI;
import com.github.commonsrdf.api.IRI;
import com.github.commonsrdf.api.RDFTerm;
import com.github.commonsrdf.api.Triple;

public class TripleImpl implements Triple {

	private RDFTerm object;
	private IRI predicate;
	private BlankNodeOrIRI subject;

	public TripleImpl(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	@Override
	public RDFTerm getObject() {
		return object;
	}

	@Override
	public IRI getPredicate() {
		return predicate;
	}

	@Override
	public BlankNodeOrIRI getSubject() {
		return subject;
	}

	@Override
	public String toString() {
		return getSubject().ntriplesString() + " "
				+ getPredicate().ntriplesString() + " "
				+ getObject().ntriplesString() + ".";
	}
}
