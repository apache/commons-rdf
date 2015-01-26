/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.commonsrdf.simple;

import java.util.Optional;

import com.github.commonsrdf.api.IRI;

/**
 * Types from the RDF and XML Schema vocabularies.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public enum Types implements IRI {

	/** <tt>http://www.w3.org/1999/02/22-rdf-syntax-ns#HTML</tt> */
	RDF_HTML("http://www.w3.org/1999/02/22-rdf-syntax-ns#HTML"),

	/** <tt>http://www.w3.org/1999/02/22-rdf-syntax-ns#langString</tt> */
	RDF_LANGSTRING("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString"),

	/**
	 * <tt>http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral</tt>
	 * 
	 * @deprecated Not used in RDF-1.1
	 */
	@Deprecated
	RDF_PLAINLITERAL("http://www.w3.org/1999/02/22-rdf-syntax-ns#PlainLiteral"),

	/** <tt>http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral</tt> */
	RDF_XMLLITERAL("http://www.w3.org/1999/02/22-rdf-syntax-ns#XMLLiteral"),

	/** <tt>http://www.w3.org/2001/XMLSchema#anyURI</tt> */
	XSD_ANYURI("http://www.w3.org/2001/XMLSchema#anyURI"),

	/** <tt>http://www.w3.org/2001/XMLSchema#base64Binary</tt> */
	XSD_BASE64BINARY("http://www.w3.org/2001/XMLSchema#base64Binary"),

	/** <tt>http://www.w3.org/2001/XMLSchema#boolean</tt> */
	XSD_BOOLEAN("http://www.w3.org/2001/XMLSchema#boolean"),

	/** <tt>http://www.w3.org/2001/XMLSchema#byte</tt> */
	XSD_BYTE("http://www.w3.org/2001/XMLSchema#byte"),

	/** <tt>http://www.w3.org/2001/XMLSchema#date</tt> */
	XSD_DATE("http://www.w3.org/2001/XMLSchema#date"),

	/** <tt>http://www.w3.org/2001/XMLSchema#dateTime</tt> */
	XSD_DATETIME("http://www.w3.org/2001/XMLSchema#dateTime"),

	/** <tt>http://www.w3.org/2001/XMLSchema#dayTimeDuration</tt> */
	XSD_DAYTIMEDURATION("http://www.w3.org/2001/XMLSchema#dayTimeDuration"),

	/** <tt>http://www.w3.org/2001/XMLSchema#decimal</tt> */
	XSD_DECIMAL("http://www.w3.org/2001/XMLSchema#decimal"),

	/** <tt>http://www.w3.org/2001/XMLSchema#double</tt> */
	XSD_DOUBLE("http://www.w3.org/2001/XMLSchema#double"),

	/** <tt>http://www.w3.org/2001/XMLSchema#duration</tt> */
	XSD_DURATION("http://www.w3.org/2001/XMLSchema#duration"),

	/** <tt>http://www.w3.org/2001/XMLSchema#float</tt> */
	XSD_FLOAT("http://www.w3.org/2001/XMLSchema#float"),

	/** <tt>http://www.w3.org/2001/XMLSchema#gDay</tt> */
	XSD_GDAY("http://www.w3.org/2001/XMLSchema#gDay"),

	/** <tt>http://www.w3.org/2001/XMLSchema#gMonth</tt> */
	XSD_GMONTH("http://www.w3.org/2001/XMLSchema#gMonth"),

	/** <tt>http://www.w3.org/2001/XMLSchema#gMonthDay</tt> */
	XSD_GMONTHDAY("http://www.w3.org/2001/XMLSchema#gMonthDay"),

	/** <tt>http://www.w3.org/2001/XMLSchema#gYear</tt> */
	XSD_GYEAR("http://www.w3.org/2001/XMLSchema#gYear"),

	/** <tt>http://www.w3.org/2001/XMLSchema#gYearMonth</tt> */
	XSD_GYEARMONTH("http://www.w3.org/2001/XMLSchema#gYearMonth"),

	/** <tt>http://www.w3.org/2001/XMLSchema#hexBinary</tt> */
	XSD_HEXBINARY("http://www.w3.org/2001/XMLSchema#hexBinary"),

	/** <tt>http://www.w3.org/2001/XMLSchema#int</tt> */
	XSD_INT("http://www.w3.org/2001/XMLSchema#int"),

	/** <tt>http://www.w3.org/2001/XMLSchema#integer</tt> */
	XSD_INTEGER("http://www.w3.org/2001/XMLSchema#integer"),

	/** <tt>http://www.w3.org/2001/XMLSchema#language</tt> */
	XSD_LANGUAGE("http://www.w3.org/2001/XMLSchema#language"),

	/** <tt>http://www.w3.org/2001/XMLSchema#long</tt> */
	XSD_LONG("http://www.w3.org/2001/XMLSchema#long"),

	/** <tt>http://www.w3.org/2001/XMLSchema#Name</tt> */
	XSD_NAME("http://www.w3.org/2001/XMLSchema#Name"),

	/** <tt>http://www.w3.org/2001/XMLSchema#NCName</tt> */
	XSD_NCNAME("http://www.w3.org/2001/XMLSchema#NCName"),

	/** <tt>http://www.w3.org/2001/XMLSchema#negativeInteger</tt> */
	XSD_NEGATIVEINTEGER("http://www.w3.org/2001/XMLSchema#negativeInteger"),

	/** <tt>http://www.w3.org/2001/XMLSchema#NMTOKEN</tt> */
	XSD_NMTOKEN("http://www.w3.org/2001/XMLSchema#NMTOKEN"),

	/** <tt>http://www.w3.org/2001/XMLSchema#nonNegativeInteger</tt> */
	XSD_NONNEGATIVEINTEGER(
			"http://www.w3.org/2001/XMLSchema#nonNegativeInteger"),

	/** <tt>http://www.w3.org/2001/XMLSchema#nonPositiveInteger</tt> */
	XSD_NONPOSITIVEINTEGER(
			"http://www.w3.org/2001/XMLSchema#nonPositiveInteger"),

	/** <tt>http://www.w3.org/2001/XMLSchema#normalizedString</tt> */
	XSD_NORMALIZEDSTRING("http://www.w3.org/2001/XMLSchema#normalizedString"),

	/** <tt>http://www.w3.org/2001/XMLSchema#positiveInteger</tt> */
	XSD_POSITIVEINTEGER("http://www.w3.org/2001/XMLSchema#positiveInteger"),

	/** <tt>http://www.w3.org/2001/XMLSchema#short</tt> */
	XSD_SHORT("http://www.w3.org/2001/XMLSchema#short"),

	/** <tt>http://www.w3.org/2001/XMLSchema#string</tt> */
	XSD_STRING("http://www.w3.org/2001/XMLSchema#string"),

	/** <tt>http://www.w3.org/2001/XMLSchema#time</tt> */
	XSD_TIME("http://www.w3.org/2001/XMLSchema#time"),

	/** <tt>http://www.w3.org/2001/XMLSchema#token</tt> */
	XSD_TOKEN("http://www.w3.org/2001/XMLSchema#token"),

	/** <tt>http://www.w3.org/2001/XMLSchema#unsignedByte</tt> */
	XSD_UNSIGNEDBYTE("http://www.w3.org/2001/XMLSchema#unsignedByte"),

	/** <tt>http://www.w3.org/2001/XMLSchema#unsignedInt</tt> */
	XSD_UNSIGNEDINT("http://www.w3.org/2001/XMLSchema#unsignedInt"),

	/** <tt>http://www.w3.org/2001/XMLSchema#unsignedLong</tt> */
	XSD_UNSIGNEDLONG("http://www.w3.org/2001/XMLSchema#unsignedLong"),

	/** <tt>http://www.w3.org/2001/XMLSchema#unsignedShort</tt> */
	XSD_UNSIGNEDSHORT("http://www.w3.org/2001/XMLSchema#unsignedShort"),

	;

	private final IRI field;

	Types(String field) {
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
	
	public static Optional<IRI> get(IRI nextIRI) {
		for(IRI nextType : values()) {
			if(nextType.equals(nextIRI)) {
				return Optional.of(nextType); 
			}
		}
		return Optional.empty();
	}
}
