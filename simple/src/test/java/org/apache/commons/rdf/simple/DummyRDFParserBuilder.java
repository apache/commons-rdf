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
package org.apache.commons.rdf.simple;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.experimental.RDFParser;
import org.apache.commons.rdf.simple.experimental.AbstractRDFParser;
import org.apache.commons.rdf.simple.experimental.RDFParseException;

/**
 * For test purposes - a {@link RDFParser} that inserts information about what
 * it has been asked to parse instead of actually parsing anything.
 * <p>
 * This always insert at least the triple equivalent to:
 * 
 * <pre>
 *    &lt;urn:uuid:b7ac3fcc-4d86-4d28-8358-a1cd094974a6&gt; &lt;http://example.com/greeting&gt; "Hello world" .
 * </pre>
 * 
 * Additional triples match the corresponding getter in AbstractRDFParser, e.g.:
 * 
 * <pre>
 *   &lt;urn:uuid:b7ac3fcc-4d86-4d28-8358-a1cd094974a6&gt; &lt;http://example.com/base&gt; &lt;http://www.example.org/&gt; .
 *   &lt;urn:uuid:b7ac3fcc-4d86-4d28-8358-a1cd094974a6&gt; &lt;http://example.com/sourceFile&gt; "/tmp/file.ttl" .
 * </pre>
 * 
 *
 */
public class DummyRDFParserBuilder extends AbstractRDFParser<DummyRDFParserBuilder> {

    @Override
    protected void parseSynchronusly() throws IOException, IllegalStateException, RDFParseException {
        // From parseSynchronusly both of these are always present
        final RDF factory = getRdfTermFactory().get();
        final Consumer<Quad> t = getTarget();

        // well - each parsing is unique. This should hopefully
        // catch any accidental double parsing
        final IRI parsing = factory.createIRI("urn:uuid:" + UUID.randomUUID());
        t.accept(factory.createQuad(null, parsing, factory.createIRI("http://example.com/greeting"),
                factory.createLiteral("Hello world")));

        // Now we'll expose the finalized AbstractRDFParser settings
        // so they can be inspected by the junit test

        if (getSourceIri().isPresent()) {
            t.accept(factory.createQuad(null, parsing, factory.createIRI("http://example.com/source"),
                    getSourceIri().get()));
        }
        if (getSourceFile().isPresent()) {
            t.accept(factory.createQuad(null, parsing, factory.createIRI("http://example.com/source"),
                    factory.createIRI(getSourceFile().get().toUri().toString())));
        }
        if (getSourceInputStream().isPresent()) {
            t.accept(factory.createQuad(null, parsing, factory.createIRI("http://example.com/source"),
                    factory.createBlankNode()));
        }

        if (getBase().isPresent()) {
            t.accept(factory.createQuad(null, parsing, factory.createIRI("http://example.com/base"), getBase().get()));
        }
        if (getContentType().isPresent()) {
            t.accept(factory.createQuad(null, parsing, factory.createIRI("http://example.com/contentType"),
                    factory.createLiteral(getContentType().get())));
        }
        if (getContentTypeSyntax().isPresent()) {
            t.accept(factory.createQuad(null, parsing, factory.createIRI("http://example.com/contentTypeSyntax"),
                    factory.createLiteral(getContentTypeSyntax().get().getname())));
        }
    }

}
