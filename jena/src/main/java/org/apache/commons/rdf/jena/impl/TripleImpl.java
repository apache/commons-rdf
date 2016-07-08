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
import java.util.UUID;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.jena.ConversionException;
import org.apache.commons.rdf.jena.JenaTriple;
import org.apache.commons.rdf.jena.JenaRDFTermFactory;

public class TripleImpl implements Triple, JenaTriple {
	private final RDFTerm object;
	private final IRI predicate;
	private final BlankNodeOrIRI subject;
	private org.apache.jena.graph.Triple triple = null;

	/* package */ TripleImpl(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	/* package */ TripleImpl(org.apache.jena.graph.Triple triple, UUID salt) throws ConversionException {
		try {
			this.subject = (BlankNodeOrIRI) JenaFactory.fromJena(triple.getSubject(), salt);
			this.predicate = (IRI) JenaFactory.fromJena(triple.getPredicate(), salt);
		} catch (ClassCastException ex) {
			throw new ConversionException("Can't adapt generalized triple: " + triple, ex);
		}
		this.object = JenaFactory.fromJena(triple.getObject(), salt);
		this.triple = triple;
	}

	@Override
	public org.apache.jena.graph.Triple asJenaTriple() {
		if (triple == null)
			triple = org.apache.jena.graph.Triple.create(JenaRDFTermFactory.toJena(subject),
					JenaRDFTermFactory.toJena(predicate), JenaRDFTermFactory.toJena(object));
		return triple;
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof Triple))
			return false;
		Triple triple = (Triple) other;
		return getSubject().equals(triple.getSubject()) && getPredicate().equals(triple.getPredicate())
				&& getObject().equals(triple.getObject());
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
		return Objects.hash(getSubject(), getPredicate(), getObject());
	}

	@Override
	public String toString() {
		return getSubject() + " " + getPredicate() + " " + getObject() + " .";
	}
}
