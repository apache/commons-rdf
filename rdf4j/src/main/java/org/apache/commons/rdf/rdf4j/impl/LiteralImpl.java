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
package org.apache.commons.rdf.rdf4j.impl;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.rdf.rdf4j.RDF4JLiteral;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;
import org.eclipse.rdf4j.rio.turtle.TurtleUtil;

public final class LiteralImpl 
	extends AbstractRDFTerm<org.eclipse.rdf4j.model.Literal>
    implements RDF4JLiteral {		

	private static final String QUOTE = "\"";
	
	public LiteralImpl(org.eclipse.rdf4j.model.Literal literal) {
		super(literal);			
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == this) { return true; }
		if (obj instanceof org.apache.commons.rdf.api.Literal) {
			org.apache.commons.rdf.api.Literal other = (org.apache.commons.rdf.api.Literal) obj;
			return getLexicalForm().equals(other.getLexicalForm()) &&
					getDatatype().equals(other.getDatatype()) && 
					getLanguageTag().equals(other.getLanguageTag());
			
		}
		return false;
	}

	@Override
	public org.apache.commons.rdf.api.IRI getDatatype() {
		return new IRIImpl(value.getDatatype());
	}

	@Override
	public Optional<String> getLanguageTag() {
		return value.getLanguage();
	}

	@Override
	public String getLexicalForm() {
		return value.getLabel();
	}

	public int hashCode() {
		return Objects.hash(value.getLabel(), value.getDatatype(), value.getLanguage());
	}

	@Override
	public String ntriplesString() {
		// TODO: Use a more efficient StringBuffer
		String escaped = QUOTE + TurtleUtil.encodeString(value.getLabel()) + QUOTE;
		if (value.getLanguage().isPresent()) {
			return escaped + "@" + value.getLanguage().get();
		}
		if (value.getDatatype().equals(XMLSchema.STRING)) { 
			return escaped;
		}
		return escaped + "^^<" + TurtleUtil.encodeURIString(value.getDatatype().toString()) + ">";
	}

	@Override
	public String toString() {
		return ntriplesString();
	}
}