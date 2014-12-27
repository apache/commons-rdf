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
		this.id = id;
	}

	@Override
	public String ntriplesString() {
		return "_:" + id;
	}

	@Override
	public String internalIdentifier() {
		return id;
	}

}
