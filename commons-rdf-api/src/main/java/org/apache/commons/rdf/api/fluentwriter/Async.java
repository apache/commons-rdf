package org.apache.commons.rdf.api.fluentwriter;

import java.util.concurrent.Future;

import org.apache.commons.rdf.api.io.Option;
import org.apache.commons.rdf.api.io.Written;

public interface Async extends _Buildable {
    
    Async build();    
    <V> Async option(Option<V> option, V value);
    
    Future<Written> writeAsync();
}
