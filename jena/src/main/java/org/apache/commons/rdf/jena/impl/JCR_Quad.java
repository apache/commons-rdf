package org.apache.commons.rdf.jena.impl;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaCommonsRDF;
import org.apache.commons.rdf.jena.JenaQuad;

public class JCR_Quad implements Quad, JenaQuad {

	private final Optional<BlankNodeOrIRI> graphName;	
    private final BlankNodeOrIRI subject ;
    private final IRI predicate ;
    private final RDFTerm object ;
    private org.apache.jena.sparql.core.Quad quad = null ;

    /* package */ JCR_Quad(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        this.graphName = Objects.requireNonNull(graphName);
		this.subject = Objects.requireNonNull(subject) ;
        this.predicate = Objects.requireNonNull(predicate) ;
        this.object = Objects.requireNonNull(object) ;
    }
    
    /* package */ JCR_Quad(org.apache.jena.sparql.core.Quad quad) {
        this.quad = Objects.requireNonNull(quad) ;
    	this.graphName = Optional.of((BlankNodeOrIRI)JCR_Factory.fromJena(quad.getGraph())) ;
        this.subject = (BlankNodeOrIRI)JCR_Factory.fromJena(quad.getSubject()) ;
        this.predicate = (IRI)JCR_Factory.fromJena(quad.getPredicate()) ;
        this.object = JCR_Factory.fromJena(quad.getObject()) ;
    }

    @Override
    public org.apache.jena.sparql.core.Quad getQuad() {
        if ( quad == null ) {
            quad = org.apache.jena.sparql.core.Quad.create(
            		JenaCommonsRDF.toJena(graphName.orElse(null)), 
            		JenaCommonsRDF.toJena(subject), 
            		JenaCommonsRDF.toJena(predicate), 
            		JenaCommonsRDF.toJena(object)) ;
        }
        return quad ;
    }

    @Override
    public BlankNodeOrIRI getSubject() {
        return subject ;
    }

    @Override
    public IRI getPredicate() {
        return predicate ;
    }

    @Override
    public RDFTerm getObject() {
        return object ;
    }

    @Override
	public Optional<BlankNodeOrIRI> getGraphName() {
		return graphName;
	}
    
    @Override
    public int hashCode() {
        return Objects.hash(getSubject(), getPredicate(), getObject(), getGraphName()) ;
    }

    @Override
    public boolean equals(Object other) {
        if ( other == this ) return true ;
        if ( other == null ) return false ;
        if ( ! ( other instanceof Quad ) ) return false ;
        Quad quad = (Quad)other ;
        return getGraphName().equals(quad.getGraphName()) &&
        		getSubject().equals(quad.getSubject()) &&
        		getPredicate().equals(quad.getPredicate()) &&
        		getObject().equals(quad.getObject()) ;
    }
    
    @Override 
    public String toString() {
    	// kind of nquad syntax
		return getSubject().ntriplesString() + " " + 
    			getPredicate().ntriplesString() + " " + 
    			getObject().ntriplesString() + " " + 
    			getGraphName().map(RDFTerm::ntriplesString).orElse("") +  ".";
    }

    @Override
    public Triple asTriple() {
    	return new JCR_Triple(getSubject(), getPredicate(), getObject());
    }

}
