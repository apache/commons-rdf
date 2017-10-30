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
package org.apache.commons.rdf.jsonldjava;

import java.util.UUID;

import org.apache.commons.rdf.api.AbstractBlankNodeTest;
import org.apache.commons.rdf.api.BlankNode;

import com.github.jsonldjava.core.RDFDataset;

public class JsonLdBlankNodeTest extends AbstractBlankNodeTest {

    String fixedPrefix = "urn:uuid:d028ca89-8b2f-4e18-90a0-8959f955038d#";

    @Override
    protected BlankNode getBlankNode() {
        return getBlankNode(UUID.randomUUID().toString());
    }

    @Override
    protected BlankNode getBlankNode(final String identifier) {
        return new JsonLdBlankNodeImpl(new RDFDataset.BlankNode("_:" + identifier), fixedPrefix);
    }

}
