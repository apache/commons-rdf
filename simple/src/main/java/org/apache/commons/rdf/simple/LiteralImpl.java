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

import java.util.IllformedLocaleException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;

/**
 * A simple implementation of Literal.
 */
final class LiteralImpl implements Literal, SimpleRDF.SimpleRDFTerm {

    private static final String QUOTE = "\"";

    private final IRI dataType;
    private final String languageTag;
    private final String lexicalForm;

    public LiteralImpl(final String literal) {
        this(literal, Types.XSD_STRING);
    }

    public LiteralImpl(final String lexicalForm, final IRI dataType) {
        this.lexicalForm = Objects.requireNonNull(lexicalForm);
        this.dataType = Types.get(Objects.requireNonNull(dataType)).orElse(dataType);
        if (Types.RDF_LANGSTRING.equals(this.dataType)) {
            throw new IllegalArgumentException(
                    "Cannot create a non-language literal with type " + Types.RDF_LANGSTRING);
        }
        this.languageTag = null;
    }

    public LiteralImpl(final String literal, final String languageTag) {
        this.lexicalForm = Objects.requireNonNull(literal);
        this.languageTag = Objects.requireNonNull(lowerCase(languageTag));
        if (languageTag.isEmpty()) {
            // TODO: Check against
            // http://www.w3.org/TR/n-triples/#n-triples-grammar
            throw new IllegalArgumentException("Language tag can't be null");
        }
        try {
            new Locale.Builder().setLanguageTag(languageTag);
        } catch (final IllformedLocaleException ex) {
            throw new IllegalArgumentException("Invalid languageTag: " + languageTag, ex);
        }

        this.dataType = Types.RDF_LANGSTRING;
    }

    @Override
    public IRI getDatatype() {
        return dataType;
    }

    @Override
    public Optional<String> getLanguageTag() {
        return Optional.ofNullable(languageTag);
    }

    @Override
    public String getLexicalForm() {
        return lexicalForm;
    }

    @Override
    public String ntriplesString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(QUOTE);
        // Escape special characters
        sb.append(getLexicalForm().replace("\\", "\\\\"). // escaped to \\
                replace("\"", "\\\""). // escaped to \"
                replace("\r", "\\r"). // escaped to \r
                replace("\n", "\\n")); // escaped to \n
        sb.append(QUOTE);

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
    public String toString() {
        return ntriplesString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(lexicalForm, dataType, languageTag);
    }

    private static String lowerCase(String langTag) { 
        return langTag.toLowerCase(Locale.ROOT);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof Literal)) {
            return false;
        }
        final Literal literal = (Literal) obj;
        return getDatatype().equals(literal.getDatatype()) && getLexicalForm().equals(literal.getLexicalForm())
                && getLanguageTag().equals(literal.getLanguageTag().map(LiteralImpl::lowerCase));
    }

}
