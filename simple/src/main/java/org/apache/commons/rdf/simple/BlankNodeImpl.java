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

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.simple.SimpleRDFTermFactory.SimpleRDFTerm;

/**
 * A simple implementation of BlankNode.
 */
final class BlankNodeImpl implements BlankNode, SimpleRDFTerm {

    private static final UUID SALT = UUID.randomUUID();
    private static final AtomicLong COUNTER = new AtomicLong();

    private final String uniqueReference;

    public BlankNodeImpl() {
        this(SALT, Long.toString(COUNTER.incrementAndGet()));
    }

    public BlankNodeImpl(UUID uuidSalt, String name) {
        if (Objects.requireNonNull(name).isEmpty()) {
            throw new IllegalArgumentException("Invalid blank node id: " + name);
        }

        // Build a semi-URN - to be hashed for a name-based UUID below
        // Both the scope and the id are used to create the UUID, ensuring that
        // a caller can reliably create the same bnode if necessary by sending
        // in the same scope to RDFTermFactory.createBlankNode(String)
        String uuidInput = "urn:uuid:" + uuidSalt + "#" + name;

        // The above is not a good value for this.id, as the id
        // needs to be further escaped for
        // ntriplesString() (there are no restrictions on
        // RDFTermFactory.createBlankNode(String) ).


        // Rather than implement ntriples escaping here, and knowing
        // our uniqueReference() contain a UUID anyway, we simply
        // create another name-based UUID, and use it within both
        // uniqueReference() and within ntriplesString().
        //
        // A side-effect from this is that the blank node identifier
        // is not preserved or shown in ntriplesString. In a way
        // this is a feature, not a bug. as the contract for RDFTermFactory
        // has no such requirement.
        this.uniqueReference = UUID.nameUUIDFromBytes(
                uuidInput.getBytes(StandardCharsets.UTF_8)).toString();
    }

    @Override
    public String uniqueReference() {
        return uniqueReference;
    }

    @Override
    public String ntriplesString() {
        return "_:" + uniqueReference;
    }

    @Override
    public String toString() {
        return ntriplesString();
    }

    @Override
    public int hashCode() {
        return uniqueReference.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        // We don't support equality with other implementations
        if (!(obj instanceof BlankNodeImpl)) {
            return false;
        }
        BlankNodeImpl other = (BlankNodeImpl) obj;
        if (uniqueReference == null) {
            if (other.uniqueReference != null) {
                return false;
            }
        } else if (!uniqueReference.equals(other.uniqueReference)) {
            return false;
        }
        return true;
    }

}
