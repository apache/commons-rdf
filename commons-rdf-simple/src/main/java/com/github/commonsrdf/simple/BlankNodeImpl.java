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
package com.github.commonsrdf.simple;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import com.github.commonsrdf.api.BlankNode;
import com.github.commonsrdf.api.Graph;

/** 
 * A simple implementation of BlankNode.
 *
 */
public class BlankNodeImpl implements BlankNode {

	private static AtomicLong bnodeCounter = new AtomicLong();
	private String id;
	private Optional<Graph> localScope;

	public BlankNodeImpl() {
		this(Optional.empty(), "b" + bnodeCounter.incrementAndGet());
	}

	public BlankNodeImpl(Optional<Graph> localScope, String id) {
		this.localScope = Objects.requireNonNull(localScope);
		if (Objects.requireNonNull(id).isEmpty()) {
			throw new IllegalArgumentException("Invalid blank node id: " + id);
			// NOTE: It is valid for the id to not be a valid ntriples bnode id.
			// See ntriplesString().
		}
		this.id = id;
	}

	@Override
	public String internalIdentifier() {
		return id;
	}

	@Override
	public String ntriplesString() {
		if (id.contains(":")) {
			// FIXME: Perhaps do a SHA hash of the id?
			throw new IllegalStateException(
					"Blank node identifier can't be expressed as ntriples string: "
							+ id);
		}
		return "_:" + id;
	}

	@Override
	public String toString() {
		return ntriplesString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(localScope, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
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
		if (localScope == null) {
			if (other.localScope != null) {
				return false;
			}
		} else if (!localScope.equals(other.localScope)) {
			return false;
		}
		return true;
	}

}
