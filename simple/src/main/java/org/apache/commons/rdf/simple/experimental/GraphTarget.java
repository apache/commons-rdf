package org.apache.commons.rdf.simple.experimental;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.experimental.ParserFactory.Target;

public class GraphTarget implements Target<Graph> {

    private Graph graph;

    public GraphTarget(Graph graph) {
        this.graph = graph;
    }

    @Override
    public Graph target() {
        return graph;
    }

}
