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

class JenaTripleImpl extends AbstractQuadLike<BlankNodeOrIRI, IRI, RDFTerm, RDFTerm> implements JenaTriple {

    JenaTripleImpl(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        super(subject, predicate, object);
    }

    JenaTripleImpl(final org.apache.jena.graph.Triple triple, final UUID salt) throws ConversionException {
        super(triple, salt);
        // Check the conversion
        if (!(subject instanceof BlankNodeOrIRI) || !(predicate instanceof IRI) || !(object instanceof RDFTerm)) {
            throw new ConversionException("Can't adapt generalized triple: " + quad);
        }
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Triple)) {
            return false;
        }
        final Triple triple = (Triple) other;
        return getSubject().equals(triple.getSubject()) && getPredicate().equals(triple.getPredicate())
                && getObject().equals(triple.getObject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubject(), getPredicate(), getObject());
    }

}
