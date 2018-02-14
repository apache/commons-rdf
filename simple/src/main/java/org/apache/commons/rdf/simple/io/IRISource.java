package org.apache.commons.rdf.simple.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.io.ParserSource;

public final class IRISource implements ParserSource<IRI> {

    private final IRI source;

    public IRISource(IRI iri) {
        this.source = iri;
    }

    @Override
    public IRI src() {
        return source;
    }

    /**
     * NOTE: This does not follow HTTP redirects or perform content negotiation
     * and should not generally be used.
     */
    @Override
    public InputStream inputStream() {
        // NOTE: This does not follow HTTP redirects, content negotiation,
        try {
            return asURL().openStream();
        } catch (IOException ex) {
            throw new UnsupportedOperationException("Unable to connect to " + source.getIRIString(), ex);
        }
    }

    private URL asURL() throws MalformedURLException {
        return new URL(source.getIRIString());
    }

    /**
     * NOTE: This does not follow HTTP redirects, content negotiation, or
     * respect the Content-Location, and so should NOT be used as a base URL
     * when parsing.
     */
    @Override
    public Optional<IRI> iri() {
        return Optional.of(source);
    }

}
