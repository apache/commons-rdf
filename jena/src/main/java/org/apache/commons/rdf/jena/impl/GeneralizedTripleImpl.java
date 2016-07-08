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

import java.util.UUID;

import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.jena.ConversionException;
import org.apache.commons.rdf.jena.JenaTripleLike;
import org.apache.commons.rdf.jena.JenaRDFTermFactory;

public class GeneralizedTripleImpl implements JenaTripleLike<RDFTerm, RDFTerm, RDFTerm> {
	private final RDFTerm object;
	private final RDFTerm predicate;
	private final RDFTerm subject;
	private org.apache.jena.graph.Triple triple = null;

	/* package */ GeneralizedTripleImpl(RDFTerm subject, RDFTerm predicate, RDFTerm object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
	}

	/* package */ GeneralizedTripleImpl(org.apache.jena.graph.Triple triple, UUID salt) throws ConversionException {
		this.subject = JenaFactory.fromJena(triple.getSubject(), salt);
		this.predicate = JenaFactory.fromJena(triple.getPredicate(), salt);
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
	public RDFTerm getObject() {
		return object;
	}

	@Override
	public RDFTerm getPredicate() {
		return predicate;
	}

	@Override
	public RDFTerm getSubject() {
		return subject;
	}

	@Override
	public String toString() {
		return getSubject() + " " + getPredicate() + " " + getObject() + " .";
	}
	
}
