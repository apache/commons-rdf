package com.github.commonsrdf.dummyimpl;

import java.net.URI;

import com.github.commonsrdf.api.IRI;

public class IRIImpl implements IRI {

	protected URI uri;
	
	public IRIImpl(String iri) {
		uri = URI.create(iri);
	}
	
	@Override
	public String ntriplesString() {
		return "<" + getIRIString() + ">";
	}

	@Override
	public String getIRIString() {
		return uri.toString();
	}

}
