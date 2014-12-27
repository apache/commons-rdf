package com.github.commonsrdf.dummyimpl;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

import com.github.commonsrdf.api.BlankNode;
import com.github.commonsrdf.api.IRI;

public class TestWritingGraph {

	private GraphImpl graph;

	@Before
	public void createGraph() throws Exception {
		graph = new GraphImpl();
		BlankNode subject = new BlankNodeImpl("subj");
		IRI predicate = new IRIImpl("pred");
		// 200k triples should do
		for (int i=0; i<200000; i++) {
			graph.add(subject, predicate, 
					new LiteralImpl("Example " + i, "en"));		
		}		
	}
	
	@Test
	public void writeGraphFromStream() throws Exception {
		Path graphFile = Files.createTempFile("graph", ".nt");
		System.out.println("From stream: " + graphFile);		
		Stream<CharSequence> stream = graph.getTriples().unordered().parallel().
				map(t -> t.toString());
		Files.write(graphFile, 
				stream::iterator,
				Charset.forName("UTF-8"));
	}

	@Test
	public void writeGraphFromStreamFiltered() throws Exception {
		Path graphFile = Files.createTempFile("graph", ".nt");		
		System.out.println("Filtered stream: " + graphFile);
		BlankNode subject = new BlankNodeImpl("subj"); 
		IRI predicate = new IRIImpl("pred");
		Stream<CharSequence> stream = graph.getTriples(subject, predicate, null).
				map(t -> t.toString());
		Files.write(graphFile, 
				stream::iterator,
				Charset.forName("UTF-8"));
		
	}

}
 