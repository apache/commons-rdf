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
 * Commons RDF integration with <a href="http://rdf4j.org/">RDF4J</a>.
 * <p>
 * Use the {@link org.apache.commons.rdf.rdf4j.RDF4J} to convert between Commons RDF and RDF4J types, for
 * instance {@link org.apache.commons.rdf.rdf4j.RDF4J#asQuad(org.eclipse.rdf4j.model.Statement)} converts a
 * RDF4J {@link org.eclipse.rdf4j.model.Statement} to a
 * {@link org.apache.commons.rdf.api.Quad}. Converted RDF terms implement the
 * {@link org.apache.commons.rdf.rdf4j.RDF4JTerm} interface, and converted statements the
 * {@link org.apache.commons.rdf.rdf4j.RDF4JTripleLike} interface, which provide convenience access to the
 * underlying RDF4J implementations.
 * <p>
 * RDF4J {@link org.eclipse.rdf4j.model.Model}s and
 * {@link org.eclipse.rdf4j.repository.Repository} instances can be adapted to
 * Commons RDF {@link org.apache.commons.rdf.api.Graph} and {@link org.apache.commons.rdf.api.Dataset}, e.g. using
 * {@link org.apache.commons.rdf.rdf4j.RDF4J#asGraph(org.eclipse.rdf4j.model.Model)} or
 * {@link org.apache.commons.rdf.rdf4j.RDF4J#asDataset(org.eclipse.rdf4j.repository.Repository, RDF4J.Option...)}
 * The returned adapted graph/dataset is directly mapped, so changes are
 * propagated both ways. For convenience, the marker interface
 * {@link org.apache.commons.rdf.rdf4j.RDF4JGraph} and {@link org.apache.commons.rdf.rdf4j.RDF4JDataset} provide access to the underlying
 * RDF4J implementations.
 * <p>
 * The {@link org.apache.commons.rdf.rdf4j.experimental.RDF4JParser} can be used to parse RDF files using RDF4j. It should
 * be most efficient if used with {@link org.apache.commons.rdf.rdf4j.experimental.RDF4JParser#target(Dataset)} and an
 * adapted {@link org.apache.commons.rdf.rdf4j.RDF4JDataset}, or {@link org.apache.commons.rdf.rdf4j.experimental.RDF4JParser#target(Graph)} and a an
 * adapted {@link org.apache.commons.rdf.rdf4j.RDF4JGraph}
 *
 *
 */
package org.apache.commons.rdf.rdf4j;

// Imports for Javadoc, do not remove
import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
