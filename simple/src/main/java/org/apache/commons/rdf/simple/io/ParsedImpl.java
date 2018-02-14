package org.apache.commons.rdf.simple.io;

import org.apache.commons.rdf.api.io.Parsed;
import org.apache.commons.rdf.api.io.ParserSource;
import org.apache.commons.rdf.api.io.ParserTarget;

public class ParsedImpl<T,S> implements Parsed<T, S> {

    private final ParserSource<S> source;
    private final ParserTarget<T> target;
    private final long count;

    public ParsedImpl(ParserSource<S> source, ParserTarget<T> target, final long count) {
        this.source = source;
        this.target = target;
        this.count = count;
    }

    @Override
    public long count() {
        return count;
    }
    
    @Override
    public ParserSource<S> from() {
        return source;
    }

    @Override
    public ParserTarget<T> into() {
        return target;
    }

}
