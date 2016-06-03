package org.apache.commons.rdf.rdf4j.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.rdf4j.RDF4JQuad;
import org.apache.commons.rdf.rdf4j.RDF4JTermFactory;
import org.eclipse.rdf4j.model.Statement;

public final class QuadImpl implements Quad, RDF4JQuad {
		private final Statement statement;	
		private UUID salt;
		private transient int hashCode = 0;
		
		public QuadImpl(Statement statement, UUID salt) {
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
			if (hashCode  != 0) {
				return hashCode;
			}
			return hashCode = Objects.hash(getSubject(), getPredicate(), getObject(), getGraphName());
		}
		
		@Override
		public String toString() {
			return statement.toString();
		}

		@Override
		public Optional<BlankNodeOrIRI> getGraphName() {
			if (statement.getContext() == null) { 
				return Optional.empty();
			}
			BlankNodeOrIRI g = (BlankNodeOrIRI) RDF4JTermFactory.asRDFTerm(statement.getContext(), salt);
			return Optional.of(g);
		}
}