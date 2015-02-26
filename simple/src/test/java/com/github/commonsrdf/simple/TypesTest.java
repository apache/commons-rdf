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

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link Types} enumeration.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class TypesTest {

	/**
	 * Test method for {@link com.github.commonsrdf.simple.Types#getIRIString()}
	 * .
	 */
	@Test
	public final void testGetIRIString() {
		assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString",
				Types.RDF_LANGSTRING.getIRIString());
	}

	/**
	 * Test method for
	 * {@link com.github.commonsrdf.simple.Types#ntriplesString()}.
	 */
	@Test
	public final void testNtriplesString() {
		assertEquals("<http://www.w3.org/1999/02/22-rdf-syntax-ns#langString>",
				Types.RDF_LANGSTRING.ntriplesString());
	}

	/**
	 * Test method for
	 * {@link com.github.commonsrdf.simple.Types#get(com.github.commonsrdf.api.IRI)}
	 * .
	 */
	@Test
	public final void testGet() {
		assertTrue(Types.get(
				new IRIImpl("http://www.w3.org/2001/XMLSchema#boolean"))
				.isPresent());
		assertEquals(
				"http://www.w3.org/2001/XMLSchema#boolean",
				Types.get(
						new IRIImpl("http://www.w3.org/2001/XMLSchema#boolean"))
						.get().getIRIString());
		assertFalse(Types.get(
				new IRIImpl("http://www.w3.org/2001/XMLSchema#nonExistent"))
				.isPresent());
	}

}
