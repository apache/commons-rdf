/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rdf.rdf4j.experimental;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.junit.Test;

public class RDF4JParserTest {
    RDF4J rdf = new RDF4J();

    @Test
    public void parseJsonLd() throws Exception {
        IRI base = rdf.createIRI("http://example.com/ex.jsonld");
        IRI name = rdf.createIRI("http://schema.org/name");
        IRI license = rdf.createIRI("http://purl.org/dc/terms/license");
        IRI rights = rdf.createIRI("http://purl.org/dc/terms/rights");
        IRI apacheLicense = rdf.createIRI("http://www.apache.org/licenses/LICENSE-2.0");
        IRI exampleLicense = rdf.createIRI("http://example.com/LICENSE");

        Dataset ds = new RDF4JParserFactory().syntax(RDFSyntax.JSONLD).rdf(rdf).base(base)
                .source(getClass().getResourceAsStream("/example-rdf/example.jsonld")).parse().dest().dest();

        // default graph
        try (Stream<? extends Quad> s = ds.stream()) {
            s.forEach(System.out::println);
        }
        System.out.println();
        assertTrue(ds.getGraph().contains(base, name, rdf.createLiteral("JSON-LD example")));
        assertTrue(ds.getGraph().contains(base, rights, null));
        assertTrue(ds.getGraph().contains(base, license, apacheLicense));
        // no leak from named graph
        assertFalse(ds.getGraph().contains(base, license, exampleLicense));

        // Let's find the bnode in the default graph
        BlankNodeOrIRI bnode = firstSubject(ds, name, rdf.createLiteral("Same BNode"));
        assertTrue(bnode instanceof BlankNode);
        // the bnode should be the same inside the graph with the same bnode
        assertTrue(ds.contains(Optional.of(bnode), bnode, name, rdf.createLiteral("Graph with different license")));
    }

    private BlankNodeOrIRI firstSubject(Dataset ds, IRI pred, RDFTerm obj) {
        try (Stream<? extends Quad> s = ds.stream(Optional.empty(), null, pred, obj)) {
            return s.findAny().get().getSubject();
        }

    }
}
