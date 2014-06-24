package org.apache.commons.rdf;

/**
 * An <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-rdf-term" >RDF-1.1
 * Term</a>, as defined by <a href= "http://www.w3.org/TR/rdf11-concepts/"
 * >RDF-1.1 Concepts and Abstract Syntax</a>, a W3C Recommendation published on
 * 25 February 2014.<br>
 *
 * @see <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-rdf-term" >RDF-1.1
 * Term</a>
 */
public interface RDFTerm {

    /**
     * Return the term serialised as specified by the RDF-1.1 N-Triples Canonical form.
     *
     * @return The term serialised as RDF-1.1 N-Triples.
     * @see <a href="http://www.w3.org/TR/n-triples/#canonical-ntriples">
     *         RDF-1.1 N-Triples Canonical form</a>
     */
    String ntriplesString();

}
