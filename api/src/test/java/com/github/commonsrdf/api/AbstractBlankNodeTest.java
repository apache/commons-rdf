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
package com.github.commonsrdf.api;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Abstract test class for the BlankNode interface.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public abstract class AbstractBlankNodeTest {

	protected abstract BlankNode getBlankNode();

	/**
	 * Gets a new blank node object based on the given identifier.
	 * <p>
	 * Subsequent calls to this method during a single test with the same
	 * identifier must return BlankNode objects that are equals and have the
	 * same hashCode. The objects returned from successive calls during a single
	 * test may be the same object, or they may be different objects.
	 * </p>
	 * 
	 * @param identifier
	 *            The identifier to use as the reference for creating the blank
	 *            node that is returned.
	 * @return A new blank node based on the
	 */
	protected abstract BlankNode getBlankNode(String identifier);

	/**
	 * Test method for
	 * {@link com.github.commonsrdf.api.BlankNode#internalIdentifier()}.
	 */
	@Test
	public final void testInternalIdentifier() {
		BlankNode testNull = new BlankNode() {
			@Override
			public String ntriplesString() {
				return null;
			}

			@Override
			public String internalIdentifier() {
				return null;
			}
		};
		BlankNode testAutomatic1 = getBlankNode();
		BlankNode testAutomatic2 = getBlankNode();
		BlankNode testManual3a = getBlankNode("3");
		BlankNode testManual3b = getBlankNode("3");
		BlankNode testManual4 = getBlankNode("4");

		// Test against our fake stub
		assertNotEquals(testNull.internalIdentifier(),
				testAutomatic1.internalIdentifier());
		assertNotEquals(testAutomatic1.internalIdentifier(),
				testNull.internalIdentifier());
		assertNotEquals(testNull.internalIdentifier(),
				testManual3a.internalIdentifier());
		assertNotEquals(testManual3a.internalIdentifier(),
				testNull.internalIdentifier());

		// Test the two imported instances against each other
		assertEquals(testAutomatic1.internalIdentifier(),
				testAutomatic1.internalIdentifier());
		assertEquals(testAutomatic2.internalIdentifier(),
				testAutomatic2.internalIdentifier());
		assertNotEquals(testAutomatic1.internalIdentifier(),
				testAutomatic2.internalIdentifier());
		assertNotEquals(testAutomatic2.internalIdentifier(),
				testAutomatic1.internalIdentifier());
		assertNotEquals(testAutomatic1.internalIdentifier(),
				testManual3a.internalIdentifier());
		assertEquals(testManual3b.internalIdentifier(),
				testManual3a.internalIdentifier());
		assertNotEquals(testManual3a.internalIdentifier(),
				testManual4.internalIdentifier());
	}

	/**
	 * Test method for
	 * {@link com.github.commonsrdf.api.BlankNode#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEquals() {
		BlankNode testNull = new BlankNode() {
			@Override
			public String ntriplesString() {
				return null;
			}

			@Override
			public String internalIdentifier() {
				return null;
			}
		};
		BlankNode testAutomatic1 = getBlankNode();
		BlankNode testAutomatic2 = getBlankNode();
		BlankNode testManual3a = getBlankNode("3");
		BlankNode testManual3b = getBlankNode("3");
		BlankNode testManual4 = getBlankNode("4");

		// Test against our fake stub
		assertNotEquals(testNull, testAutomatic1);
		assertNotEquals(testAutomatic1, testNull);
		assertNotEquals(testNull, testManual3a);
		assertNotEquals(testManual3a, testNull);

		// Test the two imported instances against each other
		assertEquals(testAutomatic1, testAutomatic1);
		assertEquals(testAutomatic2, testAutomatic2);
		assertNotEquals(testAutomatic1, testAutomatic2);
		assertNotEquals(testAutomatic2, testAutomatic1);
		assertNotEquals(testAutomatic1, testManual3a);
		assertEquals(testManual3b, testManual3a);
		assertNotEquals(testManual3a, testManual4);
	}

	/**
	 * Test method for {@link com.github.commonsrdf.api.BlankNode#hashCode()}.
	 */
	@Test
	public final void testHashCode() {
		BlankNode testNull = new BlankNode() {
			@Override
			public String ntriplesString() {
				return null;
			}

			@Override
			public String internalIdentifier() {
				return null;
			}
		};
		BlankNode testAutomatic1 = getBlankNode();
		BlankNode testAutomatic2 = getBlankNode();
		BlankNode testManual3a = getBlankNode("3");
		BlankNode testManual3b = getBlankNode("3");
		BlankNode testManual4 = getBlankNode("4");

		// Test against our fake stub
		assertNotEquals(testNull.hashCode(), testAutomatic1.hashCode());
		assertNotEquals(testAutomatic1.hashCode(), testNull.hashCode());
		assertNotEquals(testNull.hashCode(), testManual3a.hashCode());
		assertNotEquals(testManual3a.hashCode(), testNull.hashCode());

		// Test the two imported instances against each other
		assertEquals(testAutomatic1.hashCode(), testAutomatic1.hashCode());
		assertEquals(testAutomatic2.hashCode(), testAutomatic2.hashCode());
		assertNotEquals(testAutomatic1.hashCode(), testAutomatic2.hashCode());
		assertNotEquals(testAutomatic2.hashCode(), testAutomatic1.hashCode());
		assertNotEquals(testAutomatic1.hashCode(), testManual3a.hashCode());
		assertEquals(testManual3b.hashCode(), testManual3a.hashCode());
		assertNotEquals(testManual3a.hashCode(), testManual4.hashCode());
	}

	/**
	 * Test method for
	 * {@link com.github.commonsrdf.api.RDFTerm#ntriplesString()}.
	 */
	@Test
	public final void testNtriplesString() {
		BlankNode testNull = new BlankNode() {
			@Override
			public String ntriplesString() {
				return null;
			}

			@Override
			public String internalIdentifier() {
				return null;
			}
		};
		BlankNode testAutomatic1 = getBlankNode();
		BlankNode testAutomatic2 = getBlankNode();
		BlankNode testManual3a = getBlankNode("3");
		BlankNode testManual3b = getBlankNode("3");
		BlankNode testManual4 = getBlankNode("4");

		// Test against our fake stub
		assertNotEquals(testNull.ntriplesString(),
				testAutomatic1.ntriplesString());
		assertNotEquals(testAutomatic1.ntriplesString(),
				testNull.ntriplesString());
		assertNotEquals(testNull.ntriplesString(),
				testManual3a.ntriplesString());
		assertNotEquals(testManual3a.ntriplesString(),
				testNull.ntriplesString());

		// Test the two imported instances against each other
		assertEquals(testAutomatic1.ntriplesString(),
				testAutomatic1.ntriplesString());
		assertEquals(testAutomatic2.ntriplesString(),
				testAutomatic2.ntriplesString());
		assertNotEquals(testAutomatic1.ntriplesString(),
				testAutomatic2.ntriplesString());
		assertNotEquals(testAutomatic2.ntriplesString(),
				testAutomatic1.ntriplesString());
		assertNotEquals(testAutomatic1.ntriplesString(),
				testManual3a.ntriplesString());
		assertEquals(testManual3b.ntriplesString(),
				testManual3a.ntriplesString());
		assertNotEquals(testManual3a.ntriplesString(),
				testManual4.ntriplesString());
	}

}
