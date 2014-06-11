package org.apache.commons.rdf;

/**
 * An <a href=
 * "http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#dfn-rdf-term"
 * >RDF-1.1 Term</a>.
 * 
 * @see <a href=
 *      "http://www.w3.org/TR/2014/REC-rdf11-concepts-20140225/#dfn-rdf-term"
 *      >RDF-1.1 Term</a>
 */
public interface RDFTerm {

	/**
	 * Return the term representation (TBD)
	 *
	 * @return term representation
	 */
	String toString();

}
