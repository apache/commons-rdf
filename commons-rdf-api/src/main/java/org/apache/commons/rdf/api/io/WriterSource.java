package org.apache.commons.rdf.api.io;

import java.util.stream.Stream;

import org.apache.commons.rdf.api.TripleLike;

public interface WriterSource {
    Stream<? extends TripleLike> stream();
}
