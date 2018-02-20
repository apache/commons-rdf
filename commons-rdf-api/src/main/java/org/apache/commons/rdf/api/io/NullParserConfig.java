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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.io.ParserConfig.ImmutableParserConfig;

class NullParserConfig implements ImmutableParserConfig, Serializable {
	
	private static final long serialVersionUID = 1L;

	@Override
	public Optional<ParserSource> source() {
		return Optional.empty();
	}

	@Override
	public Optional<IRI> base() {
		return Optional.empty();
	}

	@Override
	public Optional<ParserTarget> target() {
		return Optional.empty();
	}

	@Override
	public Optional<RDFSyntax> syntax() {
		return Optional.empty();
	}

	@Override
	public Optional<RDF> rdf() {
		return Optional.empty();
	}

	@Override
	public Map<Option, Object> options() {
		// Always a fresh map, so that our children can
		// mutate it on the fly
		return new HashMap<>();
	}

	@Override
	public ParserConfig withSyntax(RDFSyntax syntax) {
		return new WithSyntax(this, syntax);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ParserConfig withSource(ParserSource source) {
		return new WithSource(this, source);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ParserConfig withTarget(ParserTarget target) {
		return new WithTarget(this, target);
	}

	@Override
	public ParserConfig withRDF(RDF rdf) {
		return new WithRDF(this, rdf);
	}

	@Override
	public ParserConfig withBase(IRI base) {
		return new WithBase(this, base);
	}

	@Override
	public <V> ParserConfig withOption(Option<V> o, V v) {
		return new WithOption(this, o, v);
	}

	@SuppressWarnings("rawtypes")
	private static final class WithOption extends WithParent {

		private Option o;
		private Object v;

		public <V> WithOption(final ImmutableParserConfig parent, final Option<V> o, V v) {
			super(parent);
			this.o = o;
			this.v = v;
		}

		@Override
		public Map<Option, Object> options() {
			// Add to parent options
			Map options = super.options();
			if (v == null) {
				options.remove(o);
			} else {
				options.put(o, v);
			}
			return options;
		}
	}

	private static final class WithBase extends WithParent {
		private final IRI base;

		WithBase(final ImmutableParserConfig parent, final IRI base) {
			super(parent);
			this.base = base;
		}

		@Override
		public Optional<IRI> base() {
			return Optional.ofNullable(base);
		}
	}

	private static final class WithRDF extends WithParent {
		private final RDF rdf;

		WithRDF(final ImmutableParserConfig parent, final RDF rdf) {
			super(parent);
			this.rdf = rdf;
		}

		@Override
		public Optional<RDF> rdf() {
			return Optional.ofNullable(rdf);
		}
	}

	@SuppressWarnings("rawtypes")
	private static final class WithTarget extends WithParent {
		private final ParserTarget target;

		WithTarget(final ImmutableParserConfig parent, final ParserTarget target) {
			super(parent);
			this.target = target;
		}

		@Override
		public Optional<ParserTarget> target() {
			return Optional.ofNullable(target);
		}
	}

	@SuppressWarnings("rawtypes")
	private static final class WithSource extends WithParent {
		private final ParserSource source;

		WithSource(ImmutableParserConfig parent, ParserSource source) {
			super(parent);
			this.source = source;
		}

		@Override
		public Optional<ParserSource> source() {
			return Optional.ofNullable(source);
		}
	}

	private static final class WithSyntax extends WithParent {
		private final RDFSyntax syntax;

		WithSyntax(ImmutableParserConfig parent, RDFSyntax syntax) {
			super(parent);
			this.syntax = syntax;
		}

		@Override
		public Optional<RDFSyntax> syntax() {
			return Optional.ofNullable(syntax);
		}
	}

	abstract static class WithParent extends NullParserConfig implements ImmutableParserConfig {
		private final ImmutableParserConfig parent;

		/**
		 * Override which object to use by Serializable, avoiding
		 * serialization of the whole WithParent tree. 
		 * 
		 * This method is protected so it will be invoked for all subclasses of
		 * WithParent.
		 * 
		 * @return a {@link SnapshotParserConfig}
		 * @throws ObjectStreamException
		 */
		protected Object writeReplace() throws ObjectStreamException {
			return new SnapshotParserConfig(this);
		}
		
		WithParent(ImmutableParserConfig parserConfig) {
			this.parent = Objects.requireNonNull(parserConfig);
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Optional<ParserSource> source() {
			return parent.source();
		}

		@Override
		public Optional<IRI> base() {
			return parent.base();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Optional<ParserTarget> target() {
			return parent.target();
		}

		@Override
		public Optional<RDFSyntax> syntax() {
			return parent.syntax();
		}

		@Override
		public Optional<RDF> rdf() {
			return parent.rdf();
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Map<Option, Object> options() {
			return parent.options();
		}
	}

	@SuppressWarnings("rawtypes")
	final static class SnapshotParserConfig extends NullParserConfig {
		private static final long serialVersionUID = 1L;
		private final RDF rdf;
		private final RDFSyntax syntax;
		private final IRI base;
		private final ParserSource source;
		private final ParserTarget target;
		private final Map<Option, Object> options;
		private final ExecutorService executor;

		SnapshotParserConfig(ParserConfig old) {
			this(
				old.rdf().orElse(null),
				old.syntax().orElse(null),
				old.base().orElse(null),
				old.source().orElse(null),
				old.target().orElse(null),
				old.options(),
				null);
		}
		
		SnapshotParserConfig(RDF rdf, RDFSyntax syntax, IRI base, ParserSource source, ParserTarget target, Map<Option, Object> options, 
				ExecutorService executor) {
			this.rdf = rdf;
			this.syntax = syntax;
			this.base = base;
			this.source = source;
			this.target = target;
			// We'll make a copy
			this.options = Collections.unmodifiableMap(new HashMap<Option, Object>(options));
			this.executor = executor;				
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
			// return a mutable copy so our children can build on it
			return new HashMap<Option,Object>(options);
		}
	}	
}
