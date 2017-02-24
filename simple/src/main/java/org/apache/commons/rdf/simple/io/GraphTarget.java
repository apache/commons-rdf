package org.apache.commons.rdf.simple.io;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.io.ParserTarget;

public class GraphTarget implements ParserTarget<Graph> {

    private Graph graph;

    public GraphTarget(Graph graph) {
        this.graph = graph;
    }

    @Override
    public void accept(Quad q) {
        if (! q.getGraphName().isPresent()) {
            graph.add(q.asTriple());
        }
    }
    
    @Override
    public Graph target() {
        return graph;
    }

}
