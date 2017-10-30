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
 * Commons RDF, a common library of RDF 1.1 concepts.
 * <p>
 * The core <a href="http://www.w3.org/TR/rdf11-concepts/">RDF 1.1 concepts</a>
 * are represented by corresponding interfaces:
 * <p>
 * A {@link org.apache.commons.rdf.api.Graph} is a collection of
 * {@link org.apache.commons.rdf.api.Triple}s, which have a <em>subject</em>,
 * <em>predicate</em> and <em>object</em> of
 * {@link org.apache.commons.rdf.api.RDFTerm}s. The three types of RDF terms are
 * {@link org.apache.commons.rdf.api.IRI},
 * {@link org.apache.commons.rdf.api.Literal} and
 * {@link org.apache.commons.rdf.api.BlankNode}.
 * <p>
 * Implementations of this API should provide an
 * {@link org.apache.commons.rdf.api.RDF} to facilitate creation of these
 * objects.
 * <p>
 * All the {@link org.apache.commons.rdf.api.RDFTerm} objects are immutable,
 * while a {@link org.apache.commons.rdf.api.Graph} MAY be mutable (e.g. support
 * methods like {@link org.apache.commons.rdf.api.Graph#add(Triple)}).
 * <p>
 * {@link org.apache.commons.rdf.api.RDFSyntax} enumerates the W3C standard RDF
 * 1.1 syntaxes and their media types.
 * <p>
 * For further documentation and contact details, see the
 * <a href="http://commonsrdf.incubator.apache.org/">Commons RDF</a> web site.
 *
 */
package org.apache.commons.rdf.api;
