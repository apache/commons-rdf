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

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.jena.sparql.core.DatasetGraph;

/**
 * A Jena-backed {@link Dataset}.
 * <p>
 * The underlying Jena {@link DatasetGraph} can be accessed with
 * {@link #asJenaDatasetGraph()}.
 */
public interface JenaDataset extends Dataset {

    /**
     * Return the underlying Jena {@link DatasetGraph}.
     * <p>
     * Changes to the Jena <em>dataset graph</em> are reflected in the Commons
     * RDF dataset and vice versa.
     *
     * @return A Jena {@link DatasetGraph}
     */
    DatasetGraph asJenaDatasetGraph();

    /**
     * Return a union graph view of this dataset.
     * <p>
     * The <em>union graph</em> contains triples in any graph (including the
     * default graph).
     * <p>
     * Changes in the union graph are reflected in the Commons RDF dataset and
     * vice versa. Triples added to the graph are added to the default graph.
     *
     * @return A union {@link Graph}
     */
    JenaGraph getUnionGraph();

}
