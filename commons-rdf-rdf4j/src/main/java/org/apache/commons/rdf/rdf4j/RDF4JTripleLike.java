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

import org.apache.commons.rdf.api.TripleLike;
import org.eclipse.rdf4j.model.Statement;

/**
 * Marker interface for RDF4J implementations of {@link TripleLike} statements.
 * <p>
 * This interface is in common with the more specific {@link RDF4JTriple} or
 * {@link RDF4JQuad}.
 * <p>
 * This is backed by a {@link Statement} retrievable with
 * {@link #asStatement()}.
 *
 * @see RDF4JTriple
 * @see RDF4JQuad
 */
public interface RDF4JTripleLike extends TripleLike {

    /**
     * Return the corresponding RDF4J {@link Statement}.
     *
     * @return The corresponding RDF4J Statement.
     */
    Statement asStatement();
}
