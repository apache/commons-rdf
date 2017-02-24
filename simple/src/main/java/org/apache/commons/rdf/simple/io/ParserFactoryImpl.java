package org.apache.commons.rdf.simple.io;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.io.NeedSourceOrBase;
import org.apache.commons.rdf.api.io.NeedTargetOrRDF;
import org.apache.commons.rdf.api.io.OptionalTarget;
import org.apache.commons.rdf.api.io.ParserFactory;
import org.apache.commons.rdf.api.io.ParserTarget;

public class ParserFactoryImpl implements ParserFactory {

    private ParserImplementation impl;

    public ParserFactoryImpl(ParserImplementation impl) {
        this.impl = impl;
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
        return new ParserBuilder(mutableState().withTarget(target));
    }

    @SuppressWarnings("unchecked")
    @Override
    public OptionalTarget<Dataset> rdf(RDF rdf) {
        return new ParserBuilder(mutableState().withRDF(rdf));
    }

    private ParseJob mutableState() {
        return new MutableParseJob().withImplementation(impl);
    }

    @Override
    public NeedTargetOrRDF syntax(RDFSyntax syntax) {
        return new ParserBuilder(mutableState().withSyntax(syntax));
    }

}
