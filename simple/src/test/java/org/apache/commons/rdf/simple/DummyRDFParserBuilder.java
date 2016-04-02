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

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFParserBuilder;
import org.apache.commons.rdf.api.RDFTermFactory;

/** 
 * For test purposes - a {@link RDFParserBuilder} that always insert a single triple.
 * <p>
 * This dummy RDF parser always sleeps for at least 1000 ms
 * before inserting the triple:
 * <pre>
 *    <http://example.com/test1> <http://example.com/greeting> "Hello world" .
 * </pre>
 *
 */
public class DummyRDFParserBuilder extends AbstractRDFParserBuilder {

	@Override
	protected void parseSynchronusly() throws IOException, IllegalStateException, ParseException {		
		// From parseSynchronusly both of these are always present
		RDFTermFactory factory = getRdfTermFactory().get();
		Graph graph = getIntoGraph().get();
		
		// Let's always insert the same triple
		IRI test1 = factory.createIRI("http://example.com/test1");
		IRI greeting = factory.createIRI("http://example.com/greeting");
		Literal hello = factory.createLiteral("Hello world");
		try {
			// Pretend we take a while to parse
			Thread.sleep(1000);			
		} catch (InterruptedException e) {
			return;
		} 
		graph.add(test1, greeting, hello); 		
	}

}
