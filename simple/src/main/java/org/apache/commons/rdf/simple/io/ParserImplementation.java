package org.apache.commons.rdf.simple.io;

import java.util.Map;

import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.io.Option;
import org.apache.commons.rdf.api.io.ParserSource;
import org.apache.commons.rdf.api.io.ParserTarget;

public interface ParserImplementation {
    @SuppressWarnings("rawtypes")
    public long parse(ParserSource source, RDFSyntax rdfSyntax, ParserTarget target, RDF rdf, Map<Option, Object> options);
}
