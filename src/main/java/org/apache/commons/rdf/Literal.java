package org.apache.commons.rdf;

public interface Literal extends RDFTerm {
	String getLexicalForm() ;

	IRI getDatatype() ;

    Optional<String> getLanguageTag() ;
}
