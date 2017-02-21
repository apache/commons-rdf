package org.apache.commons.rdf.simple.experimental;

import java.util.Map;

import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.experimental.ParserFactory.Option;
import org.apache.commons.rdf.experimental.ParserFactory.Source;
import org.apache.commons.rdf.experimental.ParserFactory.Target;

public interface ParserImplementation {
    @SuppressWarnings("rawtypes")
    public long parse(Source source, RDFSyntax rdfSyntax, Target target, RDF rdf, Map<Option, Object> map);
}
