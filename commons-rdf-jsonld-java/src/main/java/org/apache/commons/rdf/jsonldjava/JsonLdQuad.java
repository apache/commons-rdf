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
package org.apache.commons.rdf.jsonldjava;

import java.util.Objects;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
// Note: To avoid confusion - don't import either Quad

/**
 * An empty extension of {@link org.apache.commons.rdf.api.Quad} and {@link JsonLdTripleLike}.
 */
public interface JsonLdQuad extends org.apache.commons.rdf.api.Quad, JsonLdTripleLike {

}

final class JsonLdQuadImpl extends JsonLdQuadLikeImpl<BlankNodeOrIRI, IRI, RDFTerm, BlankNodeOrIRI>
        implements JsonLdQuad {

    JsonLdQuadImpl(final com.github.jsonldjava.core.RDFDataset.Quad quad, final String blankNodePrefix) {
        super(quad, blankNodePrefix);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof org.apache.commons.rdf.api.Quad)) {
            return false;
        }
        final org.apache.commons.rdf.api.Quad other = (org.apache.commons.rdf.api.Quad) obj;
        return getGraphName().equals(other.getGraphName()) && getSubject().equals(other.getSubject())
                && getPredicate().equals(other.getPredicate()) && getObject().equals(other.getObject());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGraphName(), getSubject(), getPredicate(), getObject());
    }
}
