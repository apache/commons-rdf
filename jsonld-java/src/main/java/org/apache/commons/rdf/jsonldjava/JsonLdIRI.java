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

import org.apache.commons.rdf.api.IRI;

import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Node;

public interface JsonLdIRI extends JsonLdTerm, IRI {
}

final class JsonLdIRIImpl extends JsonLdTermImpl implements JsonLdIRI {

    JsonLdIRIImpl(final Node node) {
        super(node);
        if (!node.isIRI()) {
            throw new IllegalArgumentException("Node is not an IRI:" + node);
        }
    }

    JsonLdIRIImpl(final String iri) {
        super(new RDFDataset.IRI(iri));
    }

    @Override
    public String ntriplesString() {
        return "<" + node.getValue() + ">";
    }

    @Override
    public String getIRIString() {
        return node.getValue();
    }

    @Override
    public int hashCode() {
        return node.getValue().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof IRI)) {
            return false;
        }
        final IRI other = (IRI) obj;
        return node.getValue().equals(other.getIRIString());
    }
}
