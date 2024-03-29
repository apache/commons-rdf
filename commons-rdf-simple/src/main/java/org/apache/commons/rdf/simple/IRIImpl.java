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

import java.net.URI;

import org.apache.commons.rdf.api.IRI;

/**
 * A simple implementation of IRI.
 */
final class IRIImpl implements IRI, SimpleRDF.SimpleRDFTerm {

    private final String iri;

    public IRIImpl(final String iri) {
        // should throw IllegalArgumentException on most illegal IRIs
        URI.create(iri);
        // NOTE: We don't keep the URI as it uses outdated RFC2396 and will get
        // some IDNs wrong
        this.iri = iri;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof IRI)) {
            return false;
        }
        final IRI other = (IRI) obj;
        return getIRIString().equals(other.getIRIString());
    }

    @Override
    public String getIRIString() {
        return iri;
    }

    @Override
    public int hashCode() {
        return iri.hashCode();
    }

    @Override
    public String ntriplesString() {
        return "<" + getIRIString() + ">";
    }

    @Override
    public String toString() {
        return ntriplesString();
    }

}
