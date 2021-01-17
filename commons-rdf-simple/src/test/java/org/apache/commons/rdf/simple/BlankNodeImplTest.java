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
package org.apache.commons.rdf.simple;

import java.util.UUID;

import org.apache.commons.rdf.api.AbstractBlankNodeTest;
import org.apache.commons.rdf.api.BlankNode;

/**
 * Concrete implementation of BlankNodeImpl test.
 */
public class BlankNodeImplTest extends AbstractBlankNodeTest {

    // Fixed salt just for this test
    private static final UUID SALT = UUID.fromString("35019b59-18b3-4e74-8707-ec55f62a37d6");

    @Override
    protected BlankNode getBlankNode() {
        return new BlankNodeImpl();
    }

    @Override
    protected BlankNode getBlankNode(final String identifier) {
        return new BlankNodeImpl(SALT, identifier);
    }

}
