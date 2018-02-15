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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.fluentparser.Async;
import org.apache.commons.rdf.api.fluentparser.NeedSourceBased;
import org.apache.commons.rdf.api.fluentparser.NeedSourceOrBase;
import org.apache.commons.rdf.api.fluentparser.NeedTargetOrRDF;
import org.apache.commons.rdf.api.fluentparser.NeedTargetOrRDFOrSyntax;
import org.apache.commons.rdf.api.fluentparser.OptionalTarget;
import org.apache.commons.rdf.api.fluentparser.Sync;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class AbstractParserFactory implements 
Cloneable, 
Serializable, 
NeedTargetOrRDF, 
NeedTargetOrRDFOrSyntax, 
NeedSourceOrBase,
NeedSourceBased, 
OptionalTarget, 
Sync, 
Async {

	private static final long serialVersionUID = 1L;

	@Override
	public AbstractParserFactory clone() {
		try {
			AbstractParserFactory c = (AbstractParserFactory) super.clone();
			c.config = (ParserConfigImpl) config.clone();
			return c;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("AbstractParserFactory was not Cloneable", e);
		}
	}

	static final class InputParserSource implements ParserSource {
		private final InputStream is;

		private InputParserSource(InputStream is) {
			this.is = is;
		}

		@Override
		public Object src() {
			return is;
		}

		@Override
		public InputStream inputStream() throws IOException {
			return is;
		}

		@Override
		public Optional iri() {
			return Optional.empty();
		}
	}

	private final class PathParserSource implements ParserSource<Path> {
		private final Path path;

		private PathParserSource(Path path) {
			this.path = path;
		}

		@Override
		public Path src() {
			return path;
		}

		@Override
		public InputStream inputStream() throws IOException {
			return Files.newInputStream(path);
		}

		@Override
		public Optional<IRI> iri() {
			final String uri = path.toUri().toString();
			return Optional.of(new IRIImpl(uri));
		}
	}

	private final class IRIParserSource implements ParserSource<IRI> {
		private final IRI iri;

		private IRIParserSource(IRI iri) {
			this.iri = iri;
		}

		@Override
		public IRI src() {
			return iri;
		}

		@Override
		public InputStream inputStream() throws IOException {
			return new URL(iri.getIRIString()).openStream();
		}

		@Override
		public Optional<IRI> iri() {
			return Optional.of(iri);
		}
	}

	private final class IRIImpl implements IRI {
		private final String uri;

		private IRIImpl(String uri) {
			this.uri = uri;
		}

		@Override
		public String ntriplesString() {
			return "<" + uri + ">";
		}

		@Override
		public String getIRIString() {
			return uri;
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof IRI) && ((IRI) obj).getIRIString().equals(uri);
		}

		@Override
		public int hashCode() {
			return uri.hashCode();
		}
	}

	public static final class ParserConfigImpl implements Cloneable, Serializable, ParserConfig {
		private static final long serialVersionUID = 1L;
		private RDF rdf = null;
		private RDFSyntax syntax = null;
		private IRI base = null;
		private ParserSource source = null;
		private ParserTarget target = null;
		final private  Map<Option, Object> options = new HashMap<>();

		public ParserConfigImpl() {
		}
		
		public ParserConfigImpl(ParserConfig old) {
			rdf = old.rdf().orElse(null);
			syntax = old.syntax().orElse(null);
			base = old.base().orElse(null);
			source = old.source().orElse(null);
			target = old.target().orElse(null);
			options.putAll(old.options());
		}

		@Override
		protected Object clone() {
			return new ParserConfigImpl(this);
		}

		@Override
		public Optional<ParserSource> source() {
			return Optional.of(source);
		}

		@Override
		public Optional<IRI> base() {
			return Optional.of(base);
		}

		@Override
		public Optional<ParserTarget> target() {
			return Optional.of(target);
		}

		@Override
		public Optional<RDFSyntax> syntax() {
			return Optional.of(syntax);
		}

		@Override
		public Optional<RDF> rdf() {
			return Optional.of(rdf);
		}

		@Override
		public Map<Option, Object> options() {
			return options;
		}
		
		
	}
	private boolean mutable = false;
	private ParserConfigImpl config = new ParserConfigImpl();

	@Override
	public NeedTargetOrRDF syntax(RDFSyntax syntax) {
		AbstractParserFactory c = mutable();
		c.config.syntax = syntax;
		return c;
	}

	private AbstractParserFactory mutable(boolean mutable) {
		if (this.mutable == mutable) {
			return this;
		} else {
			AbstractParserFactory c = clone();
			c.mutable = mutable;
			return c;
		}
	}

	private AbstractParserFactory mutable() {
		return mutable(true);
	}

	@Override
	public AbstractParserFactory build() {
		return mutable(false);
	}

	@Override
	public NeedSourceOrBase target(Dataset dataset) {
		return target(dataset::add);

	}

	@Override
	public NeedSourceOrBase<Graph> target(Graph graph) {
		return target(q -> {
			if (q.getGraphName().isPresent()) {
				// Only add if q is in default graph
				graph.add(q.asTriple());
			}
		});
	}

	@Override
	public <T> NeedSourceOrBase<T> target(ParserTarget<T> target) {
		AbstractParserFactory c = mutable();
		c.config.target = target;
		return c;
	}

	@Override
	public NeedSourceBased base(IRI iri) {
		AbstractParserFactory c = mutable();
		c.config.base = iri;
		return c;
	}

	@Override
	public NeedSourceBased base(String iri) {
		AbstractParserFactory c = mutable();
		c.config.base = new IRIImpl(iri);
		return c;
	}

	@Override
	public Sync source(final IRI iri) {
		return source(new IRIParserSource(iri));
	}

	public Sync source(Path path) {
		return source(new PathParserSource(path));
	}

	@Override
	public OptionalTarget<Dataset> rdf(RDF rdf) {
		AbstractParserFactory c = mutable();
		c.config.rdf = rdf;
		return c;
	}

	@Override
	public Sync source(ParserSource source) {
		AbstractParserFactory c = mutable();
		c.config.source = source;
		return c;		
	}

	@Override
	public Sync source(String iri) {
		return source(new IRIImpl(iri));
	}

	@Override
	public AbstractParserFactory option(Option option, Object value) {
		AbstractParserFactory c = mutable();
		c.config.options.put(option, value);
		return c;		
	}

	@Override
	public Future parseAsync() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Async async() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Async async(ExecutorService executor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parsed parse() {
		return null;
	}

	@Override
	public Sync source(InputStream is) {
		return source(new InputParserSource(is));
	}

}
