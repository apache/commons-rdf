package org.apache.commons.rdf.experimental;

import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDFSyntax;
import org.slf4j.impl.SimpleLoggerFactory;

interface State { 
    Path source();
    Graph target();
    RDFSyntax syntax();
    State withSource(Path p);
    State withTarget(Graph g);
    State withSyntax(RDFSyntax s);
}

abstract class AbstractState implements State { 
    @Override
    public State withSource(Path p) { 
       return new WithSource(p, this); 
    }
    @Override
    public State withSyntax(RDFSyntax s) {
        return new WithSyntax(s, this);
    }
    @Override
    public State withTarget(Graph g) {
        return new WithTarget(g, this);
    }
    
}

final class DefaultState extends AbstractState {
    @Override
    public Path source() {
        throw new IllegalStateException("Source not set");
    }
    @Override
    public Graph target() {
        throw new IllegalStateException("Target not set");
    }
    @Override
    public RDFSyntax syntax() {
        throw new IllegalStateException("Syntax not set");
    }
}

abstract class Inherited extends AbstractState  {
    private final State parent;
    public Inherited() {
        this(new DefaultState());
    }
    public Inherited(State state) {
        parent = state;
    }    
    @Override
    public Path source() {
        return parent.source();
    }
    @Override
    public Graph target() {
        return parent.target();
    }
    @Override
    public RDFSyntax syntax() {
        return parent.syntax();
    }
    
    
}

final class WithSource extends Inherited {    
    private final Path source;   
    public WithSource(final Path path) {
        this.source = path;
    }
    public WithSource(final Path path, final State state) {
        super(state);
        this.source = path;
    }    
    @Override
    public Path source() {
        return source;
    }
}


final class WithTarget extends Inherited {    
    private final Graph target;   
    public WithTarget(final Graph g) {
        this.target = g;
    }
    public WithTarget(final Graph g, final State state) {
        super(state);
        this.target = g;
    }    
    @Override
    public Graph target() {
        return target;
    }
}

final class WithSyntax extends Inherited {    
    private final RDFSyntax syntax;   
    public WithSyntax(final RDFSyntax s) {
        syntax = s;
    }
    public WithSyntax(final RDFSyntax s, final State state) {
        super(state);
        syntax = s;
    }    
    @Override
    public RDFSyntax syntax() {
        return syntax;
    }
}

public class Test {
    @org.junit.Test
    public void testName() throws Exception {
        Path p = Paths.get("/tmp/f.txt");
        Graph g = null;
        State s = new DefaultState().withSource(p).withTarget(g).withSyntax(RDFSyntax.JSONLD);
        
    }
}
