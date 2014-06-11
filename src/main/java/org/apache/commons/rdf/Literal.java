package org.apache.commons.rdf;

import java.util.Optional;

/**
 * An RDF-1.1 Literal, as defined by <a href=
 * "http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#section-Graph-Literal"
 * >RDF-1.1 Concepts and Abstract Syntax</a>, a W3C Recommendation published on
 * 25 February 2014
 */
public interface Literal extends RDFTerm {
	
	/**
	 * The lexical form of this literal, represented by a <a
	 * href="http://www.unicode.org/versions/latest/">Unicode string</a>, which
	 * SHOULD be in <a href="http://www.unicode.org/reports/tr15/">Normal Form
	 * C</a>.
	 * 
	 * @return The lexical form of this literal.
	 * @see <a
	 *      href="http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#dfn-lexical-form">RDF-1.1
	 *      Literal lexical form</a>
	 */
	String getLexicalForm();

	/**
	 * The IRI identifying the datatype that determines how the lexical form
	 * maps to a literal value.
	 * 
	 * @return The datatype IRI for this literal.
	 * @see <a
	 *      href="http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#dfn-datatype-iri">RDF-1.1
	 *      Literal datatype IRI</a>
	 */
	IRI getDatatype();

	/**
	 * If and only if the datatype IRI is <a
	 * href="http://www.w3.org/1999/02/22-rdf-syntax-ns#langString"
	 * >http://www.w3.org/1999/02/22-rdf-syntax-ns#langString</a>, the language
	 * tag is a non-empty language tag as defined by <a
	 * href="http://tools.ietf.org/html/bcp47">BCP47</a>.
	 * 
	 * @return The {@link Optional} language tag for this literal.
	 * @see <a
	 *      href="http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#dfn-language-tag">RDF-1.1
	 *      Literal language tag</a>
	 */
	Optional<String> getLanguageTag();
	
}
