package org.apache.commons.rdf;

import java.util.Optional;

public interface Literal extends RDFTerm {

    String getLexicalForm();

    IRI getDatatype();

    Optional<String> getLanguageTag();
}
