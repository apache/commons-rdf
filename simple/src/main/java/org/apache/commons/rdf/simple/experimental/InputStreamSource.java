package org.apache.commons.rdf.simple.experimental;

import java.io.InputStream;

import org.apache.commons.rdf.experimental.ParserFactory.Source;

public class InputStreamSource implements Source<InputStream> {

    private final InputStream source;

    public InputStreamSource(InputStream source) {
        this.source = source;
    }

    @Override
    public InputStream source() {
        return source;
    }
    
}
