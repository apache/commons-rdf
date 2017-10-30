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
package org.apache.commons.rdf.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Abstract test class for the BlankNode interface.
 */
public abstract class AbstractBlankNodeTest {

    /**
     * This method must be overridden by the implementing test to create a
     * {@link BlankNode} to be tested.
     * <p>
     * Each call to this method must provide a new, unique BlankNode.
     *
     * @return {@link RDF} instance to be tested.
     */
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
     * Test method for {@link BlankNode#uniqueReference()}.
     */
    @Test
    public final void testInternalIdentifier() {
        final BlankNode testNull = new BlankNode() {
            @Override
            public String ntriplesString() {
                return null;
            }

            @Override
            public String uniqueReference() {
                return null;
            }
        };
        final BlankNode testAutomatic1 = getBlankNode();
        final BlankNode testAutomatic2 = getBlankNode();
        final BlankNode testManual3a = getBlankNode("3");
        final BlankNode testManual3b = getBlankNode("3");
        final BlankNode testManual4 = getBlankNode("4");

        // Test against our fake stub
        assertNotEquals(testNull.uniqueReference(), testAutomatic1.uniqueReference());
        assertNotEquals(testAutomatic1.uniqueReference(), testNull.uniqueReference());
        assertNotEquals(testNull.uniqueReference(), testManual3a.uniqueReference());
        assertNotEquals(testManual3a.uniqueReference(), testNull.uniqueReference());

        // Test the two imported instances against each other
        assertEquals(testAutomatic1.uniqueReference(), testAutomatic1.uniqueReference());
        assertEquals(testAutomatic2.uniqueReference(), testAutomatic2.uniqueReference());
        assertNotEquals(testAutomatic1.uniqueReference(), testAutomatic2.uniqueReference());
        assertNotEquals(testAutomatic2.uniqueReference(), testAutomatic1.uniqueReference());
        assertNotEquals(testAutomatic1.uniqueReference(), testManual3a.uniqueReference());
        assertEquals(testManual3b.uniqueReference(), testManual3a.uniqueReference());
        assertNotEquals(testManual3a.uniqueReference(), testManual4.uniqueReference());
    }

    /**
     * Test method for {@link BlankNode#equals(java.lang.Object)}.
     */
    @Test
    public final void testEquals() {
        final BlankNode testNull = new BlankNode() {
            @Override
            public String ntriplesString() {
                return null;
            }

            @Override
            public String uniqueReference() {
                return null;
            }
        };
        final BlankNode testAutomatic1 = getBlankNode();
        final BlankNode testAutomatic2 = getBlankNode();
        final BlankNode testManual3a = getBlankNode("3");
        final BlankNode testManual3b = getBlankNode("3");
        final BlankNode testManual4 = getBlankNode("4");

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
     * Test method for {@link BlankNode#hashCode()}.
     */
    @Test
    public final void testHashCode() {
        final BlankNode testNull = new BlankNode() {
            @Override
            public String ntriplesString() {
                return null;
            }

            @Override
            public String uniqueReference() {
                return null;
            }
        };
        final BlankNode testAutomatic1 = getBlankNode();
        final BlankNode testAutomatic2 = getBlankNode();
        final BlankNode testManual3a = getBlankNode("3");
        final BlankNode testManual3b = getBlankNode("3");
        final BlankNode testManual4 = getBlankNode("4");

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
     * Test method for {@link RDFTerm#ntriplesString()}.
     */
    @Test
    public final void testNtriplesString() {
        final BlankNode testNull = new BlankNode() {
            @Override
            public String ntriplesString() {
                return null;
            }

            @Override
            public String uniqueReference() {
                return null;
            }
        };
        final BlankNode testAutomatic1 = getBlankNode();
        final BlankNode testAutomatic2 = getBlankNode();
        final BlankNode testManual3a = getBlankNode("3");
        final BlankNode testManual3b = getBlankNode("3");
        final BlankNode testManual4 = getBlankNode("4");

        // Test against our fake stub
        assertNotEquals(testNull.ntriplesString(), testAutomatic1.ntriplesString());
        assertNotEquals(testAutomatic1.ntriplesString(), testNull.ntriplesString());
        assertNotEquals(testNull.ntriplesString(), testManual3a.ntriplesString());
        assertNotEquals(testManual3a.ntriplesString(), testNull.ntriplesString());

        // Test the two imported instances against each other
        assertEquals(testAutomatic1.ntriplesString(), testAutomatic1.ntriplesString());
        assertEquals(testAutomatic2.ntriplesString(), testAutomatic2.ntriplesString());
        assertNotEquals(testAutomatic1.ntriplesString(), testAutomatic2.ntriplesString());
        assertNotEquals(testAutomatic2.ntriplesString(), testAutomatic1.ntriplesString());
        assertNotEquals(testAutomatic1.ntriplesString(), testManual3a.ntriplesString());
        assertEquals(testManual3b.ntriplesString(), testManual3a.ntriplesString());
        assertNotEquals(testManual3a.ntriplesString(), testManual4.ntriplesString());
    }

}
