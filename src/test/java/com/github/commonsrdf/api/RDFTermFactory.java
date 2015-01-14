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
package com.github.commonsrdf.api;

/**
 * Factory for creating RDFTerm and Graph instances.
 * <p>
 * If an implementation does not support a particular method (e.g. because it
 * needs more parameters or can't create graphs), then it MAY throw
 * UnsupportedOperationException (as provided by the default implementations).
 * <p>
 * If a factory method does not allow or support a provided parameter, e.g.
 * because an IRI is invalid, then it SHOULD throw IllegalArgumentException.
 * 
 * 
 * @see RDFTerm
 * @see Graph
 * 
 */
public interface RDFTermFactory {
	default BlankNode createBlankNode() throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"createBlankNode() not supported");
	}

	default BlankNode createBlankNode(String identifier)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"createBlankNode(String) not supported");
	}

	default Graph createGraph() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("createGraph() not supported");
	}

	default IRI createIRI(String iri) throws UnsupportedOperationException,
			IllegalArgumentException {
		throw new UnsupportedOperationException(
				"createIRI(String) not supported");
	}

	default Literal createLiteral(String literal)
			throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"createLiteral(String) not supported");
	}

	default Literal createLiteral(String literal, IRI dataType)
			throws UnsupportedOperationException, IllegalArgumentException {
		throw new UnsupportedOperationException(
				"createLiteral(String) not supported");
	}

	default Literal createLiteral(String literal, String language)
			throws UnsupportedOperationException, IllegalArgumentException {
		throw new UnsupportedOperationException(
				"createLiteral(String,String) not supported");
	}

	default Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object) throws UnsupportedOperationException,
			IllegalArgumentException {
		throw new UnsupportedOperationException(
				"createTriple(BlankNodeOrIRI,IRI,RDFTerm) not supported");
	}

}
