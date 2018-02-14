package org.apache.commons.rdf.simple.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.io.ParserSource;
import org.apache.commons.rdf.simple.SimpleRDF;

public class PathSource implements ParserSource<Path> {
    private final Path source;

    private static final RDF rdf = new SimpleRDF();
    
    public PathSource(Path source) {
        this.source = source;
    }

    @Override
    public Path src() {
        return source;
    }

    @Override
    public InputStream inputStream() throws IOException {
        return Files.newInputStream(source);
    }

    @Override
    public Optional<IRI> iri() {
        Path p;
        try {
            // Resolve any symlinks etc.
            p = source.toRealPath();
        } catch (IOException e) {
            // Probably some folder is not found, use full path as-is
            p = source.toAbsolutePath();
        }
        return Optional.of(rdf.createIRI(p.toUri().toString()));
    }

}
