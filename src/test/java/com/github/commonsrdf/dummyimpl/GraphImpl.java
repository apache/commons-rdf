package com.github.commonsrdf.dummyimpl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.commonsrdf.api.BlankNodeOrIRI;
import com.github.commonsrdf.api.Graph;
import com.github.commonsrdf.api.IRI;
import com.github.commonsrdf.api.RDFTerm;
import com.github.commonsrdf.api.Triple;

public class GraphImpl implements Graph {

	protected List<Triple> triples = new ArrayList<Triple>();

	@Override
	public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		add(new TripleImpl(subject, predicate, object));

	}

	@Override
	public void add(Triple triple) {
		if (triple == null) {
			throw new NullPointerException("triple can't be null");
		}
		triples.add(triple);
	}

	@Override
	public void clear() {
		triples.clear();
	}

	@Override
	public boolean contains(BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object) {
		return getTriples(subject, predicate, object).findFirst().isPresent();
	}

	@Override
	public boolean contains(Triple triple) {
		if (triple == null) {
			throw new NullPointerException("triple can't be null");
		}
		return triples.contains(triple);
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
				// Lacking the requirement for .equals() we have to be silly
				// and test ntriples string equivalance
				if (subject != null
						&& !t.getSubject().ntriplesString()
								.equals(subject.ntriplesString())) {
					return false;
				}
				if (predicate != null
						&& !t.getPredicate().ntriplesString()
								.equals(predicate.ntriplesString())) {
					return false;
				}
				if (object != null
						&& !t.getObject().ntriplesString()
								.equals(object.ntriplesString())) {
					return false;
				}
				return true;
			}
		};
		return getTriples().unordered().filter(match);
	}

	@Override
	public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		for (Triple t : 
			getTriples(subject, predicate, object).collect(Collectors.toList())) {
			// Avoid ConcurrentModificationException in ArrayList
			remove(t);
		}
	}

	@Override
	public void remove(Triple triple) {
		if (triple == null) {
			throw new NullPointerException("triple can't be null");
		}
		triples.remove(triple);
	}

	@Override
	public long size() {
		return triples.size();
	}

	@Override
	public String toString() {
		return getTriples().map(Object::toString).collect(Collectors.joining("\n"));		
	}

}
