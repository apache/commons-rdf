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
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.api.Graph;

/**
 * A simple implementation of BlankNode.
 *
 */
final class BlankNodeImpl implements BlankNode {

	private static AtomicLong bnodeCounter = new AtomicLong();

	private static final Object DEFAULT_SEED = new Object();

	private final String id;

	public BlankNodeImpl() {
		this(DEFAULT_SEED, "genid:" + bnodeCounter.incrementAndGet());
	}

	public BlankNodeImpl(Object uuidSeed, String id) {
		if (Objects.requireNonNull(id).isEmpty()) {
			throw new IllegalArgumentException("Invalid blank node id: " + id);
		}
		String uuidInput = uuidSeed.toString() + ":" + id;
		// Both the scope and the id are used to create the UUID, ensuring that
		// a caller can reliably create the same bnode if necessary by sending
		// in the same scope.
		// In addition, it would be very difficult for the default constructor
		// to interfere with this process since it uses a local object as its
		// reference.
		this.id = UUID.nameUUIDFromBytes(
				uuidInput.getBytes(StandardCharsets.UTF_8)).toString();
	}

	@Override
	public String internalIdentifier() {
		return id;
	}

	@Override
	public String ntriplesString() {
		return "_:" + id;
	}

	@Override
	public String toString() {
		return ntriplesString();
	}

	@Override
	public int hashCode() {
		return id.hashCode();
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
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
