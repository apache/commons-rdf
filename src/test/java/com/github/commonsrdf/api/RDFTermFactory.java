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
 * needs more parameters), then it may throw UnsupportedOperationException.
 * 
 * @see RDFTerm
 * @see Graph
 * 
 */
public interface RDFTermFactory {
	public BlankNode createBlankNode() throws UnsupportedOperationException;

	public BlankNode createBlankNode(String identifier)
			throws UnsupportedOperationException;

	public Graph createGraph() throws UnsupportedOperationException;

	public IRI createIRI(String iri) throws UnsupportedOperationException;

	public Literal createLiteral(String literal)
			throws UnsupportedOperationException;

	public Literal createLiteral(String literal, IRI dataType)
			throws UnsupportedOperationException;

	public Literal createLiteral(String literal, String language)
			throws UnsupportedOperationException;

	public Triple createTriple(BlankNodeOrIRI subject, IRI predicate,
			RDFTerm object) throws UnsupportedOperationException;

}
