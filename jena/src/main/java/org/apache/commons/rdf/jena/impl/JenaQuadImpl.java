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
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.jena.ConversionException;
import org.apache.commons.rdf.jena.JenaQuad;

class JenaQuadImpl	extends AbstractQuadLike<BlankNodeOrIRI,IRI,RDFTerm,BlankNodeOrIRI>
	implements JenaQuad {

	JenaQuadImpl(BlankNodeOrIRI subject, IRI predicate, RDFTerm object, Optional<BlankNodeOrIRI> graphName) {
		super(subject, predicate, object, graphName);
	}

	JenaQuadImpl(org.apache.jena.sparql.core.Quad quad, UUID salt) {
		super(quad, salt);
		// Check the conversion
		if ((graphName.isPresent() && ! (graphName.get() instanceof BlankNodeOrIRI)) ||
			! (subject instanceof BlankNodeOrIRI) ||
			! (predicate instanceof IRI) ||
			! (object instanceof RDFTerm)) {
			throw new ConversionException("Can't adapt generalized quad: " + quad);	
		}
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (!(other instanceof Quad))
			return false;
		Quad quad = (Quad) other;
		return getGraphName().equals(quad.getGraphName()) && getSubject().equals(quad.getSubject())
				&& getPredicate().equals(quad.getPredicate()) && getObject().equals(quad.getObject());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getSubject(), getPredicate(), getObject(), getGraphName());
	}
	
}
