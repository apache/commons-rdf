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
import java.util.Optional;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.simple.Types;

import com.github.jsonldjava.core.RDFDataset.Node;

public interface JsonLdLiteral extends JsonLdTerm, Literal {
}

class JsonLdLiteralImpl extends JsonLdTermImpl implements JsonLdLiteral {

    JsonLdLiteralImpl(final Node node) {
        super(node);
        if (!node.isLiteral()) {
            throw new IllegalArgumentException("Node is not a Literal:" + node);
        }
    }

    @Override
    public String ntriplesString() {
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        // Escape special characters
        sb.append(getLexicalForm().replace("\\", "\\\\"). // escaped to \\
                replace("\"", "\\\""). // escaped to \"
                replace("\r", "\\r"). // escaped to \r
                replace("\n", "\\n")); // escaped to \n
        sb.append('"');

        if (getLanguageTag().isPresent()) {
            sb.append("@");
            sb.append(getLanguageTag().get());
        } else if (!getDatatype().equals(Types.XSD_STRING)) {
            sb.append("^^");
            sb.append(getDatatype().ntriplesString());
        }
        return sb.toString();
    }

    @Override
    public String getLexicalForm() {
        return node.getValue();
    }

    @Override
    public IRI getDatatype() {
        return new JsonLdIRIImpl(node.getDatatype());
    }

    @Override
    public Optional<String> getLanguageTag() {
        return Optional.ofNullable(node.getLanguage());
    }

    @Override
    public int hashCode() {
        // Should be the same as
        // Objects.hash(getLexicalForm(), getDatatype(), getLanguageTag());
        return Objects.hash(node.getValue(), node.getDatatype(), node.getLanguage());
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof JsonLdLiteral) {
            JsonLdLiteral other = (JsonLdLiteral) obj;
            return asJsonLdNode().compareTo(other.asJsonLdNode()) == 0;
        }
        if (obj instanceof Literal) {
            Literal other = (Literal) obj;
            return getLexicalForm().equals(other.getLexicalForm()) && getDatatype().equals(other.getDatatype())
                    && getLanguageTag().equals(other.getLanguageTag());
        }
        return false;

    }
}
