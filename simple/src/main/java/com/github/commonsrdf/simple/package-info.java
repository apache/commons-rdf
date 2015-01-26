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
/**
 * A simple in-memory implementation of the Commons RDF API.
 * <p>
 * This package contains a simple (if not naive) implementation of the Commons RDF API 
 * using in-memory POJO objects.
 * <p>
 * Note that although this module fully implements the commons-rdf API,
 * it should *not*  be considered a reference implementation. 
 * It is not thread-safe nor scalable, but may be useful for testing
 * and simple usage (e.g. output from an independent RDF parser).
 * <p>
 * To use this implementation, create an instance of {@link SimpleRDFTermFactory}
 * and use methods like {@link SimpleRDFTermFactory#createGraph} and 
 * {@link SimpleRDFTermFactory#createIRI(String)}.
 * 
 */
package com.github.commonsrdf.simple;

