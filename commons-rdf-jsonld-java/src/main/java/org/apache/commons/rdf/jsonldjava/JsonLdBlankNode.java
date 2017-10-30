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

import org.apache.commons.rdf.api.BlankNode;

import com.github.jsonldjava.core.RDFDataset.Node;

public interface JsonLdBlankNode extends JsonLdTerm, BlankNode {
}

final class JsonLdBlankNodeImpl extends JsonLdTermImpl implements JsonLdBlankNode {
    private final String blankNodePrefix;

    JsonLdBlankNodeImpl(final Node node, final String blankNodePrefix) {
        super(node);
        this.blankNodePrefix = blankNodePrefix;
        if (!node.isBlankNode()) {
            throw new IllegalArgumentException("Node is not a BlankNode:" + node);
        }
    }

    @Override
    public String ntriplesString() {
        // TODO: Escape if this is not valid ntriples string (e.g. contains :)
        return node.getValue();
    }

    @Override
    public String uniqueReference() {
        return blankNodePrefix + node.getValue();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof BlankNode)) {
            return false;
        }
        final BlankNode other = (BlankNode) obj;
        return uniqueReference().equals(other.uniqueReference());
    }

    @Override
    public int hashCode() {
        return uniqueReference().hashCode();
    }

    @Override
    public String toString() {
        return ntriplesString() + " [" + uniqueReference() + "]";
    }
}
