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
package org.apache.commons.rdf.simple;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;

import java.util.Objects;

/**
 * A simple implementation of Triple.
 */
final class TripleImpl implements Triple {

    private final BlankNodeOrIRI subject;
    private final IRI predicate;
    private final RDFTerm object;

    /**
     * Construct Triple from its constituent parts.
     * <p>
     * The objects are not changed. All mapping of BNode objects is done in
     * {@link SimpleRDF#createTriple(BlankNodeOrIRI, IRI, RDFTerm)}.
     *
     * @param subject
     *            subject of triple
     * @param predicate
     *            predicate of triple
     * @param object
     *            object of triple
     */
    public TripleImpl(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        this.subject = Objects.requireNonNull(subject);
        this.predicate = Objects.requireNonNull(predicate);
        this.object = Objects.requireNonNull(object);
    }

    @Override
    public BlankNodeOrIRI getSubject() {
        return subject;
    }

    @Override
    public IRI getPredicate() {
        return predicate;
    }

    @Override
    public RDFTerm getObject() {
        return object;
    }

    @Override
    public String toString() {
        return getSubject().ntriplesString() + " " + getPredicate().ntriplesString() + " "
                + getObject().ntriplesString() + " .";
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, predicate, object);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Triple)) {
            return false;
        }
        final Triple other = (Triple) obj;
        return getSubject().equals(other.getSubject()) && getPredicate().equals(other.getPredicate())
                && getObject().equals(other.getObject());
    }

}
