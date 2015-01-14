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

import com.github.commonsrdf.api.BlankNode;
import com.github.commonsrdf.api.BlankNodeOrIRI;
import com.github.commonsrdf.api.Graph;
import com.github.commonsrdf.api.IRI;
import com.github.commonsrdf.api.RDFTerm;
import com.github.commonsrdf.api.Triple;

public class TripleImpl implements Triple {

	private BlankNodeOrIRI subject;
	private IRI predicate;
	private RDFTerm object;

	public TripleImpl(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		if (subject == null || predicate == null || object == null) {
			throw new NullPointerException("subject, predicate or object was null");
		}
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	/** Construct Triple by cloning another Triple and its constituent parts
	 * @param localScope Scope to create new triple in
	 * @param triple Triple to clone
	 */
	public TripleImpl(Graph localScope, Triple triple) {
		this.subject = (BlankNodeOrIRI)inScope(localScope, triple.getSubject());
		this.predicate = (IRI)inScope(localScope, triple.getPredicate());
		this.object = inScope(localScope, triple.getObject());
	}

	private RDFTerm inScope(Graph localScope, RDFTerm object) {
		if (object instanceof BlankNode) {
			BlankNode blankNode = (BlankNode) object; 
			return new BlankNodeImpl(localScope, blankNode.internalIdentifier());
		}
		return object;
	}

	@Override
	public BlankNodeOrIRI getSubject() {
		return subject;
	}

	@Override
	public IRI getPredicate() {
		return predicate;
	}

	@Override
	public RDFTerm getObject() {
		return object;
	}

	@Override
	public String toString() {
		return getSubject().ntriplesString() + " "
				+ getPredicate().ntriplesString() + " "
				+ getObject().ntriplesString() + " .";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + subject.hashCode();
		result = prime * result +  predicate.hashCode();
		result = prime * result + object.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Triple)) {
			return false;
		}
		Triple other = (Triple) obj;
		return getSubject().equals(other.getSubject()) &&
				getPredicate().equals(other.getPredicate()) && 
				getObject().equals(other.getObject());		
	}
	
	
	
}
