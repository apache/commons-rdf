/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

package org.apache.commons.rdf.jena.impl;

import java.util.UUID;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.jena.JenaBlankNode;
import org.apache.jena.graph.Node;

class JenaBlankNodeImpl extends AbstractJenaRDFTerm implements JenaBlankNode {

    private final UUID salt;

    JenaBlankNodeImpl(final Node node, final UUID salt) {
        super(node);
        if (!node.isBlank()) {
            throw new IllegalArgumentException("Node is not a blank node: " + node);
        }
        this.salt = salt;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof BlankNode)) {
            return false;
        }
        final BlankNode bNode = (BlankNode) other;
        return uniqueReference().equals(bNode.uniqueReference());
    }

    @Override
    public int hashCode() {
        return uniqueReference().hashCode();
    }

    @Override
    public String uniqueReference() {
        return salt + asJenaNode().getBlankNodeLabel();
    }

}
