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
package com.github.commonsrdf.dummyimpl;

import java.util.Optional;

import com.github.commonsrdf.api.IRI;
import com.github.commonsrdf.api.Literal;

public class LiteralImpl implements Literal {

	private static final String QUOTE = "\"";
	private static final IRIImpl RDF_LANG_STRING = new IRIImpl(
			"http://www.w3.org/1999/02/22-rdf-syntax-ns#langString");
	private static final IRIImpl XSD_STRING = new IRIImpl(
			"http://www.w3.org/2001/XMLSchema#string");

	private IRI dataType;
	private Optional<String> languageTag;
	private String lexicalForm;

	public LiteralImpl(String literal) {
		if (literal == null) {
			throw new NullPointerException("literal can't be null");
		}
		this.lexicalForm = literal;
		this.dataType = XSD_STRING;
		this.languageTag = Optional.empty();
	}

	public LiteralImpl(String lexicalForm, IRI dataType) {
		if (lexicalForm == null) {
			throw new NullPointerException("lexicalForm can't be null");
		}
		this.lexicalForm = lexicalForm;
		if (dataType == null) {
			throw new NullPointerException("dataType can't be null");
		}
		this.dataType = dataType;
		this.languageTag = Optional.empty();
	}

	public LiteralImpl(String literal, String languageTag) {
		if (literal == null) {
			throw new NullPointerException("literal can't be null");
		}
		this.lexicalForm = literal;
		this.languageTag = Optional.of(languageTag);
		if (languageTag.isEmpty()) {
			// TODO: Check against
			// http://www.w3.org/TR/n-triples/#n-triples-grammar
			throw new IllegalArgumentException("Language tag can't be null");
		}
		this.dataType = RDF_LANG_STRING;
	}

	@Override
	public IRI getDatatype() {
		return dataType;
	}

	@Override
	public Optional<String> getLanguageTag() {
		return languageTag;
	}

	@Override
	public String getLexicalForm() {
		return lexicalForm;
	}

	@Override
	public String ntriplesString() {
		StringBuilder sb = new StringBuilder();
		sb.append(QUOTE);
		// Escape special characters
		sb.append(getLexicalForm().replace("\\", "\\\\"). // escaped to \\
				replace("\"", "\\\""). // escaped to \"
				replace("\r", "\\r"). // escaped to \r
				replace("\n", "\\n")); // escaped to \n
		sb.append(QUOTE);

		// getLanguageTag().ifPresent(s -> sb.append("@" + s));
		if (getLanguageTag().isPresent()) {
			sb.append("@");
			sb.append(getLanguageTag().get());

		} else if (!getDatatype().equals(XSD_STRING)) {
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
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataType == null) ? 0 : dataType.hashCode());
		result = prime * result
				+ ((lexicalForm == null) ? 0 : lexicalForm.hashCode());
		result = prime * result
				+ ((languageTag == null) ? 0 : languageTag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof Literal)) {
			return false;
		}
		Literal literal = (Literal) obj;		
		return  getDatatype().equals(literal.getDatatype()) && 
				getLexicalForm().equals(literal.getLexicalForm()) && 
				getLanguageTag().equals(literal.getLanguageTag());
	}
	
	
}
