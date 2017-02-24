package org.apache.commons.rdf.simple.io;

import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.io.ParserSource;

public class InputStreamSource implements ParserSource<InputStream> {

    private final InputStream source;

    public InputStreamSource(InputStream source) {
        this.source = source;
    }

    @Override
    public InputStream source() {
        return source;
    }
    
    @Override
    public InputStream inputStream() {
        return source;
    }
    
    @Override
    public Optional<IRI> iri() {
        // Unknown base
        return Optional.empty();
    }
}
