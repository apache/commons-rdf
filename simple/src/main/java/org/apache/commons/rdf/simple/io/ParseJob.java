package org.apache.commons.rdf.simple.io;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.io.Option;
import org.apache.commons.rdf.api.io.ParserSource;
import org.apache.commons.rdf.api.io.ParserTarget;
import org.apache.commons.rdf.simple.SimpleRDF;

@SuppressWarnings("rawtypes")
public interface ParseJob {
    ParserImplementation impl();
    RDF rdf();
    ParserSource source();
    ParserTarget target();
    Optional<RDFSyntax> syntax();
    Stream<Map.Entry<Option, Object>> options();
    Map<Option, Object> optionsAsMap();
    ParseJob withRDF(RDF rdf);
    ParseJob withSource(ParserSource p);
    ParseJob withTarget(ParserTarget g);
    ParseJob withSyntax(RDFSyntax s);
    <O> ParseJob withOption(Option<O> o, O v);
    ParseJob withImplementation(ParserImplementation impl);
    ImmutableParseJob freeze();    
}

interface ImmutableParseJob extends ParseJob {
}

@SuppressWarnings("rawtypes")
final class MutableParseJob implements ParseJob, Cloneable {
    private ParserImplementation impl;
    private RDF rdf = new SimpleRDF();
    private ParserSource source;
    private ParserTarget target;
    private RDFSyntax syntax;
    private Map<Option, Object> options = new LinkedHashMap<>();

    @Override
    public ParserImplementation impl() {
        return Objects.requireNonNull(impl);
    }
    public ImmutableParseJob freeze() {
        return new FrozenParseJob(impl, source, syntax, target, rdf, options);
    }
    @Override
    public RDF rdf() {
        return Objects.requireNonNull(rdf);
    }
    @Override
    public ParserSource source() {
        return Objects.requireNonNull(source);
    }
    @Override
    public ParserTarget target() {
        return Objects.requireNonNull(target);
    }
    @Override
    public Optional<RDFSyntax> syntax() {
        return Optional.ofNullable(syntax);
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
    public ParseJob withRDF(RDF rdf) {
        this.rdf = rdf;
        return this;
    }
    @Override
    public ParseJob withSource(ParserSource s) {
        this.source = s;
        return this;
    }
    @Override
    public ParseJob withTarget(ParserTarget t) {
        this.target = t;
        return this;
    }
    @Override
    public ParseJob withSyntax(RDFSyntax s) {
        this.syntax = s;
        return this;
    }
    @Override
    public <O> ParseJob withOption(Option<O> o, O v) {
        options.put(o, v);
        return this;
    }
    @Override
    public ParseJob withImplementation(ParserImplementation impl) {
        this.impl = impl;
        return this;
    }
}


@SuppressWarnings("rawtypes")
abstract class ImmutableParseJobImpl implements ImmutableParseJob {
    @Override
    public ParseJob withSource(ParserSource src) {
        return new WithSource(src, this);
    }
    @Override
    public ParseJob withSyntax(RDFSyntax s) {
        return new WithSyntax(s, this);
    }
    @Override
    public ParseJob withTarget(ParserTarget t) {
        return new WithTarget(t, this);
    }
    public ParseJob withRDF(RDF rdf) {
        return new WithRDF(rdf, this);
    };
    public <O> ParseJob withOption(Option<O> o, O v) {
        return new WithOption(o, v, this);
    };
    @Override
    public ParseJob withImplementation(ParserImplementation impl) {
        return new WithImplementation(impl, this);
    }
    @Override
    public ImmutableParseJob freeze() {
        return this;
    }
}

@SuppressWarnings("rawtypes")
final class DefaultParseJob extends ImmutableParseJobImpl implements ImmutableParseJob {
    @Override
    public ParserSource source() {
        throw new IllegalStateException("Source not set");
    }
    @Override
    public ParserTarget target() {
        throw new IllegalStateException("Target not set");
    }
    @Override
    public Optional<RDFSyntax> syntax() {
        return Optional.empty();
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
final class FrozenParseJob extends ImmutableParseJobImpl implements ImmutableParseJob {
    private final ParserImplementation impl;
    private final ParserSource source;
    private final Optional<RDFSyntax> syntax;
    private final ParserTarget target;
    private final RDF rdf;
    private final Map<Option, Object> options;

    public FrozenParseJob(ParserImplementation impl, ParserSource source, RDFSyntax syntax, 
                             ParserTarget target, RDF rdf, Map<Option, Object> options) {
        this.impl = Objects.requireNonNull(impl);
        // null -> Optional.empty() 
        this.syntax = Optional.ofNullable(syntax);
        // fields may be null (not yet set)
        this.source =  source;
        this.target = target;
        this.rdf = rdf;
        // shallow copy of options (can't be null)
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
    public ParserSource source() {
        return Objects.requireNonNull(source, "source not set");
    }
    @Override
    public ParserTarget target() {
        return Objects.requireNonNull(target, "target not set");
    }
    @Override
    public Optional<RDFSyntax> syntax() {
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
abstract class Inherited extends ImmutableParseJobImpl implements ImmutableParseJob  {
    private final ImmutableParseJob parent;
    public Inherited() {
        this(new DefaultParseJob());
    }
    public Inherited(ImmutableParseJob job) {
        parent = job;
    }
    @Override
    public ParserSource source() {
        return parent.source();
    }
    @Override
    public ParserTarget target() {
        return parent.target();
    }
    @Override
    public Optional<RDFSyntax> syntax() {
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
    private final ParserSource source;

    public WithSource(final ParserSource src) {
        this.source = src;
    }
    public WithSource(final ParserSource src, final ImmutableParseJob parent) {
        super(parent);        
        this.source = src;
    }
    @Override
    public ParserSource source() {
        return source;
    }
}

@SuppressWarnings("rawtypes")
final class WithTarget extends Inherited {
    private final ParserTarget target;

    public WithTarget(final ParserTarget t) {
        this.target = t;
    }
    public WithTarget(final ParserTarget t, final ImmutableParseJob parent) {
        super(parent);
        this.target = t;
    }
    @Override
    public ParserTarget target() {
        return target;
    }
}

final class WithSyntax extends Inherited {
    private final RDFSyntax syntax;
    public WithSyntax(final RDFSyntax s) {
        syntax = s;
    }
    public WithSyntax(final RDFSyntax s, final ImmutableParseJob parent) {
        super(parent);
        syntax = s;
    }
    @Override
    public Optional<RDFSyntax> syntax() {
        return Optional.ofNullable(syntax);
    }
}

final class WithRDF extends Inherited {
    private final RDF rdf;
    public WithRDF(final RDF r) {
        rdf = r;
    }
    public WithRDF(final RDF r, final ImmutableParseJob parent) {
        super(parent);
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
    public <O> WithOption(Option<O> o, O v, ImmutableParseJob parent) {
        super(parent);
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
    public WithImplementation(ParserImplementation impl, ImmutableParseJob parent) {
        super(parent);
        this.impl = impl;
    }
    @Override
    public ParserImplementation impl() {
        return impl;
    }
}
