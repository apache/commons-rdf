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
package org.apache.commons.rdf.rdf4j.impl;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.rdf.api.BlankNode;
import org.apache.commons.rdf.rdf4j.RDF4JBlankNode;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.rio.turtle.TurtleUtil;

public final class BlankNodeImpl extends AbstractRDFTerm<BNode>
	implements RDF4JBlankNode {
	
	private transient int hashCode = 0;
	private long saltUUIDleast;
	private long saltUUIDmost;
	
	public BlankNodeImpl(BNode bNode, UUID salt) {
		super(bNode);			
		// Space-efficient storage of salt UUID
		saltUUIDmost = salt.getMostSignificantBits();
		saltUUIDleast = salt.getLeastSignificantBits();
	}
	
	public boolean equals(Object obj) { 
		if (obj == this) { 
			return true;
		}
		// NOTE: Do NOT use Bnode.equals() as it has a more generous
		// equality based only on the value.getID();			
		if (obj instanceof BlankNode) {
			BlankNode blankNode = (BlankNode) obj;
			return uniqueReference().equals(blankNode.uniqueReference());								
		}
		return false;
	}

	@Override
	public int hashCode() {
		if (hashCode != 0) {
			return hashCode;
		}
		return hashCode = uniqueReference().hashCode();
	}

	private boolean isValidBlankNodeLabel(String id) {
		// FIXME: Replace with a regular expression?			
		if (id.isEmpty()) { 
			return false;
		}
		if (! TurtleUtil.isBLANK_NODE_LABEL_StartChar(id.codePointAt(0)))  {
			return false;
		}
		for (int i=1; i<id.length(); i++) { 
			if (! TurtleUtil.isBLANK_NODE_LABEL_Char(id.codePointAt(i))) { 
				return false;
			}
		}
		return true;
	}

	@Override
	public String ntriplesString() {
		if (isValidBlankNodeLabel(value.getID())) { 
			return "_:" + value.getID();
		} else {
			return "_:" + UUID.nameUUIDFromBytes(value.getID().getBytes(StandardCharsets.UTF_8));
		}
	}

	@Override
	public String uniqueReference() {
		UUID uuid = new UUID(saltUUIDmost, saltUUIDleast);
		return "urn:uuid:" + uuid + "#" + value.getID();
	}
}