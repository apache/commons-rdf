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
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.rdf.api.AbstractGraphTest;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.Sail;
import org.eclipse.rdf4j.sail.memory.model.MemValueFactory;
import org.eclipse.rdf4j.sail.nativerdf.NativeStore;


public class NativeStoreGraphTest extends AbstractGraphTest {

	public static final class NativeStoreFactory extends RDF4JTermFactory {
		NativeStoreFactory() {
			super(new MemValueFactory());
		}
		@Override
		public RDF4JGraph createGraph() {
			return asRDFTermGraph(createRepository());
		}
	}

	public static Repository createRepository() {
		Path tempDir;
		try {
			tempDir = Files.createTempDirectory("test-commonsrdf-rdf4j");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Sail sail = new NativeStore(tempDir.toFile());
		SailRepository repository = new SailRepository(sail);
		repository.initialize();
		return repository;
	}
	
	@Override
	public RDFTermFactory createFactory() {
		// FIXME: This valueFactory will be backed by the wrong temporary folder!
		ValueFactory valueFactory = createRepository().getValueFactory();
		return new RDF4JTermFactory(valueFactory);
	}

}
