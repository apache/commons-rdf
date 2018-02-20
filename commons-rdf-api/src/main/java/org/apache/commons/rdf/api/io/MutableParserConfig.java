/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rdf.api.io;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;

final class MutableParserConfig implements Cloneable, Serializable, ParserConfig {
	private static final long serialVersionUID = 1L;
	private RDF rdf = null;
	private RDFSyntax syntax = null;
	private IRI base = null;
	@SuppressWarnings("rawtypes")
	private ParserSource source = null;
	@SuppressWarnings("rawtypes")
	private ParserTarget target = null;
	private final Map<Option, Object> options = new HashMap<>();

	public MutableParserConfig() {
	}

	public MutableParserConfig(ParserConfig old) {
		rdf = old.rdf().orElse(null);
		syntax = old.syntax().orElse(null);
		base = old.base().orElse(null);
		source = old.source().orElse(null);
		target = old.target().orElse(null);
		options.putAll(old.options());
	}

	@Override
	protected Object clone() {
		return new MutableParserConfig(this);
	}

	@Override
	public Optional<ParserSource> source() {
		return Optional.ofNullable(source);
	}

	@Override
	public Optional<IRI> base() {
		return Optional.ofNullable(base);
	}

	@Override
	public Optional<ParserTarget> target() {
		return Optional.ofNullable(target);
	}

	@Override
	public Optional<RDFSyntax> syntax() {
		return Optional.ofNullable(syntax);
	}

	@Override
	public Optional<RDF> rdf() {
		return Optional.ofNullable(rdf);
	}

	@Override
	public Map<Option, Object> options() {
		return options;
	}

	public ParserConfig withSyntax(RDFSyntax syntax) {
		this.syntax = syntax;
		return this;
	}

	@SuppressWarnings("rawtypes")
	public ParserConfig withSource(ParserSource source) {
		this.source = source;
		return this;
	}

	public ParserConfig withTarget(ParserTarget target) {
		this.target = target;
		return this;
	}

	public ParserConfig withRDF(RDF rdf) {
		this.rdf = rdf;
		return this;
	}

	public ParserConfig withBase(IRI base) {
		this.base = base;
		return this;
	}

	public <V> ParserConfig withOption(Option<V> o, V v) {
		this.options.put(o, v);
		return this;
	}

}