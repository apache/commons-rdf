package org.apache.commons.rdf.simple.experimental;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.concurrent.Future;

import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.experimental.ParserFactory;
import org.apache.commons.rdf.experimental.ParserFactory.*;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.junit.Test;

public class ParserFactoryTest {
    @Test
    public void testName() throws Exception {
        ParserFactory f = new ParserFactoryImpl((a,b,c,d,e) -> 1);
        InputStream is = getClass().getResourceAsStream("Fred");
        Async g = f.syntax(RDFSyntax.JSONLD).rdf(new SimpleRDF()).base("http://example.com/").source(is).async();
        Future<Parsed<?,?>> x = g.parseAsync();
        Parsed<?, ?> p = x.get();
        System.out.println(p.count());
    }
}
