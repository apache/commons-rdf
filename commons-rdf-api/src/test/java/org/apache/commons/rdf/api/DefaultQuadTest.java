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

import static org.junit.Assert.*;

import java.util.Objects;

import org.junit.Test;

public class DefaultQuadTest {
    @Test
    public void asQuad() throws Exception {
        final Quad q = new DummyQuad();
        final Triple t = q.asTriple();
        assertEquals(t, t);
        assertNotEquals(t,  q);
        assertEquals(t, new DummyTriple());
        assertEquals(t, new DummyQuad().asTriple());

        // FIXME: This would not catch if asTriple() accidentally mixed up s/p/o
        // as they are here all the same
        assertEquals(new DummyIRI(1), t.getSubject());
        assertEquals(new DummyIRI(2), t.getPredicate());
        assertEquals(new DummyIRI(3), t.getObject());



        assertEquals(Objects.hash(q.getSubject(), q.getPredicate(), q.getObject()), t.hashCode());
    }

}
