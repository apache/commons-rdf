package org.apache.commons.rdf.simple.io;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.io.Async;
import org.apache.commons.rdf.api.io.NeedSourceBased;
import org.apache.commons.rdf.api.io.NeedSourceOrBase;
import org.apache.commons.rdf.api.io.NeedTargetOrRDF;
import org.apache.commons.rdf.api.io.Option;
import org.apache.commons.rdf.api.io.OptionalTarget;
import org.apache.commons.rdf.api.io.Parsed;
import org.apache.commons.rdf.api.io.ParserSource;
import org.apache.commons.rdf.api.io.ParserTarget;
import org.apache.commons.rdf.api.io.Sync;
import org.apache.commons.rdf.api.io.Option.RequiredOption;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class ParserBuilder implements NeedTargetOrRDF, NeedSourceBased, Sync, NeedSourceOrBase, OptionalTarget,
        Async {

    public static enum AsyncOption implements RequiredOption<ExecutorService> {
        EXECUTOR_SERVICE
    }

    public static enum BaseOption implements RequiredOption<IRI> {
        BASE        
    }
    
    private final ParseJob parseJob;

    public ParserBuilder() {
        this.parseJob = new DefaultParseJob();
    }

    public ParserBuilder(ParserImplementation impl) {
        this.parseJob = new WithImplementation(impl);
    }

    public ParserBuilder(ParseJob parseJob) {
        this.parseJob = parseJob;
    }

    @Override
    public Async async() {
        return async(ForkJoinPool.commonPool());
    }

    @Override
    public Async async(ExecutorService executor) {
        return newState(parseJob.withOption(AsyncOption.EXECUTOR_SERVICE, executor));
    }

    @Override
    public NeedSourceBased<Dataset> base(IRI iri) {
        return newState(implicitTarget().withOption(BaseOption.BASE, iri));
    }

    @Override
    public NeedSourceBased<Dataset> base(String iri) {
        return base(parseJob.rdf().createIRI(iri));
    }

    @Override
    public ParserBuilder build() {
        return newState(parseJob.freeze());
    }

    @Override
    public ParserBuilder option(Option o, Object v) {
        return newState(parseJob.withOption(o, v));
    }

    @Override
    public Parsed parse() {
        ParserImplementation impl = parseJob.impl();
        long count = impl.parse(parseJob.source(), parseJob.syntax().orElse(null), parseJob.target(), parseJob.rdf(), parseJob.optionsAsMap());
        return new ParsedImpl<>(parseJob.source(), parseJob.target(), count);
    }

    @Override
    public Future parseAsync() {
        Map<Option, Object> options = parseJob.optionsAsMap();
        ExecutorService executor = (ExecutorService) options.getOrDefault(AsyncOption.EXECUTOR_SERVICE,
                ForkJoinPool.commonPool());
        return executor.submit(this::parse);
    }

    @Override
    public OptionalTarget rdf(RDF rdf) {
        return newState(parseJob.withRDF(rdf));
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
    public Sync source(ParserSource source) {
        return newState(implicitTarget().withSource(source));
    }

    @Override
    public Sync source(String iri) {
        return source(parseJob.rdf().createIRI(iri));
    }

    public NeedSourceOrBase syntax(RDFSyntax syntax) {
        return newState(parseJob.withSyntax(syntax));
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
    public NeedSourceOrBase target(ParserTarget target) {
        return newState(parseJob.withTarget(target));
    }

    private ParseJob implicitTarget() {
        return parseJob.withTarget(new ImplicitDatasetTarget(parseJob.rdf()));
    }

    private ParserBuilder newState(ParseJob newState) {
        if (this.parseJob == newState) {
            // probably a MutableParseJob
            return this;
        }
        return new ParserBuilder(newState);
    }

}
