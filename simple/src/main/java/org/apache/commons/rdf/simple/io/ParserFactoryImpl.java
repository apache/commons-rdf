package org.apache.commons.rdf.simple.io;

import java.nio.file.Path;
import java.util.Set;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.fluentparser.NeedSourceBased;
import org.apache.commons.rdf.api.fluentparser.NeedSourceOrBase;
import org.apache.commons.rdf.api.fluentparser.NeedTargetOrRDF;
import org.apache.commons.rdf.api.fluentparser.OptionalTarget;
import org.apache.commons.rdf.api.fluentparser.Sync;
import org.apache.commons.rdf.api.io.Option;
import org.apache.commons.rdf.api.io.ParserFactory;
import org.apache.commons.rdf.api.io.ParserSource;
import org.apache.commons.rdf.api.io.ParserTarget;

public class ParserFactoryImpl implements ParserFactory {

    private final ParserImplementation impl;
    private final RDF rdf;
    private final Set<RDFSyntax> syntaxes;

    public ParserFactoryImpl(final RDF rdf, final ParserImplementation impl, 
                                final Set<RDFSyntax> syntaxes) {
        this.rdf = rdf;
        this.impl = impl;
        this.syntaxes = syntaxes;
    }
    
    @Override
    public NeedSourceOrBase<Graph> target(Graph graph) {
        return target(new GraphTarget(graph));
    }

    @Override
    public NeedSourceOrBase<Dataset> target(Dataset dataset) {
        return target(new DatasetTarget(dataset));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> NeedSourceOrBase<T> target(ParserTarget<T> target) {
        return new ParserBuilder(createMutableState().withTarget(target));
    }

    private ParseJob createMutableState() {
        return new MutableParseJob().withImplementation(impl).withRDF(rdf);
    }

    private ParseJob createImmutableMutableState() {
        return new DefaultParseJob().withImplementation(impl).withRDF(rdf);
    }
    
    @Override
    public NeedTargetOrRDF syntax(RDFSyntax syntax) {
        return new ParserBuilder(createMutableState().withSyntax(syntax));
    }

    @Override
    public Set<RDFSyntax> supportedSyntaxes() {
        return syntaxes;
    }

    @Override
    public ParserBuilder build() {
        return new ParserBuilder(createImmutableMutableState());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> OptionalTarget<Dataset> option(Option<V> o, V v) {
        return new ParserBuilder(createMutableState()).option(o, v);
    }

    @Override
    public NeedSourceBased<Dataset> base(IRI iri) {
        return new ParserBuilder(createMutableState()).base(iri);
    }

    @Override
    public NeedSourceBased<Dataset> base(String iri) {
        return new ParserBuilder(createMutableState()).base(iri);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Sync<Dataset, IRI> source(IRI iri) {
        return new ParserBuilder(createMutableState()).source(iri);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Sync<Dataset, Path> source(Path path) {
        return new ParserBuilder(createMutableState()).source(path);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> Sync<Dataset, S> source(ParserSource<S> source) {
        return new ParserBuilder(createMutableState()).source(source);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Sync<Dataset, IRI> source(String iri) {
        return new ParserBuilder(createMutableState()).source(iri);
    }

}
