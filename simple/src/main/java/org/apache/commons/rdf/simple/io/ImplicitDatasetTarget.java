package org.apache.commons.rdf.simple.io;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.io.ParserTarget;

public class ImplicitDatasetTarget implements ParserTarget<Dataset> {

    private final RDF rdf;
    
    private Dataset target;

    public ImplicitDatasetTarget(RDF rdf) {
        this.rdf = rdf;
    }
    
    @Override
    public Dataset dest() {
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
        dest().add(t);        
    }
}
