package org.apache.commons.rdf.simple.experimental;

import java.nio.file.Path;

import org.apache.commons.rdf.experimental.ParserFactory.Source;

public class PathSource implements Source<Path> {
    private final Path source;

    public PathSource(Path source) {
        this.source = source;
    }
    
    @Override
    public Path source() {
        return source;
    }


}
