package org.apache.commons.rdf.simple.experimental;

import java.util.function.Consumer;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.experimental.ParserFactory.Target;

public class ImplicitDatasetTarget implements Target<Dataset> {

    private final RDF rdf;
    
    private Dataset target;

    public ImplicitDatasetTarget(RDF rdf) {
        this.rdf = rdf;
    }
    
    @Override
    public Dataset target() {
        if (target == null) {
            synchronized (this) {
                // Make sure we only make it once
                if (target == null) {
                    target = rdf.createDataset();
                }
            }
        }
        return target;
    }

    @Override
    public void accept(Quad t) {
        target().add(t);        
    }
}
