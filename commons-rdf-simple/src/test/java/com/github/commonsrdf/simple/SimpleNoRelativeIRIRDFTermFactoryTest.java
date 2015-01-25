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

import java.net.URI;

import com.github.commonsrdf.api.AbstractRDFTermFactoryTest;
import com.github.commonsrdf.api.IRI;
import com.github.commonsrdf.api.RDFTermFactory;

/**
 * Test simple IRI without relative IRI support.
 * <p?>
 * Ensures that {@link AbstractRDFTermFactoryTest#createIRIRelative()} is
 * correctly skipped (without causing an error.
 *
 */
public class SimpleNoRelativeIRIRDFTermFactoryTest extends
		AbstractRDFTermFactoryTest {
	@Override
	public RDFTermFactory createFactory() {
		return new SimpleRDFTermFactory() {
			@Override
			public IRI createIRI(String iri) {
				if (!URI.create(iri).isAbsolute()) {
					throw new IllegalArgumentException("IRIs must be absolute");
					// ..in this subclass for testing purposes only :)
				}
				return super.createIRI(iri);
			}
		};
	}
}
