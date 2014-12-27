package com.github.commonsrdf.dummyimpl;

import java.util.Optional;

import com.github.commonsrdf.api.IRI;
import com.github.commonsrdf.api.Literal;

public class LiteralImpl implements Literal {

	private static final IRIImpl XSD_STRING = new IRIImpl("http://www.w3.org/2001/XMLSchema#string");
	private static final IRIImpl RDF_LANG_STRING = new IRIImpl("http://www.w3.org/1999/02/22-rdf-syntax-ns#langString");
	private static final String QUOTE = "\"";

	private Optional<String> languageTag;
	private IRI dataType;
	private String lexicalForm;
	
	public LiteralImpl(String literal) {
		if (literal == null) { 
			throw new NullPointerException("literal can't be null");
		}
		this.lexicalForm = literal;
		this.dataType = XSD_STRING;
		this.languageTag = Optional.empty();
	}
	
	public LiteralImpl(String literal, String languageTag) {
		if (literal == null) { 
			throw new NullPointerException("literal can't be null");
		}
		this.lexicalForm = literal;
		this.languageTag = Optional.of(languageTag);
		if (languageTag.isEmpty()) { 
			// TODO: Check against http://www.w3.org/TR/n-triples/#n-triples-grammar
			throw new IllegalArgumentException("Language tag can't be null");
		}
		this.dataType = RDF_LANG_STRING;		
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

	@Override
	public String ntriplesString() {
		StringBuilder sb = new StringBuilder();
		sb.append(QUOTE);
		// Escape special characters
		sb.append(getLexicalForm().
				replace("\\", "\\\\").  // escaped to \\
				replace("\"", "\\\"").  // escaped to \"
				replace("\r", "\\r").   // escaped to \r
				replace("\n", "\\n"));  // escaped to \n
		sb.append(QUOTE);

		//getLanguageTag().ifPresent(s -> sb.append("@" + s));
		if (getLanguageTag().isPresent()) {
			sb.append("@");
			sb.append(getLanguageTag().get());
		
		} else if (! getDatatype().getIRIString().equals(XSD_STRING.getIRIString())) { 
			sb.append("^^");
			sb.append(getDatatype().ntriplesString());
		}
		sb.append(QUOTE);
		return sb.toString();
	}

	@Override
	public String getLexicalForm() {
		return lexicalForm;
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
	public String toString() {
		return ntriplesString();
	}

}
