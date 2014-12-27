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
package com.github.commonsrdf.dummyimpl;

import java.util.concurrent.atomic.AtomicLong;

import com.github.commonsrdf.api.BlankNode;

public class BlankNodeImpl implements BlankNode {

	private static AtomicLong bnodeCounter = new AtomicLong();
	private String id;

	public BlankNodeImpl() {
		this("b" + bnodeCounter.incrementAndGet());
	}

	public BlankNodeImpl(String id) {
		if (id == null || id.isEmpty() || id.contains(":")) {
			// TODO: Check against
			// http://www.w3.org/TR/n-triples/#n-triples-grammar
			throw new IllegalArgumentException("Invalid blank node id: " + id);
		}
		this.id = id;
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

}
