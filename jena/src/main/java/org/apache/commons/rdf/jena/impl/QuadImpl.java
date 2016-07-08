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
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.JenaQuad;
import org.apache.commons.rdf.jena.RDFTermFactoryJena;

public class QuadImpl implements JenaQuad {

	private final Optional<BlankNodeOrIRI> graphName;
	private final RDFTerm object;
	private final IRI predicate;
	private org.apache.jena.sparql.core.Quad quad = null;
	private final BlankNodeOrIRI subject;

	/* package */ QuadImpl(Optional<BlankNodeOrIRI> graphName, BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		this.graphName = Objects.requireNonNull(graphName);
		this.subject = Objects.requireNonNull(subject);
		this.predicate = Objects.requireNonNull(predicate);
		this.object = Objects.requireNonNull(object);
	}

	/* package */ QuadImpl(org.apache.jena.sparql.core.Quad quad, UUID salt) {
		this.quad = Objects.requireNonNull(quad);
		this.graphName = Optional.of((BlankNodeOrIRI) JenaFactory.fromJena(quad.getGraph(), salt));
		this.subject = (BlankNodeOrIRI) JenaFactory.fromJena(quad.getSubject(), salt);
		this.predicate = (IRI) JenaFactory.fromJena(quad.getPredicate(), salt);
		this.object = JenaFactory.fromJena(quad.getObject(), salt);
	}

	@Override
	public org.apache.jena.sparql.core.Quad asJenaQuad() {
		if (quad == null) {
			quad = org.apache.jena.sparql.core.Quad.create(
					RDFTermFactoryJena.toJena(graphName.orElse(null)),
					RDFTermFactoryJena.toJena(subject), 
					RDFTermFactoryJena.toJena(predicate),
					RDFTermFactoryJena.toJena(object));
		}
		return quad;
	}

	@Override
	public Triple asTriple() {
		return new TripleImpl(getSubject(), getPredicate(), getObject());
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof Quad))
			return false;
		Quad quad = (Quad) other;
		return getGraphName().equals(quad.getGraphName()) && getSubject().equals(quad.getSubject())
				&& getPredicate().equals(quad.getPredicate()) && getObject().equals(quad.getObject());
	}

	@Override
	public Optional<BlankNodeOrIRI> getGraphName() {
		return graphName;
	}

	@Override
	public RDFTerm getObject() {
		return object;
	}

	@Override
	public IRI getPredicate() {
		return predicate;
	}

	@Override
	public BlankNodeOrIRI getSubject() {
		return subject;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getSubject(), getPredicate(), getObject(), getGraphName());
	}

	@Override
	public String toString() {
		// kind of nquad syntax
		return getSubject().ntriplesString() + " " + 
				getPredicate().ntriplesString() + " "
				+ getObject().ntriplesString() + " " + 
				getGraphName().map(RDFTerm::ntriplesString).orElse("") + ".";
	}

}
