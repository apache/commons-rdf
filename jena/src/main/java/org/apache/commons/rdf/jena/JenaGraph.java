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

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;

/** Access the Jena graph backing this object */
public interface JenaGraph extends org.apache.commons.rdf.api.Graph {
	
	/**
	 * Return the underlying Jena {@link Graph}.
	 * <p>
	 * Changes to the Jena graph are reflected in the Commons RDF graph and vice versa.
	 * 
	 * @return A Jena {@link Graph}
	 */
	public Graph asJenaGraph();

	/**
	 * Return the underlying Jena {@link Model}.
	 * <p>
	 * Changes to the Jena model are reflected in the Commons RDF graph and vice
	 * versa.
	 * 
	 * @return A Jena {@link Model}
	 */	
	public Model asJenaModel();
}
