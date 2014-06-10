package org.apache.commons.rdf;

public interface Literal extends Term {
	String getLexicalForm() ;

	IRI getDatatype() ;

    Optional<String> getLanguageTag() ;
}
