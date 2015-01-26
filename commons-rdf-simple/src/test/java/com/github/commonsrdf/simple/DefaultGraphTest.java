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

import com.github.commonsrdf.api.AbstractGraphTest;
import com.github.commonsrdf.api.Graph;
import com.github.commonsrdf.api.IRI;
import com.github.commonsrdf.api.RDFTermFactory;

/**
 * Ensure AbstractGraphTest does not crash if the RDFTermFactory throws
 * UnsupportedOperationException
 *
 */

public class DefaultGraphTest extends AbstractGraphTest {

	@Override
	public RDFTermFactory createFactory() {
		// The most minimal RDFTermFactory that would still
		// make sense with a Graph
		return new RDFTermFactory() {
			@Override
			public Graph createGraph() throws UnsupportedOperationException {
				return new GraphImpl();
			}

			@Override
			public IRI createIRI(String iri)
					throws UnsupportedOperationException,
					IllegalArgumentException {
				return new IRIImpl(iri);
			}
		};
	}

}
