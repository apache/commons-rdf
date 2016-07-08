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

package org.apache.commons.rdf.jena.impl;

import org.apache.commons.rdf.jena.JenaAny;
import org.apache.commons.rdf.jena.JenaRDFTerm;
import org.apache.jena.graph.Node;

public class AnyImpl implements JenaRDFTerm, JenaAny {

	static class Singleton {
		static AnyImpl instance = new AnyImpl();
	}
	
	/**
	 * Private constructor
	 * 
	 * @see {@link Singleton#instance}
	 */
	private AnyImpl() {
	}
	
	@Override
	public String ntriplesString() {
		return "[]";
	}

	@Override
	public Node asJenaNode() {
		return Node.ANY;
	}

}
