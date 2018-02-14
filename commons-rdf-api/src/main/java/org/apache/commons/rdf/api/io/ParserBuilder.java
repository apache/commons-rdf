package org.apache.commons.rdf.api.io;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.fluentparser.OptionalTarget;

public interface ParserBuilder extends OptionalTarget<Dataset> {
    ParserBuilder build();
    <V> ParserBuilder option(Option<V> option, V value);
}
