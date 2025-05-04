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

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.function.Uncheck;
import org.apache.commons.rdf.api.AbstractGraphTest;
import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFTerm;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.io.TempDir;

/**
 * Test a graph within a file-based RDF4J {@link SailRepository}.
 * <p>
 * TIP: If the {@link #shutdownAndDelete()} take about 20 seconds this is a hint
 * that a {@link RepositoryConnection} or {@link RepositoryResult} was not
 * closed correctly.
 * <p>
 * A timeout of more than 15 seconds pr test indicates typically that
 * shutdownAndDelete failed.
 */
@Timeout(value = 15, unit = TimeUnit.SECONDS)
public class NativeStoreGraphTest extends AbstractGraphTest {

    public final class NativeStoreRDF implements RDF {

        RDF4J rdf4jFactory = new RDF4J(getRepository().getValueFactory());

        // Delegate methods
        @Override
        public RDF4JBlankNode createBlankNode() {
            return rdf4jFactory.createBlankNode();
        }

        @Override
        public RDF4JBlankNode createBlankNode(final String name) {
            return rdf4jFactory.createBlankNode(name);
        }

        @Override
        public Dataset createDataset() {
            throw new UnsupportedOperationException("Can't create more than one Dataset in this test");
            // ...as the below would re-use the same repository:
            // return rdf4jFactory.asRDFTermDataset(getRepository());
        }

        @Override
        public RDF4JGraph createGraph() {
            // We re-use the repository connection, but use a different context
            // every time
            final Set<RDF4JBlankNode> context = Collections.singleton(rdf4jFactory.createBlankNode());
            return rdf4jFactory.asGraph(getRepository(), context);
        }

        @Override
        public RDF4JIRI createIRI(final String iri) throws IllegalArgumentException, UnsupportedOperationException {
            return rdf4jFactory.createIRI(iri);
        }

        @Override
        public RDF4JLiteral createLiteral(final String lexicalForm) {
            return rdf4jFactory.createLiteral(lexicalForm);
        }

        @Override
        public Literal createLiteral(final String lexicalForm, final IRI dataType) {
            return rdf4jFactory.createLiteral(lexicalForm, dataType);
        }

        @Override
        public Literal createLiteral(final String lexicalForm, final String languageTag) {
            return rdf4jFactory.createLiteral(lexicalForm, languageTag);
        }

        @Override
        public Quad createQuad(final BlankNodeOrIRI graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object)
                throws IllegalArgumentException {
            return rdf4jFactory.createQuad(graphName, subject, predicate, object);
        }

        @Override
        public RDF4JTriple createTriple(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
            return rdf4jFactory.createTriple(subject, predicate, object);
        }

    }

    @TempDir
    public File tempDir;

    private SailRepository repository;

    @Override
    public RDF createFactory() {
        return new NativeStoreRDF();
    }

    public void createRepository() throws IOException {
        final Sail sail = new NativeStore(newFolder(tempDir, "junit"));
        repository = new SailRepository(sail);
        repository.init();
    }

    public synchronized SailRepository getRepository() {
        if (repository == null) {
            Uncheck.run(this::createRepository);
        }
        return repository;
    }

    // private static void deleteAll(Path dir) throws IOException {
    // Files.walkFileTree(dir, new SimpleFileVisitor<Path>(){
    // @Override
    // public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
    // throws IOException {
    // Files.delete(file);
    // return FileVisitResult.CONTINUE;
    // }
    // @Override
    // public FileVisitResult postVisitDirectory(Path dir, IOException exc)
    // throws IOException {
    // FileVisitResult r = super.postVisitDirectory(dir, exc);
    // Files.delete(dir);
    // return r;
    // }
    // });
    // }

    @AfterEach
    public void shutdownAndDelete() {
        // must shutdown before we delete
        if (repository != null) {
            System.out.print("Shutting down rdf4j repository " + repository + "...");
            // NOTE:
            // If this takes about 20 seconds it means the code forgot to close
            // a
            // RepositoryConnection or RespositoryResult
            // e.g. missing a try-with-resources block
            repository.shutDown();
            System.out.println("OK");
        }
    }

    private static File newFolder(File root, String... subDirs) throws IOException {
        String subFolder = String.join("/", subDirs);
        File result = new File(root, subFolder);
        if (!result.mkdirs()) {
            throw new IOException("Couldn't create folders " + root);
        }
        return result;
    }

}
