/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
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
package org.apache.commons.rdf.simple;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFParserBuilder;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.RDFTermFactory;

/**
 * Abstract RDFParserBuilder
 * <p>
 * This abstract class keeps the builder properties in protected fields like
 * {@link #sourceFile} using {@link Optional}. Some basic checking like
 * {@link #checkIsAbsolute(IRI)} is performed.
 * <p>
 * 
 */
public abstract class AbstractRDFParserBuilder implements RDFParserBuilder {

	private static final ThreadGroup threadGroup = new ThreadGroup("Commons RDF parsers");
	private static final ExecutorService threadpool = Executors.newCachedThreadPool(r -> new Thread(threadGroup, r));

	// Basically only used for creating IRIs
	private static RDFTermFactory internalRdfTermFactory = new SimpleRDFTermFactory();

	protected Optional<RDFTermFactory> rdfTermFactory = Optional.empty();
	protected Optional<RDFSyntax> contentTypeSyntax = Optional.empty();
	protected Optional<String> contentType = Optional.empty();
	protected Optional<Graph> intoGraph = Optional.empty();
	protected Optional<IRI> base = Optional.empty();
	protected Optional<InputStream> sourceInputStream = Optional.empty();
	protected Optional<Path> sourceFile = Optional.empty();
	protected Optional<IRI> sourceIri = Optional.empty();

	@Override
	public AbstractRDFParserBuilder clone() {
		try {
			return (AbstractRDFParserBuilder) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public RDFParserBuilder rdfTermFactory(RDFTermFactory rdfTermFactory) {
		AbstractRDFParserBuilder c = clone();
		c.rdfTermFactory = Optional.ofNullable(rdfTermFactory);
		return c;
	}

	@Override
	public RDFParserBuilder contentType(RDFSyntax rdfSyntax) throws IllegalArgumentException {
		AbstractRDFParserBuilder c = clone();
		c.contentTypeSyntax = Optional.ofNullable(rdfSyntax);
		c.contentType = c.contentTypeSyntax.map(syntax -> syntax.mediaType);
		return c;
	}

	@Override
	public RDFParserBuilder contentType(String contentType) {
		AbstractRDFParserBuilder c = clone();
		c.contentType = Optional.ofNullable(contentType);
		c.contentTypeSyntax = c.contentType.flatMap(RDFSyntax::byMediaType);
		return c;
	}

	@Override
	public RDFParserBuilder intoGraph(Graph graph) {
		AbstractRDFParserBuilder c = clone();
		c.intoGraph = Optional.ofNullable(graph);
		return c;
	}

	@Override
	public RDFParserBuilder base(IRI base) {
		AbstractRDFParserBuilder c = clone();
		c.base = Optional.ofNullable(base);
		c.base.ifPresent(i -> checkIsAbsolute(i));
		return c;
	}

	@Override
	public RDFParserBuilder base(String base) throws IllegalArgumentException {
		return base(internalRdfTermFactory.createIRI(base));
	}

	@Override
	public RDFParserBuilder source(InputStream inputStream) {
		AbstractRDFParserBuilder c = clone();
		c.resetSource();
		c.sourceInputStream = Optional.ofNullable(inputStream);
		return c;
	}

	@Override
	public RDFParserBuilder source(Path file) {
		AbstractRDFParserBuilder c = clone();
		c.resetSource();
		c.sourceFile = Optional.ofNullable(file);
		return c;
	}

	@Override
	public RDFParserBuilder source(IRI iri) {
		AbstractRDFParserBuilder c = clone();
		c.resetSource();
		c.sourceIri = Optional.ofNullable(iri);
		c.sourceIri.ifPresent(i -> checkIsAbsolute(i));
		return c;
	}

	@Override
	public RDFParserBuilder source(String iri) throws IllegalArgumentException {
		AbstractRDFParserBuilder c = clone();
		c.resetSource();
		c.sourceIri = Optional.ofNullable(iri).map(internalRdfTermFactory::createIRI);
		c.sourceIri.ifPresent(i -> checkIsAbsolute(i));
		return source(internalRdfTermFactory.createIRI(iri));
	}

	/**
	 * Check if an iri is absolute.
	 * <p>
	 * Used by {@link #source(String)} and {@link #base(String)}
	 * 
	 * @param iri
	 */
	protected void checkIsAbsolute(IRI iri) {
		if (!URI.create(iri.getIRIString()).isAbsolute()) {
			throw new IllegalArgumentException("IRI is not absolute: " + iri);
		}
	}

	/**
	 * Check that one and only one source is present and valid.
	 * <p>
	 * Used by {@link #parse()}.
	 * 
	 * @param iri
	 * @throws IOException
	 */
	protected void checkSource() throws IOException {
		if (!sourceFile.isPresent() && !sourceInputStream.isPresent() && !sourceIri.isPresent()) {
			throw new IllegalStateException("No source has been set");
		}
		if (sourceIri.isPresent() && sourceInputStream.isPresent()) {
			throw new IllegalStateException("Both sourceIri and sourceInputStream have been set");
		}
		if (sourceIri.isPresent() && sourceFile.isPresent()) {
			throw new IllegalStateException("Both sourceIri and sourceFile have been set");
		}
		if (sourceInputStream.isPresent() && sourceFile.isPresent()) {
			throw new IllegalStateException("Both sourceInputStream and sourceFile have been set");
		}
		if (sourceFile.isPresent() && !sourceFile.filter(Files::isReadable).isPresent()) {
			throw new IOException("Can't read file: " + sourceFile);
		}
	}

	/**
	 * Check base, if required.
	 */
	protected void checkBaseRequired() {
		if (!base.isPresent() && sourceInputStream.isPresent()
				&& !contentTypeSyntax.filter(t -> t == RDFSyntax.NQUADS || t == RDFSyntax.NTRIPLES).isPresent()) {
			throw new IllegalStateException("base iri required for inputstream source");
		}
	}

	/**
	 * Reset all source* fields to Optional.empty()
	 * <p>
	 * Subclasses should override this if they need to reset any additional
	 * source* fields.
	 * 
	 */
	protected void resetSource() {
		sourceInputStream = Optional.empty();
		sourceIri = Optional.empty();
		sourceFile = Optional.empty();
	}

	/**
	 * Parse {@link #sourceInputStream}, {@link #sourceFile} or
	 * {@link #sourceIri}.
	 * <p>
	 * One of the source fields MUST be present.
	 * <p>
	 * When this method is called, {@link #intoGraph} MUST always be present, as
	 * that is where the parsed triples MUST be inserted into.
	 * <p>
	 * 
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 * @throws ParseException
	 */
	protected abstract void parseSynchronusly() throws IOException, IllegalStateException, ParseException;

	/**
	 * Prepare a clone of this RDFParserBuilder which have been checked and
	 * completed.
	 * <p>
	 * 
	 * 
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	protected AbstractRDFParserBuilder prepareForParsing() throws IOException, IllegalStateException {
		checkSource();
		checkBaseRequired();
		// We'll make a clone of our current state which will be passed to
		// parseSynchronously()
		AbstractRDFParserBuilder c = clone();

		// Use a fresh SimpleRDFTermFactory for each parse
		if (!c.rdfTermFactory.isPresent()) {
			c.rdfTermFactory = Optional.of(createRDFTermFactory());
		}
		// No graph? We'll create one.
		if (!c.intoGraph.isPresent()) {
			c.intoGraph = c.rdfTermFactory.map(RDFTermFactory::createGraph);
		}
		// sourceFile, but no base? Let's follow any symlinks and use
		// the file:/// URI
		if (c.sourceFile.isPresent() && !c.base.isPresent()) {
			URI baseUri = c.sourceFile.get().toRealPath().toUri();
			c.base = Optional.of(internalRdfTermFactory.createIRI(baseUri.toString()));
		}
		return c;
	}

	protected RDFTermFactory createRDFTermFactory() {
		return new SimpleRDFTermFactory();
	}

	@Override
	public Future<Graph> parse() throws IOException, IllegalStateException {
		final AbstractRDFParserBuilder c = prepareForParsing();
		return threadpool.submit(() -> {
			c.parseSynchronusly();
			return c.intoGraph.get();
		});
	}

}
