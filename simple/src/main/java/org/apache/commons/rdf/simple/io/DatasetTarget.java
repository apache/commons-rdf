package org.apache.commons.rdf.simple.io;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.io.ParserTarget;

public class DatasetTarget implements ParserTarget<Dataset> {

    private final Dataset dataset;

    public DatasetTarget(Dataset dataset) {
        this.dataset = dataset;
    }
    
    @Override
    public void accept(Quad q) {
        dataset.add(q);
    }
    
    @Override
    public Dataset target() {
        return dataset;
    }

}
