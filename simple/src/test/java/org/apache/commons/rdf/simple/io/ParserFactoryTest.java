package org.apache.commons.rdf.simple.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.fluentparser.Async;
import org.apache.commons.rdf.api.fluentparser.NeedSourceBased;
import org.apache.commons.rdf.api.fluentparser.Sync;
import org.apache.commons.rdf.api.io.Option;
import org.apache.commons.rdf.api.io.Parsed;
import org.apache.commons.rdf.api.io.ParserFactory;
import org.apache.commons.rdf.api.io.ParserSource;
import org.apache.commons.rdf.api.io.ParserTarget;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.junit.Test;

public class ParserFactoryTest implements ParserImplementation {
    @Test
    public void testName() throws Exception {
        SimpleRDF rdf = new SimpleRDF();
        Set<RDFSyntax> s = Collections.emptySet();
        ParserFactory f = new ParserFactoryImpl(rdf, this, s);
        // 
        NeedSourceBased<Graph> reusable = f.target(rdf.createGraph()).base("sd").build();
        // 
        Sync<Graph, Path> src1 = reusable.source(Paths.get("/tmp/file1.ttl"));
        Sync<Graph, Path> src2 = reusable.source(Paths.get("/tmp/file2.jsonld"));
        Parsed<Graph, Path> src1Parsed = src1.parse();
        Parsed<Graph, Path> src2Parsed = src2.parse();
        Graph g1 = src1Parsed.into().dest();
        Graph g2 = src2Parsed.into().dest();
        assertEquals(g1, g2);
        Path p1 = src1Parsed.from().src();
        Path p2 = src2Parsed.from().src();
        assertNotEquals(p1, p2);
                
                
        Dataset ds = f.source("hello").parse().into().dest();
        Async<Dataset, InputStream> g;
        try (InputStream is = getClass().getResourceAsStream("Fred")) {
            //f.base("fred").source(is).async().parseAsync().get().target();
            g = f.syntax(RDFSyntax.JSONLD).rdf(new SimpleRDF()).base("http://example.com/").source(is).async();
        }
        Future<Parsed<Dataset, InputStream>> x = g.parseAsync();
        Parsed<Dataset, InputStream> p3 = x.get();    
        System.out.println(p3.count());
    }

    @Override
    public long parse(ParserSource source, RDFSyntax rdfSyntax, ParserTarget target, RDF rdf,
            Map<Option, Object> options) {
        
        return 0;
    }
}
