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

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.rdf.api.IRI;

/**
 * Types from the RDF and XML Schema vocabularies.
 */
public final class Types implements IRI, SimpleRDF.SimpleRDFTerm {

    /**
     * {@code http://www.w3.org/1999/02/22-rdf-syntax-ns#HTML}
     */
    public static final Types RDF_HTML = new Types("http://www.w3.org/1999/02/22-rdf-syntax-ns#HTML");

    /**
     * {@code http://www.w3.org/1999/02/22-rdf-syntax-ns#langString}
     */
    public static final Types RDF_LANGSTRING = new Types("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString");

    /**
     * {@code http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral}
     *
     * @deprecated Not used in RDF-1.1
     */
    @Deprecated
    public static final Types RDF_PLAINLITERAL = new Types("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral");

    /**
     * {@code http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral}
     */
    public static final Types RDF_XMLLITERAL = new Types("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#anyURI}
     */
    public static final Types XSD_ANYURI = new Types("http://www.w3.org/2001/XMLSchema#anyURI");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#base64Binary}
     */
    public static final Types XSD_BASE64BINARY = new Types("http://www.w3.org/2001/XMLSchema#base64Binary");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#boolean}
     */
    public static final Types XSD_BOOLEAN = new Types("http://www.w3.org/2001/XMLSchema#boolean");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#byte}
     */
    public static final Types XSD_BYTE = new Types("http://www.w3.org/2001/XMLSchema#byte");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#date}
     */
    public static final Types XSD_DATE = new Types("http://www.w3.org/2001/XMLSchema#date");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#dateTime}
     */
    public static final Types XSD_DATETIME = new Types("http://www.w3.org/2001/XMLSchema#dateTime");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#dayTimeDuration}
     */
    public static final Types XSD_DAYTIMEDURATION = new Types("http://www.w3.org/2001/XMLSchema#dayTimeDuration");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#decimal}
     */
    public static final Types XSD_DECIMAL = new Types("http://www.w3.org/2001/XMLSchema#decimal");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#double}
     */
    public static final Types XSD_DOUBLE = new Types("http://www.w3.org/2001/XMLSchema#double");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#duration}
     */
    public static final Types XSD_DURATION = new Types("http://www.w3.org/2001/XMLSchema#duration");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#float}
     */
    public static final Types XSD_FLOAT = new Types("http://www.w3.org/2001/XMLSchema#float");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#gDay}
     */
    public static final Types XSD_GDAY = new Types("http://www.w3.org/2001/XMLSchema#gDay");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#gMonth}
     */
    public static final Types XSD_GMONTH = new Types("http://www.w3.org/2001/XMLSchema#gMonth");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#gMonthDay}
     */
    public static final Types XSD_GMONTHDAY = new Types("http://www.w3.org/2001/XMLSchema#gMonthDay");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#gYear}
     */
    public static final Types XSD_GYEAR = new Types("http://www.w3.org/2001/XMLSchema#gYear");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#gYearMonth}
     */
    public static final Types XSD_GYEARMONTH = new Types("http://www.w3.org/2001/XMLSchema#gYearMonth");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#hexBinary}
     */
    public static final Types XSD_HEXBINARY = new Types("http://www.w3.org/2001/XMLSchema#hexBinary");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#int}
     */
    public static final Types XSD_INT = new Types("http://www.w3.org/2001/XMLSchema#int");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#integer}
     */
    public static final Types XSD_INTEGER = new Types("http://www.w3.org/2001/XMLSchema#integer");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#language}
     */
    public static final Types XSD_LANGUAGE = new Types("http://www.w3.org/2001/XMLSchema#language");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#long}
     */
    public static final Types XSD_LONG = new Types("http://www.w3.org/2001/XMLSchema#long");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#Name}
     */
    public static final Types XSD_NAME = new Types("http://www.w3.org/2001/XMLSchema#Name");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#NCName}
     */
    public static final Types XSD_NCNAME = new Types("http://www.w3.org/2001/XMLSchema#NCName");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#negativeInteger}
     */
    public static final Types XSD_NEGATIVEINTEGER = new Types("http://www.w3.org/2001/XMLSchema#negativeInteger");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#NMTOKEN}
     */
    public static final Types XSD_NMTOKEN = new Types("http://www.w3.org/2001/XMLSchema#NMTOKEN");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#nonNegativeInteger}
     */
    public static final Types XSD_NONNEGATIVEINTEGER = new Types("http://www.w3.org/2001/XMLSchema#nonNegativeInteger");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#nonPositiveInteger}
     */
    public static final Types XSD_NONPOSITIVEINTEGER = new Types("http://www.w3.org/2001/XMLSchema#nonPositiveInteger");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#normalizedString}
     */
    public static final Types XSD_NORMALIZEDSTRING = new Types("http://www.w3.org/2001/XMLSchema#normalizedString");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#positiveInteger}
     */
    public static final Types XSD_POSITIVEINTEGER = new Types("http://www.w3.org/2001/XMLSchema#positiveInteger");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#short}
     */
    public static final Types XSD_SHORT = new Types("http://www.w3.org/2001/XMLSchema#short");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#string}
     */
    public static final Types XSD_STRING = new Types("http://www.w3.org/2001/XMLSchema#string");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#time}
     */
    public static final Types XSD_TIME = new Types("http://www.w3.org/2001/XMLSchema#time");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#token}
     */
    public static final Types XSD_TOKEN = new Types("http://www.w3.org/2001/XMLSchema#token");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#unsignedByte}
     */
    public static final Types XSD_UNSIGNEDBYTE = new Types("http://www.w3.org/2001/XMLSchema#unsignedByte");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#unsignedInt}
     */
    public static final Types XSD_UNSIGNEDINT = new Types("http://www.w3.org/2001/XMLSchema#unsignedInt");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#unsignedLong}
     */
    public static final Types XSD_UNSIGNEDLONG = new Types("http://www.w3.org/2001/XMLSchema#unsignedLong");

    /**
     * {@code http://www.w3.org/2001/XMLSchema#unsignedShort}
     */
    public static final Types XSD_UNSIGNEDSHORT = new Types("http://www.w3.org/2001/XMLSchema#unsignedShort");

    private static final Set<IRI> ALL_TYPES;

    static {
        final Set<IRI> tempTypes = new LinkedHashSet<>();
        tempTypes.add(RDF_HTML);
        tempTypes.add(RDF_LANGSTRING);
        tempTypes.add(RDF_PLAINLITERAL);
        tempTypes.add(RDF_XMLLITERAL);
        tempTypes.add(XSD_ANYURI);
        tempTypes.add(XSD_BASE64BINARY);
        tempTypes.add(XSD_BOOLEAN);
        tempTypes.add(XSD_BYTE);
        tempTypes.add(XSD_DATE);
        tempTypes.add(XSD_DATETIME);
        tempTypes.add(XSD_DAYTIMEDURATION);
        tempTypes.add(XSD_DECIMAL);
        tempTypes.add(XSD_DOUBLE);
        tempTypes.add(XSD_DURATION);
        tempTypes.add(XSD_FLOAT);
        tempTypes.add(XSD_GDAY);
        tempTypes.add(XSD_GMONTH);
        tempTypes.add(XSD_GMONTHDAY);
        tempTypes.add(XSD_GYEAR);
        tempTypes.add(XSD_GYEARMONTH);
        tempTypes.add(XSD_HEXBINARY);
        tempTypes.add(XSD_INT);
        tempTypes.add(XSD_INTEGER);
        tempTypes.add(XSD_LANGUAGE);
        tempTypes.add(XSD_LONG);
        tempTypes.add(XSD_NAME);
        tempTypes.add(XSD_NCNAME);
        tempTypes.add(XSD_NEGATIVEINTEGER);
        tempTypes.add(XSD_NMTOKEN);
        tempTypes.add(XSD_NONNEGATIVEINTEGER);
        tempTypes.add(XSD_NONPOSITIVEINTEGER);
        tempTypes.add(XSD_NORMALIZEDSTRING);
        tempTypes.add(XSD_POSITIVEINTEGER);
        tempTypes.add(XSD_SHORT);
        tempTypes.add(XSD_STRING);
        tempTypes.add(XSD_TIME);
        tempTypes.add(XSD_TOKEN);
        tempTypes.add(XSD_UNSIGNEDBYTE);
        tempTypes.add(XSD_UNSIGNEDINT);
        tempTypes.add(XSD_UNSIGNEDLONG);
        tempTypes.add(XSD_UNSIGNEDSHORT);
        ALL_TYPES = Collections.unmodifiableSet(tempTypes);
    }

    /**
     * Gets the IRI from this collection if it is present, or return
     * {@link Optional#empty()} otherwise.
     *
     * @param nextIRI
     *            The IRI to look for.
     * @return An {@link Optional} containing the IRI from this collection or
     *         {@link Optional#empty()} if it is not present here.
     */
    public static Optional<IRI> get(final IRI nextIRI) {
        if (ALL_TYPES.contains(nextIRI)) {
            // If we know about this IRI, then look through our set to find the
            // object that matches and return it
            for (final IRI nextType : ALL_TYPES) {
                if (nextType.equals(nextIRI)) {
                    return Optional.of(nextType);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Gets an immutable set of the IRIs used by the RDF-1.1 specification to
     * define types, from the RDF and XML Schema vocabularies.
     *
     * @return A {@link Set} containing all of the IRIs in this collection.
     */
    public static Set<IRI> values() {
        return ALL_TYPES;
    }

    private final IRI field;

    private Types(final String field) {
        this.field = new IRIImpl(field);
    }

    @Override
    public boolean equals(final Object other) {
        return this.field.equals(other);
    }

    @Override
    public String getIRIString() {
        return this.field.getIRIString();
    }

    @Override
    public int hashCode() {
        return this.field.hashCode();
    }

    @Override
    public String ntriplesString() {
        return this.field.ntriplesString();
    }

    @Override
    public String toString() {
        return this.field.toString();
    }
}
