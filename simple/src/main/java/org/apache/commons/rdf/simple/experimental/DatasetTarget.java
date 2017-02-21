package org.apache.commons.rdf.simple.experimental;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.experimental.ParserFactory.Target;

public class DatasetTarget implements Target<Dataset> {

    private final Dataset target;

    public DatasetTarget(Dataset target) {
        this.target = target;
    }
    
    @Override
    public Dataset target() {
        return target;
    }

}
