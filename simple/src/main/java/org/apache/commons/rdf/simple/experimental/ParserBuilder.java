package org.apache.commons.rdf.simple.experimental;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.experimental.ParserFactory.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class ParserBuilder implements NeedTargetOrRDF, NeedSourceBased, Sync, NeedSourceOrBase, OptionalTarget,
        NeedSourceOrBaseOrSyntax, Async {

    public static enum AsyncOption implements Option<ExecutorService> {
        EXECUTOR_SERVICE
    }

    public static enum BaseOption implements Option<IRI> {
        BASE        
    }
    
    private final State state;

    public ParserBuilder() {
        this.state = new DefaultState();
    }

    public ParserBuilder(ParserImplementation impl) {
        this.state = new WithImplementation(impl);
    }

    public ParserBuilder(State state) {
        this.state = state;
    }

    @Override
    public Async async() {
        return async(ForkJoinPool.commonPool());
    }

    @Override
    public Async async(ExecutorService executor) {
        return newState(state.withOption(AsyncOption.EXECUTOR_SERVICE, executor));
    }

    @Override
    public NeedSourceBased<Dataset> base(IRI iri) {
        return newState(implicitTarget().withOption(BaseOption.BASE, iri));
    }

    @Override
    public NeedSourceBased<Dataset> base(String iri) {
        return base(state.rdf().createIRI(iri));
    }

    public ParserBuilder build() {
        return newState(state.freeze());
    }

    @Override
    public ParserBuilder option(Option o, Object v) {
        return newState(state.withOption(o, v));
    }

    @Override
    public Parsed parse() {
        ParserImplementation impl = state.impl();
        long count = impl.parse(state.source(), state.syntax(), state.target(), state.rdf(), state.optionsAsMap());
        return new ParsedImpl<>(state.source(), state.target(), count);
    }

    @Override
    public Future parseAsync() {
        Map<Option, Object> options = state.optionsAsMap();
        ExecutorService executor = (ExecutorService) options.getOrDefault(AsyncOption.EXECUTOR_SERVICE,
                ForkJoinPool.commonPool());
        return executor.submit(this::parse);
    }

    @Override
    public OptionalTarget rdf(RDF rdf) {
        return newState(state.withRDF(rdf));
    }

    @Override
    public Sync source(InputStream is) {
        return source(new InputStreamSource(is));
    }

    @Override
    public Sync source(IRI iri) {
        return source(new IRISource(iri));
    }

    @Override
    public Sync source(Path path) {
        return source(new PathSource(path));
    }

    @Override
    public Sync source(Source source) {
        return newState(implicitTarget().withSource(source));
    }

    @Override
    public Sync source(String iri) {
        return source(state.rdf().createIRI(iri));
    }

    public NeedSourceOrBase syntax(RDFSyntax syntax) {
        return newState(state.withSyntax(syntax));
    }

    @Override
    public NeedSourceOrBase<Consumer<Quad>> target(Consumer<? super Quad> consumer) {
        return target(new QuadConsumerTarget(consumer));
    }

    @Override
    public NeedSourceOrBase<Dataset> target(Dataset dataset) {
        return target(new DatasetTarget(dataset));
    }

    @Override
    public NeedSourceOrBase<Graph> target(Graph graph) {
        return target(new GraphTarget(graph));
    }

    @Override
    public NeedSourceOrBase target(Target target) {
        return newState(state.withTarget(target));
    }

    private State implicitTarget() {
        return state.withTarget(new ImplicitDatasetTarget(state.rdf()));
    }

    private ParserBuilder newState(State newState) {
        if (this.state == newState) {
            // probably a MutableState
            return this;
        }
        return new ParserBuilder(newState);
    }

}
