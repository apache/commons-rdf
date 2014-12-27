package com.github.commonsrdf.dummyimpl;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.github.commonsrdf.api.BlankNodeOrIRI;
import com.github.commonsrdf.api.Graph;
import com.github.commonsrdf.api.IRI;
import com.github.commonsrdf.api.RDFTerm;
import com.github.commonsrdf.api.Triple;

public class GraphImpl implements Graph {

	protected Set<Triple> triples = new LinkedHashSet<Triple>();

	@Override
	public void add(Triple triple) {
		if (triple == null) {
			throw new NullPointerException("triple can't be null");
		}
		triples.add(triple);
	}

	@Override
	public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		add(new TripleImpl(subject, predicate, object));

	}

	@Override
	public boolean contains(Triple triple) {
		if (triple == null) {
			throw new NullPointerException("triple can't be null");
		}
		return triples.contains(triple);
	}

	@Override
	public boolean contains(BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object) {
		return getTriples(subject, predicate, object).findFirst().isPresent();
	}

	@Override
	public void remove(Triple triple) {
		if (triple == null) {
			throw new NullPointerException("triple can't be null");
		}
		triples.remove(triple);
	}

	@Override
	public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		Iterator<? extends Triple> it = getTriples(subject, predicate, object)
				.iterator();
		while (it.hasNext()) {
			it.remove();
		}
	}

	@Override
	public void clear() {
		triples.clear();
	}

	@Override
	public long size() {
		return triples.size();
	}

	@Override
	public Stream<? extends Triple> getTriples() {
		return triples.parallelStream();
	}

	@Override
	public Stream<? extends Triple> getTriples(final BlankNodeOrIRI subject,
			final IRI predicate, final RDFTerm object) {
		Predicate<Triple> match = new Predicate<Triple>() {
			@Override
			public boolean test(Triple t) {
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
			}
		};
		return getTriples().filter(match);
	}

	@Override
	public String toString() {
		// thread-safe StringBuffer as forEach use parallel streams
		final StringBuffer sb = new StringBuffer();
		getTriples().parallel().forEach(t -> sb.append(t.toString() + "\n"));
		return sb.toString();
	}

}
