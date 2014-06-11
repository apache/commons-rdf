package org.apache.commons.rdf;

/**
 * An abstraction used to represent cases where either a {@link BlankNode} or an
 * {@link IRI} can be used interchangeably.<br>
 * Examples of its use include <a href=
 * "http://www.w3.org/TR/rdf11-concepts/#dfn-subject" >RDF-1.1 Triple
 * subjects</a> and <a href=
 * "http://www.w3.org/TR/rdf11-concepts/#dfn-named-graph" >RDF-1.1 Named
 * Graphs</a>.
 */
public interface BlankNodeOrIRI extends RDFTerm {
}