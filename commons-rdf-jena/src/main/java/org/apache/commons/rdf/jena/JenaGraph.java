/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

package org.apache.commons.rdf.jena;

// NOTE: To avoid confusion, don't import Graph as it exists in both APIs
import org.apache.jena.rdf.model.Model;
//

/**
 * A Jena-backed {@link org.apache.commons.rdf.api.Graph}.
 * <p>
 * The underlying Jena {@link org.apache.jena.graph.Graph} can be accessed with
 * {@link #asJenaGraph()}.
 */
public interface JenaGraph extends org.apache.commons.rdf.api.Graph {

    /**
     * Return the underlying Jena {@link org.apache.jena.graph.Graph}.
     * <p>
     * Changes to the Jena graph are reflected in the Commons RDF graph and vice
     * versa.
     *
     * @return A Jena {@link org.apache.jena.graph.Graph}
     */
    org.apache.jena.graph.Graph asJenaGraph();

    /**
     * Return the graph as a Jena {@link Model}.
     * <p>
     * Changes to the Jena model are reflected in the Commons RDF graph and vice
     * versa.
     * <p>
     * Note that in some cases there is no underlying Jena {@link Model}, in
     * which case this method will create one. Subsequent calls should return
     * the same model.
     *
     * @return A Jena {@link Model}
     */
    Model asJenaModel();
}
