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

import java.util.Objects;

import org.apache.commons.rdf.jena.JenaRDFTerm;
import org.apache.commons.rdf.jena.JenaVariable;
import org.apache.jena.graph.Node;

public class VariableImpl implements JenaRDFTerm, JenaVariable {

	private Node node;

	VariableImpl(Node node) {	
		if (! node.isVariable()) {
			throw new IllegalArgumentException("Node is not a variable: " + node);
		}
		this.node = node;
	}

	@Override
	public String ntriplesString() {
		return "?" + getVariableName();
	}

	@Override
	public String getVariableName() {
		return node.getName();
	}

	@Override
	public Node asJenaNode() {
		return node;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) { 
			return true;
		}
		if (! (obj instanceof JenaVariable)) { 
			return false;
		}
		return Objects.equals(getVariableName(), ((JenaVariable)obj).getVariableName());
	}

}
