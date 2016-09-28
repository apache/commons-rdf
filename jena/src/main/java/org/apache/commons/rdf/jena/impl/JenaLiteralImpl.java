/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

package org.apache.commons.rdf.jena.impl;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.jena.JenaLiteral;
import org.apache.jena.graph.Node;

public class JenaLiteralImpl extends AbstractJenaRDFTerm implements JenaLiteral {

	/* package */ JenaLiteralImpl(Node node) {
		super(node);
		if (! node.isLiteral()) {
			throw new IllegalArgumentException("Node is not a literal: " + node);
		}		
	}

	@Override
	public boolean equals(Object other) {
		if (other == this)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof Literal))
			return false;
		Literal literal = (Literal) other;
		return getLexicalForm().equals(literal.getLexicalForm()) && getLanguageTag().equals(literal.getLanguageTag())
				&& getDatatype().equals(literal.getDatatype());
	}

	@Override
	public IRI getDatatype() {
		return JenaFactory.createIRI(asJenaNode().getLiteralDatatype().getURI());
	}

	@Override
	public Optional<String> getLanguageTag() {
		String x = asJenaNode().getLiteralLanguage();
		if (x == null || x.isEmpty())
			return Optional.empty();
		return Optional.of(x);
	}

	@Override
	public String getLexicalForm() {
		return asJenaNode().getLiteralLexicalForm();
	}

	@Override
	public int hashCode() {
		return Objects.hash(getLexicalForm(), getDatatype(), getLanguageTag());
	}
}
