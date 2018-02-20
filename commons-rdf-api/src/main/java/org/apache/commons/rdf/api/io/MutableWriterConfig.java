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

import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;

final class MutableWriterConfig implements Serializable, WriterConfig {
	private static final long serialVersionUID = 1L;
	private RDF rdf = null;
	private RDFSyntax syntax = null;
	private WriterSource source = null;
	private WriterTarget target = null;
	@SuppressWarnings("rawtypes")
	private final Map<Option, Object> options = new HashMap<>();

	public MutableWriterConfig() {
	}

	public MutableWriterConfig(WriterConfig old) {
		rdf = old.rdf().orElse(null);
		syntax = old.syntax().orElse(null);
		source = old.source().orElse(null);
		target = old.target().orElse(null);
		options.putAll(old.options());
	}

	@Override
	public Optional<WriterSource> source() {
		return Optional.ofNullable(source);
	}

	@Override
	public Optional<WriterTarget> target() {
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

	@SuppressWarnings("rawtypes")
	@Override
	public Map<Option, Object> options() {
		return options;
	}

	public WriterConfig withSyntax(RDFSyntax syntax) {
		this.syntax = syntax;
		return this;
	}

	public WriterConfig withSource(WriterSource source) {
		this.source = source;
		return this;
	}

	public WriterConfig withTarget(WriterTarget target) {
		this.target = target;
		return this;
	}

	public WriterConfig withRDF(RDF rdf) {
		this.rdf = rdf;
		return this;
	}

	public <V> WriterConfig withOption(Option<V> o, V v) {
		this.options.put(o, v);
		return this;
	}

}