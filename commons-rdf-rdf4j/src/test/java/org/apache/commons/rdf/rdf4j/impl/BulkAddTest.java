package org.apache.commons.rdf.rdf4j.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.memory.MemoryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link RepositoryDatasetImpl#addAll(Iterable) & RepositoryGraphImpl#addAll(Iterable)}.
 */
class BulkAddTest {

	private RDF4J rdf4j;
	private RDFTerm literal1;
	private RDFTerm literal2;
	private RDFTerm literal3;
	private IRI subject;
	private IRI predicate1;
	private IRI predicate2;
	private IRI predicate3;

	@BeforeEach
	public void setup() {
		rdf4j = new RDF4J();		
		subject = rdf4j.createIRI("http://example.com/test1");
		predicate1 = rdf4j.createIRI("http://example.com/predicate1");
		predicate2 = rdf4j.createIRI("http://example.com/predicate2");
		predicate3 = rdf4j.createIRI("http://example.com/predicate3");
		literal1 = rdf4j.createLiteral("TestString1");
		literal2 = rdf4j.createLiteral("TestString2");
		literal3 = rdf4j.createLiteral("TestString3");
	}
	
	/**
	 * Tests {@link RepositoryDatasetImpl#addAll(Iterable)}.
	 * @throws Exception
	 */
	@Test
	void testRepositoryDatasetImplAddAll() throws Exception {
		IRI graphName = rdf4j.createIRI("http://example.com/g1");
		
		List<Quad> quads = new ArrayList<>();
		quads.add(rdf4j.createQuad(graphName, subject, predicate1, literal1));
		quads.add(rdf4j.createQuad(graphName, subject, predicate2, literal2));
		quads.add(rdf4j.createQuad(graphName, subject, predicate3, literal3));
		
        try(Dataset ds = createDataset()) {
        	ds.addAll(quads);
    		Graph g = ds.getGraph(graphName).orElseThrow(()->new IllegalStateException());
    		assertEquals(3, g.stream().count());
    		assertEquals(literal1, findTriple(g, subject, predicate1).getObject());
    		assertEquals(literal2, findTriple(g, subject, predicate2).getObject());
    		assertEquals(literal3, findTriple(g, subject, predicate3).getObject());
        }
	}

	/**
	 * Tests RepositoryGraphImpl#addAll(Iterable)}. 
	 */	
	@Test
	void testRepositoryGraphImplAddAll() {
		List<Triple> triples = new ArrayList<>();
		triples.add(rdf4j.createTriple(subject, predicate1, literal1));
		triples.add(rdf4j.createTriple(subject, predicate2, literal2));
		triples.add(rdf4j.createTriple(subject, predicate3, literal3));

		Dataset ds = createDataset();
		Graph g = ds.getGraph();		
		g.addAll(triples);		

		assertEquals(3, g.stream().count());
		assertEquals(literal1, findTriple(g, subject, predicate1).getObject());
		assertEquals(literal2, findTriple(g, subject, predicate2).getObject());
		assertEquals(literal3, findTriple(g, subject, predicate3).getObject());
	}

	private Dataset createDataset() {
        final Sail sail = new MemoryStore();
        final Repository repository = new SailRepository(sail);
        return new RepositoryDatasetImpl(repository, UUID.randomUUID(), true, false);		
	}

    private Triple findTriple(Graph g, IRI subject, IRI predicate1) {
		return g.stream(subject, predicate1, null).findFirst().orElseThrow(()->new IllegalStateException());
	}
}
