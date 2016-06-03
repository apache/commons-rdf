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

import org.apache.commons.rdf.api.Graph;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.Repository;


/**
 * Marker interface for RDF4J implementations of Graph.
 * 
 */
public interface RDF4JGraph extends Graph {
	
	/**
	 * Return the corresponding RDF4J {@link Model}, if present.
	 * <p>
	 * The return value is {@link Optional#isPresent()} if this graph is
	 * backed by a Model.
	 * <p>
	 * Changes to the Model are reflected in this Graph, and
	 * vice versa.
	 * 
	 * @return The corresponding RDF4J Model.
	 */
	public Optional<Model> asModel();
	
	/**
	 * Return the corresponding RDF4J {@link Repository}, if present.
	 * <p>
	 * The return value is {@link Optional#isPresent()} if this graph is
	 * backed by a Repository.
	 * <p>
	 * Changes to the Repository are reflected in this Graph, and
	 * vice versa.
	 * 
	 * @return The corresponding RDF4J Repository.
	 */
	public Optional<Repository> asRepository();

}
