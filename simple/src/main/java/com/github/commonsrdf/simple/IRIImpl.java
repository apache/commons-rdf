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

import java.net.URI;

import com.github.commonsrdf.api.IRI;

/** 
 * A simple implementation of IRI.
 *
 */
public class IRIImpl implements IRI {

	protected String iri;

	public IRIImpl(String iri) {
		// should throw IllegalArgumentException on most illegal IRIs
		URI.create(iri);
		// NOTE: We don't keep the URI as it uses outdated RFC2396 and will get some IDNs wrong
		this.iri = iri;
	}

	@Override
	public String getIRIString() {
		return iri;
	}

	@Override
	public String ntriplesString() {
		return "<" + getIRIString() + ">";
	}

	@Override
	public String toString() {
		return ntriplesString();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IRI)) {
			return false;
		}
		IRI other = (IRI) obj;
		return getIRIString().equals(other.getIRIString());
	}

	@Override
	public int hashCode() {
		return iri.hashCode();
	}

}
