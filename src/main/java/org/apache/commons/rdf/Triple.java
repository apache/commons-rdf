package org.apache.commons.rdf;

public interface Triple extends TermTuple {
    // Repeat for clarity

    BlankNodeOrIRI getSubject();

    IRI getProperty();

    Resource getObject();
}
