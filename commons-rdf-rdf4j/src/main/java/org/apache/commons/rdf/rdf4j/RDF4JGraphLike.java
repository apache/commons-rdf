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
package org.apache.commons.rdf.rdf4j;

import java.util.Optional;

import org.apache.commons.rdf.api.GraphLike;
import org.apache.commons.rdf.api.TripleLike;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.Repository;

/**
 * Marker interface for RDF4J implementations of GraphLike.
 * <p>
 * This is a common interface for {@link RDF4JGraph} and {@link RDF4JDataset}
 * which provides access to the underlying RDF4J {@link Model} and/or
 * {@link Repository}.
 * <p>
 * At least one of {@link #asModel()} or {@link #asRepository()} will always be
 * {@link Optional#isPresent()}.
 *
 * @see RDF4JDataset
 * @see RDF4JGraph
 */
public interface RDF4JGraphLike<T extends TripleLike> extends GraphLike<T>, AutoCloseable {

    /**
     * Return the corresponding RDF4J {@link Model}, if present.
     * <p>
     * The return value is {@link Optional#isPresent()} if this is backed by a
     * Model.
     * <p>
     * Changes to the Model are reflected in both directions.
     *
     * @return The corresponding RDF4J Model.
     */
    Optional<Model> asModel();

    /**
     * Return the corresponding RDF4J {@link Repository}, if present.
     * <p>
     * The return value is {@link Optional#isPresent()} if this is backed by a
     * Repository.
     * <p>
     * Changes to the Repository are reflected in both directions.
     *
     * @return The corresponding RDF4J Repository.
     */
    Optional<Repository> asRepository();

}
