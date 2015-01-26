/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.commonsrdf.simple;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.commonsrdf.api.BlankNode;
import com.github.commonsrdf.api.IRI;

public class TestWritingGraph {

	/*
	 * 200k triples should do - about 7 MB on disk. Override with
	 * -Dtriples=20000000 to exercise your memory banks.
	 */
	private static final int TRIPLES = Integer.getInteger("triples", 200000);

	/** Run tests with -Dkeepfiles=true to inspect /tmp files **/
	private static boolean KEEP_FILES = Boolean.getBoolean("keepfiles");

	private static GraphImpl graph;

	@BeforeClass
	public static void createGraph() throws Exception {
		graph = new GraphImpl();
		BlankNode subject = new BlankNodeImpl(Optional.of(graph), "subj");
		IRI predicate = new IRIImpl("pred");
		for (int i = 0; i < TRIPLES; i++) {
			graph.add(subject, predicate, new LiteralImpl("Example " + i, "en"));
		}
	}

	@Test
	public void createGraphTiming() throws Exception {
		createGraph();
	}
	
	@Test
	public void countQuery() {
		BlankNode subject = new BlankNodeImpl(Optional.of(graph), "subj");
		IRI predicate = new IRIImpl("pred");
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

		BlankNode subject = new BlankNodeImpl(Optional.of(graph), "subj");
		IRI predicate = new IRIImpl("pred");
		Stream<CharSequence> stream = graph
				.getTriples(subject, predicate, null).map(Object::toString);
		Files.write(graphFile, stream::iterator, Charset.forName("UTF-8"));

	}

}
