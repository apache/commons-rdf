/*
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
/**
 * A simple in-memory implementation of the Commons RDF API.
 * <p>
 * This package contains a simple (if not naive) implementation of
 * {@link org.apache.commons.rdf.api} using in-memory POJO objects.
 * <p>
 * Note that although this module fully implements the commons-rdf API, it
 * should <strong>not</strong> be considered a reference implementation. It is
 * <strong>not thread-safe</strong> nor scalable, but may be useful for testing
 * and simple usage (e.g. prototyping).
 * <p>
 * To use this implementation, create an instance of
 * {@link org.apache.commons.rdf.simple.SimpleRDF} and use methods like
 * {@link org.apache.commons.rdf.simple.SimpleRDF#createGraph} and
 * {@link org.apache.commons.rdf.simple.SimpleRDF#createIRI(String)}.
 * <p>
 * The {@link org.apache.commons.rdf.simple.Types} class provide constant
 * {@link org.apache.commons.rdf.api.IRI}s of the common RDF XML datatypes.
 *
 */
package org.apache.commons.rdf.simple;
