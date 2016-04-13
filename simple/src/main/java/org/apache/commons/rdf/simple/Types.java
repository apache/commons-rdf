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

import org.apache.commons.rdf.api.IRI;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Types from the RDF and XML Schema vocabularies.
 */
public final class Types implements IRI, SimpleRDFTermFactory.SimpleRDFTerm {

    /**
     * <tt>http://www.w3.org/1999/02/22-rdf-syntax-ns#HTML</tt>
     */
    public static final Types RDF_HTML = new Types(
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#HTML");

    /**
     * <tt>http://www.w3.org/1999/02/22-rdf-syntax-ns#langString</tt>
     */
    public static final Types RDF_LANGSTRING = new Types(
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString");

    /**
     * <tt>http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral</tt>
     *
     * @deprecated Not used in RDF-1.1
     */
    @Deprecated
    public static final Types RDF_PLAINLITERAL = new Types(
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral");

    /**
     * <tt>http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral</tt>
     */
    public static final Types RDF_XMLLITERAL = new Types(
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#anyURI</tt>
     */
    public static final Types XSD_ANYURI = new Types(
            "http://www.w3.org/2001/XMLSchema#anyURI");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#base64Binary</tt>
     */
    public static final Types XSD_BASE64BINARY = new Types(
            "http://www.w3.org/2001/XMLSchema#base64Binary");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#boolean</tt>
     */
    public static final Types XSD_BOOLEAN = new Types(
            "http://www.w3.org/2001/XMLSchema#boolean");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#byte</tt>
     */
    public static final Types XSD_BYTE = new Types(
            "http://www.w3.org/2001/XMLSchema#byte");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#date</tt>
     */
    public static final Types XSD_DATE = new Types(
            "http://www.w3.org/2001/XMLSchema#date");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#dateTime</tt>
     */
    public static final Types XSD_DATETIME = new Types(
            "http://www.w3.org/2001/XMLSchema#dateTime");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#dayTimeDuration</tt>
     */
    public static final Types XSD_DAYTIMEDURATION = new Types(
            "http://www.w3.org/2001/XMLSchema#dayTimeDuration");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#decimal</tt>
     */
    public static final Types XSD_DECIMAL = new Types(
            "http://www.w3.org/2001/XMLSchema#decimal");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#double</tt>
     */
    public static final Types XSD_DOUBLE = new Types(
            "http://www.w3.org/2001/XMLSchema#double");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#duration</tt>
     */
    public static final Types XSD_DURATION = new Types(
            "http://www.w3.org/2001/XMLSchema#duration");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#float</tt>
     */
    public static final Types XSD_FLOAT = new Types(
            "http://www.w3.org/2001/XMLSchema#float");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#gDay</tt>
     */
    public static final Types XSD_GDAY = new Types(
            "http://www.w3.org/2001/XMLSchema#gDay");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#gMonth</tt>
     */
    public static final Types XSD_GMONTH = new Types(
            "http://www.w3.org/2001/XMLSchema#gMonth");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#gMonthDay</tt>
     */
    public static final Types XSD_GMONTHDAY = new Types(
            "http://www.w3.org/2001/XMLSchema#gMonthDay");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#gYear</tt>
     */
    public static final Types XSD_GYEAR = new Types(
            "http://www.w3.org/2001/XMLSchema#gYear");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#gYearMonth</tt>
     */
    public static final Types XSD_GYEARMONTH = new Types(
            "http://www.w3.org/2001/XMLSchema#gYearMonth");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#hexBinary</tt>
     */
    public static final Types XSD_HEXBINARY = new Types(
            "http://www.w3.org/2001/XMLSchema#hexBinary");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#int</tt>
     */
    public static final Types XSD_INT = new Types(
            "http://www.w3.org/2001/XMLSchema#int");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#integer</tt>
     */
    public static final Types XSD_INTEGER = new Types(
            "http://www.w3.org/2001/XMLSchema#integer");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#language</tt>
     */
    public static final Types XSD_LANGUAGE = new Types(
            "http://www.w3.org/2001/XMLSchema#language");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#long</tt>
     */
    public static final Types XSD_LONG = new Types(
            "http://www.w3.org/2001/XMLSchema#long");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#Name</tt>
     */
    public static final Types XSD_NAME = new Types(
            "http://www.w3.org/2001/XMLSchema#Name");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#NCName</tt>
     */
    public static final Types XSD_NCNAME = new Types(
            "http://www.w3.org/2001/XMLSchema#NCName");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#negativeInteger</tt>
     */
    public static final Types XSD_NEGATIVEINTEGER = new Types(
            "http://www.w3.org/2001/XMLSchema#negativeInteger");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#NMTOKEN</tt>
     */
    public static final Types XSD_NMTOKEN = new Types(
            "http://www.w3.org/2001/XMLSchema#NMTOKEN");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#nonNegativeInteger</tt>
     */
    public static final Types XSD_NONNEGATIVEINTEGER = new Types(
            "http://www.w3.org/2001/XMLSchema#nonNegativeInteger");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#nonPositiveInteger</tt>
     */
    public static final Types XSD_NONPOSITIVEINTEGER = new Types(
            "http://www.w3.org/2001/XMLSchema#nonPositiveInteger");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#normalizedString</tt>
     */
    public static final Types XSD_NORMALIZEDSTRING = new Types(
            "http://www.w3.org/2001/XMLSchema#normalizedString");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#positiveInteger</tt>
     */
    public static final Types XSD_POSITIVEINTEGER = new Types(
            "http://www.w3.org/2001/XMLSchema#positiveInteger");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#short</tt>
     */
    public static final Types XSD_SHORT = new Types(
            "http://www.w3.org/2001/XMLSchema#short");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#string</tt>
     */
    public static final Types XSD_STRING = new Types(
            "http://www.w3.org/2001/XMLSchema#string");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#time</tt>
     */
    public static final Types XSD_TIME = new Types(
            "http://www.w3.org/2001/XMLSchema#time");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#token</tt>
     */
    public static final Types XSD_TOKEN = new Types(
            "http://www.w3.org/2001/XMLSchema#token");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#unsignedByte</tt>
     */
    public static final Types XSD_UNSIGNEDBYTE = new Types(
            "http://www.w3.org/2001/XMLSchema#unsignedByte");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#unsignedInt</tt>
     */
    public static final Types XSD_UNSIGNEDINT = new Types(
            "http://www.w3.org/2001/XMLSchema#unsignedInt");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#unsignedLong</tt>
     */
    public static final Types XSD_UNSIGNEDLONG = new Types(
            "http://www.w3.org/2001/XMLSchema#unsignedLong");

    /**
     * <tt>http://www.w3.org/2001/XMLSchema#unsignedShort</tt>
     */
    public static final Types XSD_UNSIGNEDSHORT = new Types(
            "http://www.w3.org/2001/XMLSchema#unsignedShort");

    private static final Set<IRI> ALL_TYPES;

    static {
        Set<IRI> tempTypes = new LinkedHashSet<>();
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

    private final IRI field;

    private Types(String field) {
        this.field = new IRIImpl(field);
    }

    @Override
    public String getIRIString() {
        return this.field.getIRIString();
    }

    @Override
    public String ntriplesString() {
        return this.field.ntriplesString();
    }

    @Override
    public boolean equals(Object other) {
        return this.field.equals(other);
    }

    @Override
    public int hashCode() {
        return this.field.hashCode();
    }

    @Override
    public String toString() {
        return this.field.toString();
    }

    /**
     * Get an immutable set of the IRIs used by the RDF-1.1 specification to
     * define types, from the RDF and XML Schema vocabularies.
     *
     * @return A {@link Set} containing all of the IRIs in this collection.
     */
    public static Set<IRI> values() {
        return ALL_TYPES;
    }

    /**
     * Get the IRI from this collection if it is present, or return
     * {@link Optional#empty()} otherwise.
     *
     * @param nextIRI The IRI to look for.
     * @return An {@link Optional} containing the IRI from this collection or
     * {@link Optional#empty()} if it is not present here.
     */
    public static Optional<IRI> get(IRI nextIRI) {
        if (ALL_TYPES.contains(nextIRI)) {
            // If we know about this IRI, then look through our set to find the
            // object that matches and return it
            for (IRI nextType : ALL_TYPES) {
                if (nextType.equals(nextIRI)) {
                    return Optional.of(nextType);
                }
            }
        }
        return Optional.empty();
    }
}
