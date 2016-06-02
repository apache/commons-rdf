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

import org.apache.commons.rdf.rdf4j.RDF4JIRI;

public final class IRIImpl extends AbstractRDFTerm<org.eclipse.rdf4j.model.IRI> 
	implements RDF4JIRI {

	public IRIImpl(org.eclipse.rdf4j.model.IRI iri) {
		super(iri);			
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == this) { return true; }
		if (obj instanceof IRIImpl) {
			IRIImpl impl = (IRIImpl) obj; 
			return asValue().equals(impl.asValue());
		}
		if (obj instanceof org.apache.commons.rdf.api.IRI) {
			org.apache.commons.rdf.api.IRI iri = (org.apache.commons.rdf.api.IRI) obj;
			return value.toString().equals(iri.getIRIString());
		}
		return false;
	}

	@Override
	public String getIRIString() {
		return value.toString();
	}

	public int hashCode() {
		// Same definition
		return value.hashCode();
	}

	@Override
	public String ntriplesString() {
		return "<" + value.toString() +  ">";
	}
	@Override
	public String toString() {
		return value.toString();
	}
	
}