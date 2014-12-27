package com.github.commonsrdf.dummyimpl;

import java.util.concurrent.atomic.AtomicLong;

import com.github.commonsrdf.api.BlankNode;

public class BlankNodeImpl implements BlankNode {

	private static AtomicLong bnodeCounter = new AtomicLong();
	private String id;

	public BlankNodeImpl() {
		this("b" + bnodeCounter.incrementAndGet());
	}

	public BlankNodeImpl(String id) {
		if (id == null || id.isEmpty() || id.contains(":")) {
			// TODO: Check against
			// http://www.w3.org/TR/n-triples/#n-triples-grammar
			throw new IllegalArgumentException("Invalid blank node id: " + id);
		}
		this.id = id;
	}

	@Override
	public String internalIdentifier() {
		return id;
	}

	@Override
	public String ntriplesString() {
		return "_:" + id;
	}

	@Override
	public String toString() {
		return ntriplesString();
	}

}
