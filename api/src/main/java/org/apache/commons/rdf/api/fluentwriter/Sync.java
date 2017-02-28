package org.apache.commons.rdf.api.fluentwriter;

import java.util.concurrent.ExecutorService;

import org.apache.commons.rdf.api.io.Option;
import org.apache.commons.rdf.api.io.Written;

public interface Sync extends _Buildable {
    
    Sync build();    
    <V> Sync option(Option<V> option, V value);

    Written write(); // terminal
    Async async();
    Async async(ExecutorService service);
}
