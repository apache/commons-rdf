package org.apache.commons.rdf.simple.experimental;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.experimental.ParserFactory.Source;

public final class IRISource implements Source<IRI> {

    private final IRI source;

    public IRISource(IRI iri) {
        this.source = iri;
    }

    @Override
    public IRI source() {
        return source;
    }


}
