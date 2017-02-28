package org.apache.commons.rdf.api.fluentwriter;

import java.util.stream.Stream;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.TripleLike;
import org.apache.commons.rdf.api.io.Option;
import org.apache.commons.rdf.api.io.WriterSource;

public interface NeedSource extends _Buildable {
    
    NeedSource build();    
    <V> NeedSource option(Option<V> option, V value);
    
    Sync source(Dataset dataset);
    Sync source(Graph graph);
    Sync source(Stream<? extends TripleLike> stream);
    Sync source(WriterSource source);
}
