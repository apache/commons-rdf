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
package org.apache.commons.rdf.jsonldjava;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFSyntax;
import org.junit.Test;

public class JsonLdParserBuilderTest {
	@Test
	public void parseByUrl() throws Exception {
		URL url = getClass().getResource("/test.jsonld");
		IRI iri = new JsonLdRDFTermFactory().createIRI(url.toString());
		Graph g = new JsonLdParserBuilder().contentType(RDFSyntax.JSONLD).source(iri).parse().get(10, TimeUnit.SECONDS);
		System.out.println(g);
	}
}
