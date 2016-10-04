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
package org.apache.commons.rdf.rdf4j;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collections;
import java.util.Set;

import org.apache.commons.rdf.api.AbstractGraphTest;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.junit.AfterClass;
import org.junit.BeforeClass;


/**
 * Test a graph within a file-based RDF4J {@link SailRepository}.
 * <p>
 * Note that for efficiency reasons this test uses a shared repository for all
 * tests, but uses a different BlankNode context for each 
 * {@link NativeStoreFactory#createGraph()}.
 * <p>
 * TIP: If the {@link #shutdownAndDelete()} take about 20 seconds
 * this is a hint that a {@link RepositoryConnection} or 
 * {@link RepositoryResult} was not closed correctly.
 * 
 */
public class NativeStoreGraphTest extends AbstractGraphTest {

	public final class NativeStoreFactory implements RDFTermFactory {

		RDF4JTermFactory rdf4jFactory = new RDF4JTermFactory(repository.getValueFactory());

		@Override
		public RDF4JGraph createGraph() {
			// We re-use the repository connection, but use a different context every time
			Set<RDF4JBlankNode> context = Collections.singleton(rdf4jFactory.createBlankNode());
			return rdf4jFactory.asRDFTermGraph(repository, context);
		}

		// Delegate methods 
		public RDF4JBlankNode createBlankNode() {
			return rdf4jFactory.createBlankNode();
		}
		public RDF4JBlankNode createBlankNode(String name) {
			return rdf4jFactory.createBlankNode(name);
		}
		public RDF4JIRI createIRI(String iri) throws IllegalArgumentException, UnsupportedOperationException {
			return rdf4jFactory.createIRI(iri);
		}
		public RDF4JLiteral createLiteral(String lexicalForm) {
			return rdf4jFactory.createLiteral(lexicalForm);
		}
		public Literal createLiteral(String lexicalForm, IRI dataType) {
			return rdf4jFactory.createLiteral(lexicalForm, dataType);
		}
		public Literal createLiteral(String lexicalForm, String languageTag) {
			return rdf4jFactory.createLiteral(lexicalForm, languageTag);
		}
		public RDF4JTriple createTriple(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
			return rdf4jFactory.createTriple(subject, predicate, object);
		}
	}

	private static SailRepository repository;
	private static Path tempDir;
	
	@BeforeClass
	public static void createRepository() {
		try {
			tempDir = Files.createTempDirectory("test-commonsrdf-rdf4j");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Sail sail = new NativeStore(tempDir.toFile());
		repository = new SailRepository(sail);
		repository.initialize();
	}

	@AfterClass
	public static void shutdownAndDelete() throws IOException {
		// must shutdown before we delete
		if (repository != null) {
			System.out.println("Shutting down rdf4j repository " + repository);
			repository.shutDown();
			System.out.println("rdf4j repository shut down.");
		}
		if (tempDir != null) {
			deleteAll(tempDir);
		}
	}
	
	private static void deleteAll(Path dir) throws IOException {
		Files.walkFileTree(dir, new SimpleFileVisitor<Path>(){
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}
			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				FileVisitResult r = super.postVisitDirectory(dir, exc);
				Files.delete(dir);
				return r;
			}
		});
	}

	@Override
	public RDFTermFactory createFactory() {
		return new NativeStoreFactory();
	}

}
