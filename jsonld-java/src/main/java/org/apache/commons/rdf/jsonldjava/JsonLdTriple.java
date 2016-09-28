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

import java.util.Objects;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;

import com.github.jsonldjava.core.RDFDataset.Quad;

public interface JsonLdTriple extends Triple {

	/**
	 * Return the underlying JsonLD {@link com.github.jsonldjava.core.RDFDataset.Quad}
	 * 
	 * @return The JsonLD {@link com.github.jsonldjava.core.RDFDataset.Quad}
	 */
	public com.github.jsonldjava.core.RDFDataset.Quad asJsonLdQuad();
}
	
	final class JsonLdTripleImpl extends JsonLdQuadLikeImpl<BlankNodeOrIRI, IRI, RDFTerm, RDFTerm>
		implements JsonLdTriple {
		
		JsonLdTripleImpl(Quad quad, String blankNodePrefix) {
			super(quad, blankNodePrefix);
		}
	
		@Override
		public boolean equals(Object obj) {
			if (! (obj instanceof Triple)) {
				return false;
			}
			Triple other = (Triple) obj;
			return getSubject().equals(other.getSubject()) && 
					getPredicate().equals(other.getPredicate()) && 
					getObject().equals(other.getObject());
		}
	
		@Override
		public int hashCode() {
			return Objects.hash(getSubject(), getPredicate(), getObject());
		}
	}
