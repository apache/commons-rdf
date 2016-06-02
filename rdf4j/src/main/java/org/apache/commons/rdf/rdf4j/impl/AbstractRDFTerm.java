package org.apache.commons.rdf.rdf4j.impl;

import org.apache.commons.rdf.rdf4j.RDF4JTerm;
import org.eclipse.rdf4j.model.Value;

abstract class AbstractRDFTerm<T extends Value> implements RDF4JTerm<T> {
	T value;

	AbstractRDFTerm(T value) {
		this.value = value;			
	}
	
	public T asValue() { 
		return value;
	}
}