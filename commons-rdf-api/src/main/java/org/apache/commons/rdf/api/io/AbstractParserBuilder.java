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

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
public final class AbstractParserBuilder implements Cloneable, Serializable, NeedTargetOrRDF, NeedTargetOrRDFOrSyntax,
		NeedSourceOrBase, NeedSourceBased, OptionalTarget, Sync, Async {

	private static final long serialVersionUID = 1L;


    private static final ThreadGroup THEAD_GROUP = new ThreadGroup("Commons RDF parsers");
    private static final ExecutorService DEFAULT_EXECUTOR = Executors.newCachedThreadPool(r -> new Thread(THEAD_GROUP, r));
	
	public AbstractParserBuilder(RDF rdf) {
		config.withRDF(rdf);
	}
	
	@Override
	public AbstractParserBuilder clone() {
		try {
			AbstractParserBuilder c = (AbstractParserBuilder) super.clone();
			c.config = (ParserConfigImpl) config.clone();
			return c;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("AbstractParserBuilder was not Cloneable", e);
		}
	}

	private boolean mutable = false;
	private ParserConfigImpl config = new ParserConfigImpl();
	private ExecutorService executor = DEFAULT_EXECUTOR;

	@Override
	public NeedTargetOrRDF syntax(RDFSyntax syntax) {
		AbstractParserBuilder c = mutable();
		c.config.withSyntax(syntax);
		return c;
	}

	private AbstractParserBuilder mutable(boolean mutable) {
		if (this.mutable == mutable) {
			return this;
		} else {
			AbstractParserBuilder c = clone();
			c.mutable = mutable;
			return c;
		}
	}

	private AbstractParserBuilder mutable() {
		return mutable(true);
	}

	@Override
	public AbstractParserBuilder build() {
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
		AbstractParserBuilder c = mutable();
		c.config.withTarget(target);
		return c;
	}

	@Override
	public NeedSourceBased base(IRI iri) {
		AbstractParserBuilder c = mutable();
		c.config.withBase(iri);
		return c;
	}

	@Override
	public NeedSourceBased base(String iri) {
		AbstractParserBuilder c = mutable();
		c.config.withBase(new IRIImpl(iri));
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
		AbstractParserBuilder c = mutable();
		c.config.withRDF(rdf);
		return c;
	}

	@Override
	public Sync source(ParserSource source) {
		AbstractParserBuilder c = mutable();
		c.config.withSource(source);
		return c;
	}

	@Override
	public Sync source(String iri) {
		return source(new IRIImpl(iri));
	}

	@Override
	public AbstractParserBuilder option(Option option, Object value) {
		AbstractParserBuilder c = mutable();
		c.config.withOption(option, value);
		return c;
	}

	@Override
	public Future<Parsed> parseAsync() {
		// Ensure immutable
		AbstractParserBuilder frozen = mutable(false);
		Parser parser = getParserOrFail(frozen.config);		
		return frozen.executor.submit(() -> parser.parse(frozen.config));
	}

	@Override
	public Async async() {
		AbstractParserBuilder c = mutable();
		c.executor = DEFAULT_EXECUTOR;
		return c;
	}

	@Override
	public Async async(ExecutorService executor) {
		AbstractParserBuilder c = mutable();
		c.executor = executor;
		return c;
	}

	@Override
	public Sync source(InputStream is) {
		return source(new InputParserSource(is));
	}

	@Override
	public Parsed parse() {
		// ensure immutable copy of config
		ParserConfigImpl c = mutable(false).config;
		Parser parser = getParserOrFail(c);
		return parser.parse(c);
	}

	private Parser getParserOrFail(ParserConfigImpl c) {
		if (! c.rdf().isPresent()) {
			throw new IllegalStateException("ParserState has no RDF instance configured");
		}
		Optional<Parser> parser = c.rdf().get().parser(c.syntax().orElse(null));
		if (! parser.isPresent()) { 
			throw new IllegalStateException("Unsupported RDF syntax " 
					+ c.syntax().map(t -> t.name() ).orElse("(guess)"));
		}
		return parser.get();
	}

}
