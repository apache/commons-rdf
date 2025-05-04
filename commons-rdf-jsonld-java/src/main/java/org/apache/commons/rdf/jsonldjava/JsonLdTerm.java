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

import org.apache.commons.rdf.api.RDFTerm;

import com.github.jsonldjava.core.RDFDataset.Node;

abstract class AbstractJsonLdTermImpl implements JsonLdTerm {
    final Node node;

    AbstractJsonLdTermImpl(final Node node) {
        this.node = node;
    }

    @Override
    public Node asJsonLdNode() {
        return node;
    }

    @Override
    public String toString() {
        return ntriplesString();
    }

}

/**
 * Extends {@link RDFTerm} to provide a conversion service.
 */
public interface JsonLdTerm extends RDFTerm {

    /**
     * Return the underlying JsonLd {@link Node}.
     *
     * @return JsonLd {@link Node}
     */
    Node asJsonLdNode();
}
