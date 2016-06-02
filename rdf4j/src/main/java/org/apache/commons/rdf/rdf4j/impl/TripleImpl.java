package org.apache.commons.rdf.rdf4j.impl;

import java.util.Objects;
import java.util.UUID;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.rdf4j.RDF4JTermFactory;
import org.apache.commons.rdf.rdf4j.RDF4JTriple;
import org.eclipse.rdf4j.model.Statement;

public final class TripleImpl implements Triple, RDF4JTriple {
		private final Statement statement;	
		private UUID salt;
		
		public TripleImpl(Statement statement, UUID salt) {
			this.statement = statement;
			this.salt = salt;
		}
	
		public Statement asStatement() { 
			return statement;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Triple) {
				Triple triple = (Triple) obj;
				return getSubject().equals(triple.getSubject()) &&
						getPredicate().equals(triple.getPredicate()) && 
						getObject().equals(triple.getObject());
			}
			return false;
		}
	
		@Override
		public RDFTerm getObject() {
			return RDF4JTermFactory.asRDFTerm(statement.getObject(), salt);
		}
	
		@Override
		public org.apache.commons.rdf.api.IRI getPredicate() {
			return (org.apache.commons.rdf.api.IRI) RDF4JTermFactory.asRDFTerm(statement.getPredicate(), null);
		}
		
		@Override
		public BlankNodeOrIRI getSubject() {
			return (BlankNodeOrIRI) RDF4JTermFactory.asRDFTerm(statement.getSubject(), salt);
		}
	
		@Override
		public int hashCode() {
			return Objects.hash(getSubject(), getPredicate(), getObject());
		}
		
		@Override
		public String toString() {
			return statement.toString();
		}
}