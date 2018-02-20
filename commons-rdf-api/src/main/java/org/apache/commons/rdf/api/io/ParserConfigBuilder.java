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
import java.nio.file.Path;
import java.util.Optional;
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
import org.apache.commons.rdf.api.fluentparser.OptionalTarget;
import org.apache.commons.rdf.api.fluentparser.Sync;
import org.apache.commons.rdf.api.io.ParserConfig.ImmutableParserConfig;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class ParserConfigBuilder implements ParserBuilder, NeedTargetOrRDF, 
		NeedSourceOrBase, NeedSourceBased, OptionalTarget, Sync, Async {

	public ParserConfigBuilder(ParserConfig mutated) {
		this.config = mutated;
	}	
	private final ParserConfig config;
	private ParserConfigBuilder mutate(ParserConfig mutated) {
		if (mutated == config) {
			// We're mutable (or nothing changed)
			return this;
		} else {
			return new ParserConfigBuilder(mutated);
		}
	}
	
	public ParserConfig buildConfig() {
		return config.asImmutableConfig();
	}

	@Override
	public NeedSourceOrBase target(Dataset dataset) {
		return target(ParserTarget.toDataset(dataset));

	}

	@Override
	public NeedSourceOrBase<Graph> target(Graph graph) {
		return target(ParserTarget.toGraph(graph));
	}

	@Override
	public <T> NeedSourceOrBase<T> target(ParserTarget<T> target) {
		return mutate(config.withTarget(target));
	}

	@Override
	public ParserConfigBuilder base(IRI iri) {
		return mutate(config.withBase(iri));
	}

	@Override
	public ParserConfigBuilder base(String iriStr) {
		return base(new IRIImpl(iriStr));
	}

	@Override
	public Sync source(final IRI iri) {
		return source(ParserSource.fromIRI(iri));
	}

	public Sync source(Path path) {
		return source(ParserSource.fromPath(path));
	}

	@Override
	public OptionalTarget<Dataset> rdf(RDF rdf) {
		return mutate(config.withRDF(rdf));
	}

	@Override
	public Sync source(ParserSource source) {
		return mutate(config.withSource(source));
	}

	@Override
	public Sync source(String iri) {
		return source(new IRIImpl(iri));
	}

	@Override
	public ParserConfigBuilder option(Option option, Object value) {
		return mutate(config.withOption(option, value));
	}

	@Override
	public Sync source(InputStream is) {
		return source(ParserSource.fromInputStream(is));
	}

	@Override
	public Parsed parse() {
		ImmutableParserConfig c = config.asImmutableConfig();
		Parser parser = getParserOrFail(c);
		return parser.parse(c);
	}
	
	private static Parser getParserOrFail(ImmutableParserConfig c) {
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

	@Override
	public Future parseAsync() {
		ImmutableParserConfig c = config.asImmutableConfig();
		Parser parser = getParserOrFail(c);
		return parser.parseAsync(c);		
	}

	@Override
	public Async async() {
		return this;
	}

	@Override
	public ParserConfigBuilder build() {
		return mutate(config.asImmutableConfig());
	}

	@Override
	public NeedTargetOrRDF syntax(RDFSyntax syntax) {
		return mutate(config.withSyntax(syntax));
	}

}
