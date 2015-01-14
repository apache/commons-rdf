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

import java.net.URI;

import com.github.commonsrdf.api.IRI;

public class IRIImpl implements IRI {

	protected URI uri;

	public IRIImpl(String iri) {
		// TODO: Check against http://www.w3.org/TR/n-triples/#n-triples-grammar
		uri = URI.create(iri);
	}

	@Override
	public String getIRIString() {
		return uri.toString();
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
		if (! (obj instanceof IRI)) { 
			return false;
		}
		IRI other = (IRI) obj;
		return getIRIString().equals(other.getIRIString());
	}

	@Override
	public int hashCode() {		
		return getIRIString().hashCode();
	}
	
}
