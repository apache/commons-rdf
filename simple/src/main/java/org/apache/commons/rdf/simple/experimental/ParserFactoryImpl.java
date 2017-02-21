package org.apache.commons.rdf.simple.experimental;

import java.util.function.Consumer;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.experimental.ParserFactory;

public class ParserFactoryImpl implements ParserFactory {

    private State state;

    public ParserFactoryImpl(ParserImplementation impl) {
        this.state = new WithImplementation(impl);
    }
    
    @Override
    public NeedSourceOrBase<Graph> target(Graph graph) {
        return target(new GraphTarget(graph));
    }

    @Override
    public NeedSourceOrBase<Dataset> target(Dataset dataset) {
        return target(new DatasetTarget(dataset));
    }

    @Override
    public NeedSourceOrBase<Consumer<Quad>> target(Consumer<? super Quad> consumer) {
        return target(new QuadConsumerTarget(consumer));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> NeedSourceOrBase<T> target(Target<T> target) {
        return new ParserBuilder(state.withTarget(target));
    }

    @SuppressWarnings("unchecked")
    @Override
    public OptionalTarget<Dataset> rdf(RDF rdf) {
        return new ParserBuilder(state.withRDF(rdf));
    }

    @Override
    public NeedTargetOrRDF syntax(RDFSyntax syntax) {
        return new ParserBuilder(state.withSyntax(syntax));
    }

}
