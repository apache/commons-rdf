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
import java.text.ParseException;
import java.util.UUID;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFParserBuilder;
import org.apache.commons.rdf.api.RDFTermFactory;

/** 
 * For test purposes - a {@link RDFParserBuilder} that inserts information
 * about what it has been asked to parse instead of actually parsing anything.
 * <p>
 * This always insert at least the triple equivalent to:
 * <pre>
 *    <urn:uuid:b7ac3fcc-4d86-4d28-8358-a1cd094974a6> <http://example.com/greeting> "Hello world" .
 * </pre>
 * Additional triples match the corresponding getter in AbstractRDFParserBuilder,
 * e.g.:
 * <pre>
 *   <urn:uuid:b7ac3fcc-4d86-4d28-8358-a1cd094974a6> <http://example.com/base> <http://www.example.org/> .
 *   <urn:uuid:b7ac3fcc-4d86-4d28-8358-a1cd094974a6> <http://example.com/sourceFile> "/tmp/file.ttl" .   
 * </pre> 
 * 
 *
 */
public class DummyRDFParserBuilder extends AbstractRDFParserBuilder {
	
	@Override
	protected void parseSynchronusly() throws IOException, IllegalStateException, RDFParseException {		
		// From parseSynchronusly both of these are always present
		RDFTermFactory factory = getRdfTermFactory().get();
		Graph graph = getIntoGraph().get();
				
		// well - each parsing is unique. This should hopefully
		// catch any accidental double parsing
		IRI parsing = factory.createIRI("urn:uuid:" + UUID.randomUUID());
		graph.add(parsing, factory.createIRI("http://example.com/greeting"), 
				factory.createLiteral("Hello world"));
		
		// Now we'll expose the finalized AbstractRDFParserBuilder settings
		// so they can be inspected by the junit test

		if (getSourceIri().isPresent()) {
			graph.add(parsing, 
					factory.createIRI("http://example.com/source"),
					getSourceIri().get());			
		}		
		if (getSourceFile().isPresent()) {
			graph.add(parsing, 
					factory.createIRI("http://example.com/source"),
					factory.createIRI(getSourceFile().get().toUri().toString()));
		}
		if (getSourceInputStream().isPresent()) { 
			graph.add(parsing, 
					factory.createIRI("http://example.com/source"),
					factory.createBlankNode());
		}

		if (getBase().isPresent()) { 
			graph.add(parsing, 
					factory.createIRI("http://example.com/base"),
					getBase().get());
		}
		if (getContentType().isPresent()) {
			graph.add(parsing, 
					factory.createIRI("http://example.com/contentType"),
					factory.createLiteral(getContentType().get()));
		}
		if (getContentTypeSyntax().isPresent()) {
			graph.add(parsing, 
					factory.createIRI("http://example.com/contentTypeSyntax"),
					factory.createLiteral(getContentTypeSyntax().get().name()));
		}		
	}

}
