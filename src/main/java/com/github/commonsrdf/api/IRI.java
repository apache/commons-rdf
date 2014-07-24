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
 * An <a href= "http://www.w3.org/TR/rdf11-concepts/#dfn-iri"
 * >RDF-1.1 IRI</a>, as defined by <a href=
 * "http://www.w3.org/TR/rdf11-concepts/" >RDF-1.1 Concepts
 * and Abstract Syntax</a>, a W3C Recommendation published on 25 February 2014.<br>
 */
public interface IRI extends BlankNodeOrIRI {

	/**
	 * Returns the IRI encoded as a native Unicode String.<br>
	 * 
	 * The returned string must not include URL-encoding to escape 
	 * non-ASCII characters.
	 * 
	 * @return The IRI encoded as a native Unicode String.
	 */
    String getIRIString();
}
