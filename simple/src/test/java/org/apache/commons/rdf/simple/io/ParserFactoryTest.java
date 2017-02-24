package org.apache.commons.rdf.simple.io;

import java.io.InputStream;
import java.util.concurrent.Future;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.io.Async;
import org.apache.commons.rdf.api.io.OptionalTarget;
import org.apache.commons.rdf.api.io.Parsed;
import org.apache.commons.rdf.api.io.ParserFactory;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.junit.Test;

public class ParserFactoryTest {
    @Test
    public void testName() throws Exception {
        ParserFactory f = new ParserFactoryImpl((a,b,c,d,e) -> 1);
        InputStream is = getClass().getResourceAsStream("Fred");
        OptionalTarget<Dataset> p = f.rdf(new SimpleRDF());
        Dataset ds = p.source("hello").parse().target().target();
        
        
        Async<Dataset, InputStream> g = f.syntax(RDFSyntax.JSONLD).rdf(new SimpleRDF()).base("http://example.com/").source(is).async();
        
        Future<Parsed<Dataset, InputStream>> x = g.parseAsync();
        Parsed<Dataset, InputStream> p2 = x.get();    
        System.out.println(p2.count());
    }
}
