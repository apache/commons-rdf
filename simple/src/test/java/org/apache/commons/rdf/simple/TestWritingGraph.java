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

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTermFactory;

public class TestWritingGraph {

	/*
	 * 200k triples should do - about 7 MB on disk. Override with
	 * -Dtriples=20000000 to exercise your memory banks.
	 */
	private static final int TRIPLES = Integer.getInteger("triples", 200000);

	/** Run tests with -Dkeepfiles=true to inspect /tmp files **/
	private static boolean KEEP_FILES = Boolean.getBoolean("keepfiles");

	private static Graph graph;

	private static RDFTermFactory factory;

	@BeforeClass
	public static void createGraph() throws Exception {
		factory = new SimpleRDFTermFactory();
		graph = factory.createGraph();
		BlankNode subject = factory.createBlankNode("subj");
		IRI predicate = factory.createIRI("pred");
		List<IRI> types = new ArrayList<>(Types.values());
		// Ensure we don't try to create a literal with rdf:langString but
		// without a language tag
		types.remove(Types.RDF_LANGSTRING);
		Collections.shuffle(types);
		for (int i = 0; i < TRIPLES; i++) {
			if (i % 5 == 0) {
				graph.add(subject, predicate,
						factory.createLiteral("Example " + i, "en"));
			} else if (i % 3 == 0) {
				graph.add(
						subject,
						predicate,
						factory.createLiteral("Example " + i,
								types.get(i % types.size())));
			} else {
				graph.add(subject, predicate,
						factory.createLiteral("Example " + i));
			}
		}
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
		graph.clear();
		graph = null;
	}

	@Test
	public void createGraphTiming() throws Exception {
		createGraph();
	}

	@Test
	public void countQuery() {
		BlankNode subject = factory.createBlankNode("subj");
		IRI predicate = factory.createIRI("pred");
		long count = graph.getTriples(subject, predicate, null).unordered()
				.parallel().count();
		System.out.println("Counted - " + count);
	}

	@Test
	public void writeGraphFromStream() throws Exception {
		Path graphFile = Files.createTempFile("graph", ".nt");
		if (KEEP_FILES) {
			System.out.println("From stream: " + graphFile);
		} else {
			graphFile.toFile().deleteOnExit();
		}

		Stream<CharSequence> stream = graph.getTriples().unordered()
				.map(Object::toString);
		Files.write(graphFile, stream::iterator, Charset.forName("UTF-8"));
	}

	@Test
	public void writeGraphFromStreamFiltered() throws Exception {
		Path graphFile = Files.createTempFile("graph", ".nt");
		if (KEEP_FILES) {
			System.out.println("Filtered stream: " + graphFile);
		} else {
			graphFile.toFile().deleteOnExit();
		}

		BlankNode subject = factory.createBlankNode("subj");
		IRI predicate = factory.createIRI("pred");
		Stream<CharSequence> stream = graph
				.getTriples(subject, predicate, null).map(Object::toString);
		Files.write(graphFile, stream::iterator, Charset.forName("UTF-8"));

	}

}
