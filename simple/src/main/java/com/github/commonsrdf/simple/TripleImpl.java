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

import com.github.commonsrdf.api.BlankNode;
import com.github.commonsrdf.api.BlankNodeOrIRI;
import com.github.commonsrdf.api.Graph;
import com.github.commonsrdf.api.IRI;
import com.github.commonsrdf.api.Literal;
import com.github.commonsrdf.api.RDFTerm;
import com.github.commonsrdf.api.Triple;

/**
 * A simple implementation of Triple.
 *
 */
public class TripleImpl implements Triple {

	private BlankNodeOrIRI subject;
	private IRI predicate;
	private RDFTerm object;

	/**
	 * Construct Triple from its constituent parts.
	 * <p>
	 * The parts may be copied to ensure they are in scope.
	 * 
	 * @param subject subject of triple
	 * @param predicate predicate of triple
	 * @param object object of triple
	 */
	public TripleImpl(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		this.subject = (BlankNodeOrIRI) inScope(Optional.empty(),
				Objects.requireNonNull(subject));
		this.predicate = (IRI) inScope(null, Objects.requireNonNull(predicate));
		this.object = inScope(Optional.empty(), Objects.requireNonNull(object));
	}

	/**
	 * Construct Triple by cloning another Triple and its constituent parts.
	 * <p>
	 * The parts of the triple may be copied to ensure they are in scope.
	 * 
	 * @param localScope
	 *            Scope to create new triple in.
	 * @param triple
	 *            Triple to clone
	 */
	public TripleImpl(Optional<Graph> localScope, Triple triple) {
		Objects.requireNonNull(localScope);
		Objects.requireNonNull(triple);

		this.subject = (BlankNodeOrIRI) inScope(localScope, triple.getSubject());
		this.predicate = (IRI) inScope(localScope, triple.getPredicate());
		this.object = inScope(localScope, triple.getObject());
	}

	private RDFTerm inScope(Optional<Graph> localScope, RDFTerm object) {
		if (!(object instanceof BlankNode) && !(object instanceof IRI)
				& !(object instanceof Literal)) {
			throw new IllegalArgumentException(
					"RDFTerm must be BlankNode, IRI or Literal");
		}
		if (object instanceof BlankNode) {
			BlankNode blankNode = (BlankNode) object;
			return new BlankNodeImpl(Objects.requireNonNull(localScope),
					blankNode.internalIdentifier());
		} else if (object instanceof IRI && !(object instanceof IRIImpl)) {
			IRI iri = (IRI) object;
			return new IRIImpl(iri.getIRIString());
		} else if (object instanceof Literal
				&& !(object instanceof LiteralImpl)) {
			Literal literal = (Literal) object;
			if (literal.getLanguageTag().isPresent()) {
				return new LiteralImpl(literal.getLexicalForm(), literal
						.getLanguageTag().get());
			} else {
				IRI dataType = (IRI) inScope(localScope, literal.getDatatype());
				return new LiteralImpl(literal.getLexicalForm(), dataType);
			}
		} else {
			return object;
		}
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
		return Objects.hash(subject, predicate, object);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Triple)) {
			return false;
		}
		Triple other = (Triple) obj;
		return getSubject().equals(other.getSubject())
				&& getPredicate().equals(other.getPredicate())
				&& getObject().equals(other.getObject());
	}

}
