package org.apache.commons.rdf.simple.experimental;

import org.apache.commons.rdf.experimental.ParserFactory.Parsed;
import org.apache.commons.rdf.experimental.ParserFactory.Source;
import org.apache.commons.rdf.experimental.ParserFactory.Target;

public class ParsedImpl<T,S> implements Parsed<T, S> {

    private final Source<S> source;
    private final Target<T> target;
    private final long count;

    public ParsedImpl(Source<S> source, Target<T> target, final long count) {
        this.source = source;
        this.target = target;
        this.count = count;
    }

    @Override
    public long count() {
        return count;
    }
    
    @Override
    public Source<S> source() {
        return source;
    }

    @Override
    public Target<T> target() {
        return target;
    }

}
