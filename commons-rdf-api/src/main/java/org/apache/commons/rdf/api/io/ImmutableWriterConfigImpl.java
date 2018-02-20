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
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.io.WriterConfig.ImmutableWriterConfig;

class ImmutableWriterConfigImpl implements ImmutableWriterConfig, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Optional<WriterSource> source() {
		return Optional.empty();
	}

	@Override
	public Optional<WriterTarget> target() {
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

	@SuppressWarnings("rawtypes")
	@Override
	public Map<Option, Object> options() {
		return Collections.emptyMap();
	}

	@Override
	public WriterConfig withSyntax(RDFSyntax syntax) {
		return new WithSyntax(this, syntax);
	}

	@Override
	public WriterConfig withSource(WriterSource source) {
		return new WithSource(this, source);
	}

	@Override
	public WriterConfig withTarget(WriterTarget target) {
		return new WithTarget(this, target);
	}

	@Override
	public WriterConfig withRDF(RDF rdf) {
		return new WithRDF(this, rdf);
	}

	@Override
	public <V> WriterConfig withOption(Option<V> o, V v) {
		return new WithOption(this, o, v);
	}

	static class WithParent extends ImmutableWriterConfigImpl implements ImmutableWriterConfig {
		private final ImmutableWriterConfig parent;

		/**
		 * Override which object to use by Serializable, avoiding
		 * serialization of the whole WithParent tree. 
		 * 
		 * This method is protected so it will be invoked for all subclasses of
		 * WithParent.
		 * 
		 * @return a {@link SnapshotWriterConfig}
		 * @throws ObjectStreamException
		 */
		protected Object writeReplace() throws ObjectStreamException {
			return new SnapshotWriterConfig(this);
		}		
		
		WithParent(ImmutableWriterConfig parent) {
			this.parent = parent;
		}

		@Override
		public Optional<WriterSource> source() {
			return parent.source();
		}

		@Override
		public Optional<WriterTarget> target() {
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

	static final class WithSyntax extends WithParent implements WriterConfig {
		private final RDFSyntax syntax;

		public WithSyntax(ImmutableWriterConfig parent, RDFSyntax syntax) {
			super(parent);
			this.syntax = syntax;
		}

		@Override
		public Optional<RDFSyntax> syntax() {
			return Optional.ofNullable(syntax);
		}
	}

	static final class WithSource extends WithParent implements WriterConfig {
		private final WriterSource source;

		public WithSource(ImmutableWriterConfig parent, WriterSource source) {
			super(parent);
			this.source = source;
		}

		@Override
		public Optional<WriterSource> source() {
			return Optional.ofNullable(source);
		}
	}

	static class WithTarget extends WithParent implements WriterConfig {
		private final WriterTarget target;

		public WithTarget(ImmutableWriterConfig parent, WriterTarget target) {
			super(parent);
			this.target = target;
		}

		@Override
		public Optional<WriterTarget> target() {
			return Optional.ofNullable(target);
		}
	}
	
	static class WithRDF extends WithParent implements WriterConfig {
		private final RDF rdf;

		public WithRDF(ImmutableWriterConfig parent, RDF rdf) {
			super(parent);
			this.rdf = rdf;
		}
		@Override
		public Optional<RDF> rdf() {
			return Optional.ofNullable(rdf);
		}

	}	

	@SuppressWarnings("rawtypes")
	static class WithOption extends WithParent implements WriterConfig {
		private Option o;
		private Object v;
		public <V> WithOption(ImmutableWriterConfig parent, Option<V> o, V v) {
			super(parent);
			this.o = o;
			this.v = v;
		}
		@Override
		public Map<Option, Object> options() {
			// Add to parent options
			Map<Option, Object> options = super.options();
			if (v == null) {
				options.remove(o);
			} else {
				options.put(o, v);
			}
			return options;			
		}
	}
	
	@SuppressWarnings("rawtypes")
	final static class SnapshotWriterConfig extends ImmutableWriterConfigImpl {
		private static final long serialVersionUID = 1L;
		private final RDF rdf;
		private final RDFSyntax syntax;
		private final WriterSource source;
		private final WriterTarget target;
		private final Map<Option, Object> options;

		SnapshotWriterConfig(WriterConfig old) {
			this(
				old.rdf().orElse(null),
				old.syntax().orElse(null),
				old.source().orElse(null),
				old.target().orElse(null),
				old.options(),
				null);
		}
		
		SnapshotWriterConfig(RDF rdf, RDFSyntax syntax, WriterSource source, WriterTarget target, Map<Option, Object> options, 
				ExecutorService executor) {
			this.rdf = rdf;
			this.syntax = syntax;
			this.source = source;
			this.target = target;
			// We'll make a copy
			this.options = Collections.unmodifiableMap(new HashMap<Option, Object>(options));
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

		@Override
		public Map<Option, Object> options() {
			// return a mutable copy so our children can build on it
			return new HashMap<Option,Object>(options);
		}
	}	
	
}