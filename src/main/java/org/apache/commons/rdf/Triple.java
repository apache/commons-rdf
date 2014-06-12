package org.apache.commons.rdf;

public interface Triple {

    Resource getSubject();

    IRI getProperty();

    RDFTerm getObject();

}
