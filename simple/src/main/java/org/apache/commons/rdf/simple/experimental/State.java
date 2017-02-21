package org.apache.commons.rdf.simple.experimental;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.experimental.ParserFactory.Option;
import org.apache.commons.rdf.experimental.ParserFactory.Source;
import org.apache.commons.rdf.experimental.ParserFactory.Target;
import org.apache.commons.rdf.simple.SimpleRDF;

@SuppressWarnings("rawtypes")
interface State {
    ParserImplementation impl();
    RDF rdf();
    Source source();
    Target target();
    RDFSyntax syntax();
    Stream<Map.Entry<Option, Object>> options();
    Map<Option, Object> optionsAsMap();
    State withRDF(RDF rdf);
    State withSource(Source p);
    State withTarget(Target g);
    State withSyntax(RDFSyntax s);
    <O> State withOption(Option<O> o, O v);
    State withImplementation(ParserImplementation impl);
    State freeze();
}

@SuppressWarnings("rawtypes")
final class MutableState implements State, Cloneable {
    private ParserImplementation impl;
    private RDF rdf = new SimpleRDF();
    private Source source;
    private Target target;
    private RDFSyntax syntax;
    private Map<Option, Object> options = new LinkedHashMap<>();

    @Override
    public ParserImplementation impl() {
        return Objects.requireNonNull(impl);
    }
    public State freeze() {
        // options will be cloned inside constructor
        return new FrozenState(impl, source, syntax, target, rdf, options);
    }
    @Override
    public RDF rdf() {
        return Objects.requireNonNull(rdf);
    }
    @Override
    public Source source() {
        return Objects.requireNonNull(source);
    }
    @Override
    public Target target() {
        return Objects.requireNonNull(target);
    }
    @Override
    public RDFSyntax syntax() {
        return Objects.requireNonNull(syntax);
    }
    @Override
    public Stream<Entry<Option, Object>> options() {
        return options.entrySet().stream();
    }
    @Override
    public Map<Option, Object> optionsAsMap() {
        return Collections.unmodifiableMap(options);
    }
    @Override
    public State withRDF(RDF rdf) {
        this.rdf = rdf;
        return this;
    }
    @Override
    public State withSource(Source s) {
        this.source = s;
        return this;
    }
    @Override
    public State withTarget(Target t) {
        this.target = t;
        return this;
    }
    @Override
    public State withSyntax(RDFSyntax s) {
        this.syntax = s;
        return this;
    }
    @Override
    public <O> State withOption(Option<O> o, O v) {
        options.put(o, v);
        return this;
    }
    @Override
    public State withImplementation(ParserImplementation impl) {
        this.impl = impl;
        return this;
    }
}


@SuppressWarnings("rawtypes")
abstract class ImmutableState implements State {
    @Override
    public State withSource(Source src) {
        return new WithSource(src, this);
    }
    @Override
    public State withSyntax(RDFSyntax s) {
        return new WithSyntax(s, this);
    }
    @Override
    public State withTarget(Target t) {
        return new WithTarget(t, this);
    }
    public State withRDF(RDF rdf) {
        return new WithRDF(rdf, this);
    };
    public <O> State withOption(Option<O> o, O v) {
        return new WithOption(o, v, this);
    };
    @Override
    public State withImplementation(ParserImplementation impl) {
        return new WithImplementation(impl, this);
    }
    @Override
    public State freeze() {
        return this;
    }
}

@SuppressWarnings("rawtypes")
final class DefaultState extends ImmutableState {
    @Override
    public Source source() {
        throw new IllegalStateException("Source not set");
    }
    @Override
    public Target target() {
        throw new IllegalStateException("Target not set");
    }
    @Override
    public RDFSyntax syntax() {
        throw new IllegalStateException("Syntax not set");
    }
    @Override
    public RDF rdf() {
        return new SimpleRDF(); // fresh every time?
    }
    @Override
    public Stream<Entry<Option, Object>> options() {
        return Stream.empty();
    }
    @Override
    public Map<Option, Object> optionsAsMap() {
        return Collections.emptyMap();
    }
    @Override
    public ParserImplementation impl() {
        throw new IllegalStateException("Implementation not set");
    }
}

@SuppressWarnings("rawtypes")
final class FrozenState extends ImmutableState implements State {
    private final ParserImplementation impl;
    private final Source source;
    private final RDFSyntax syntax;
    private final Target target;
    private final RDF rdf;
    private final Map<Option, Object> options;

    public FrozenState(ParserImplementation impl, Source source, RDFSyntax syntax, Target target, RDF rdf,
            Map<Option, Object> options) {
        this.impl = impl;
        this.source = source;
        this.syntax = syntax;
        this.target = target;
        this.rdf = rdf;
        // shallow copy of options
        this.options = Collections.unmodifiableMap(new LinkedHashMap<>(options));
    }
    @Override
    public ParserImplementation impl() {
        return impl;
    }
    @Override
    public RDF rdf() {
        return rdf;
    }
    @Override
    public Source source() {
        return source;
    }
    @Override
    public Target target() {
        return target;
    }
    @Override
    public RDFSyntax syntax() {
        return syntax;
    }
    @Override
    public Stream<Entry<Option, Object>> options() {
        return options.entrySet().stream();
    }
    @Override
    public Map<Option, Object> optionsAsMap() {
        return options;
    }
}

@SuppressWarnings("rawtypes")
abstract class Inherited extends ImmutableState {
    private final ImmutableState parent;
    public Inherited() {
        this(new DefaultState());
    }
    public Inherited(ImmutableState state) {
        parent = state;
    }
    @Override
    public Source source() {
        return parent.source();
    }
    @Override
    public Target target() {
        return parent.target();
    }
    @Override
    public RDFSyntax syntax() {
        return parent.syntax();
    }
    @Override
    public RDF rdf() {
        return parent.rdf();
    }
    @Override
    public Stream<Entry<Option, Object>> options() {
        return parent.options();
    }
    @Override
    public Map<Option, Object> optionsAsMap() {
        return parent.optionsAsMap();
    }
    @Override
    public ParserImplementation impl() {
        return parent.impl();
    }
}

@SuppressWarnings("rawtypes")
final class WithSource extends Inherited {
    private final Source source;

    public WithSource(final Source src) {
        this.source = src;
    }
    public WithSource(final Source src, final ImmutableState state) {
        super(state);
        this.source = src;
    }
    @Override
    public Source source() {
        return source;
    }
}

@SuppressWarnings("rawtypes")
final class WithTarget extends Inherited {
    private final Target target;

    public WithTarget(final Target t) {
        this.target = t;
    }
    public WithTarget(final Target t, final ImmutableState state) {
        super(state);
        this.target = t;
    }
    @Override
    public Target target() {
        return target;
    }
}

final class WithSyntax extends Inherited {
    private final RDFSyntax syntax;
    public WithSyntax(final RDFSyntax s) {
        syntax = s;
    }
    public WithSyntax(final RDFSyntax s, final ImmutableState state) {
        super(state);
        syntax = s;
    }
    @Override
    public RDFSyntax syntax() {
        return syntax;
    }
}

final class WithRDF extends Inherited {
    private final RDF rdf;
    public WithRDF(final RDF r) {
        rdf = r;
    }
    public WithRDF(final RDF r, final ImmutableState state) {
        super(state);
        rdf = r;
    }
    @Override
    public RDF rdf() {
        return rdf;
    }
}

@SuppressWarnings({ "rawtypes", "unchecked" })
final class WithOption extends Inherited {
    private Option<? extends Object> option;
    private Object value;
    public <O> WithOption(Option<O> o, O v, ImmutableState immutableState) {
        this.option = o;
        this.value = v;
    }
    @Override
    public Stream<Entry<Option, Object>> options() {
        return Stream.concat(super.options(), Stream.of(new SimpleImmutableEntry(option, value)));
    }
    @Override
    public Map<Option, Object> optionsAsMap() {
        return options().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }
}

final class WithImplementation extends Inherited {
    private final ParserImplementation impl;
    public WithImplementation(ParserImplementation impl) {
        this.impl = impl;
    }
    public WithImplementation(ParserImplementation impl, ImmutableState parent) {
        super(parent);
        this.impl = impl;
    }
    @Override
    public ParserImplementation impl() {
        return impl;
    }
}
