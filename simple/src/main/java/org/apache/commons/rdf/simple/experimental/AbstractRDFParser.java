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
package org.apache.commons.rdf.simple.experimental;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.experimental.RDFParser;
import org.apache.commons.rdf.simple.SimpleRDF;

/**
 * A Abstract RDFParser
 * <p>
 * This abstract class keeps the properties in protected fields like
 * {@link #sourceFile} using {@link Optional}. Some basic checking like
 * {@link #checkIsAbsolute(IRI)} is performed.
 * <p>
 * This class and its subclasses are {@link Cloneable}, immutable and
 * (therefore) thread-safe - each call to option methods like
 * {@link #contentType(String)} or {@link #source(IRI)} will return a cloned,
 * mutated copy.
 * <p>
 * By default, parsing is done by the abstract method
 * {@link #parseSynchronusly()} - which is executed in a cloned snapshot - hence
 * multiple {@link #parse()} calls are thread-safe. The default {@link #parse()}
 * uses a thread pool in {@link #threadGroup} - but implementations can override
 * {@link #parse()} (e.g. because it has its own threading model or use
 * asynchronous remote execution).
 */
public abstract class AbstractRDFParser<T extends AbstractRDFParser<T>> implements RDFParser, Cloneable {

	public static final ThreadGroup threadGroup = new ThreadGroup("Commons RDF parsers");
	private static final ExecutorService threadpool = Executors.newCachedThreadPool(r -> new Thread(threadGroup, r));

	private Optional<RDF> rdfTermFactory = Optional.empty();
	private Optional<RDFSyntax> contentTypeSyntax = Optional.empty();
	private Optional<String> contentType = Optional.empty();
	private Optional<IRI> base = Optional.empty();
	private Optional<InputStream> sourceInputStream = Optional.empty();
	private Optional<Path> sourceFile = Optional.empty();
	private Optional<IRI> sourceIri = Optional.empty();
	private Consumer<Quad> target;
	private Optional<Dataset> targetDataset;
	private Optional<Graph> targetGraph;
	
	// Basically only used for creating IRIs
	private static RDF internalRdfTermFactory = new SimpleRDF();

	/**
	 * Get the set {@link RDF}, if any.
	 * 
	 * @return The {@link RDF} to use, or {@link Optional#empty()} if it has not
	 *         been set
	 */
	public Optional<RDF> getRdfTermFactory() {
		return rdfTermFactory;
	}

	/**
	 * Get the set content-type {@link RDFSyntax}, if any.
	 * <p>
	 * If this is {@link Optional#isPresent()}, then {@link #getContentType()}
	 * contains the value of {@link RDFSyntax#mediaType}.
	 * 
	 * @return The {@link RDFSyntax} of the content type, or
	 *         {@link Optional#empty()} if it has not been set
	 */
	public Optional<RDFSyntax> getContentTypeSyntax() {
		return contentTypeSyntax;
	}

	/**
	 * Get the set content-type String, if any.
	 * <p>
	 * If this is {@link Optional#isPresent()} and is recognized by
	 * {@link RDFSyntax#byMediaType(String)}, then the corresponding
	 * {@link RDFSyntax} is set on {@link #getContentType()}, otherwise that is
	 * {@link Optional#empty()}.
	 * 
	 * @return The Content-Type IANA media type, e.g. <code>text/turtle</code>,
	 *         or {@link Optional#empty()} if it has not been set
	 */
	public final Optional<String> getContentType() {
		return contentType;
	}

	/**
	 * Get the target to consume parsed Quads.
	 * <p>
	 * From the call to {@link #parseSynchronusly()}, this will be a
	 * non-<code>null</code> value (as a target is a required setting).
	 * 
	 * @return The target consumer of {@link Quad}s, or <code>null</code> if it
	 *         has not yet been set.
	 * 
	 */
	public Consumer<Quad> getTarget() {
		return target;
	}

	/**
	 * Get the target dataset as set by {@link #target(Dataset)}.
	 * <p>
	 * The return value is {@link Optional#isPresent()} if and only if
	 * {@link #target(Dataset)} has been set, meaning that the implementation
	 * may choose to append parsed quads to the {@link Dataset} directly instead
	 * of relying on the generated {@link #getTarget()} consumer.
	 * <p>
	 * If this value is present, then {@link #getTargetGraph()} MUST be
	 * {@link Optional#empty()}.
	 * 
	 * @return The target Dataset, or {@link Optional#empty()} if another kind
	 *         of target has been set.
	 */
	public Optional<Dataset> getTargetDataset() {
		return targetDataset;
	}

	/**
	 * Get the target graph as set by {@link #target(Graph)}.
	 * <p>
	 * The return value is {@link Optional#isPresent()} if and only if
	 * {@link #target(Graph)} has been set, meaning that the implementation may
	 * choose to append parsed triples to the {@link Graph} directly instead of
	 * relying on the generated {@link #getTarget()} consumer.
	 * <p>
	 * If this value is present, then {@link #getTargetDataset()} MUST be
	 * {@link Optional#empty()}.
	 * 
	 * @return The target Graph, or {@link Optional#empty()} if another kind of
	 *         target has been set.
	 */
	public Optional<Graph> getTargetGraph() {
		return targetGraph;
	}

	/**
	 * Get the set base {@link IRI}, if present.
	 * 
	 * @return The base {@link IRI}, or {@link Optional#empty()} if it has not
	 *         been set
	 */
	public Optional<IRI> getBase() {
		return base;
	}

	/**
	 * Get the set source {@link InputStream}.
	 * <p>
	 * If this is {@link Optional#isPresent()}, then {@link #getSourceFile()}
	 * and {@link #getSourceIri()} are {@link Optional#empty()}.
	 * 
	 * @return The source {@link InputStream}, or {@link Optional#empty()} if it
	 *         has not been set
	 */
	public Optional<InputStream> getSourceInputStream() {
		return sourceInputStream;
	}

	/**
	 * Get the set source {@link Path}.
	 * <p>
	 * If this is {@link Optional#isPresent()}, then
	 * {@link #getSourceInputStream()} and {@link #getSourceIri()} are
	 * {@link Optional#empty()}.
	 *
	 * @return The source {@link Path}, or {@link Optional#empty()} if it has
	 *         not been set
	 */
	public Optional<Path> getSourceFile() {
		return sourceFile;
	}

	/**
	 * Get the set source {@link Path}.
	 * <p>
	 * If this is {@link Optional#isPresent()}, then
	 * {@link #getSourceInputStream()} and {@link #getSourceInputStream()} are
	 * {@link Optional#empty()}.
	 * 
	 * @return The source {@link IRI}, or {@link Optional#empty()} if it has not
	 *         been set
	 */
	public Optional<IRI> getSourceIri() {
		return sourceIri;
	}


	@SuppressWarnings("unchecked")
	@Override
	public T clone() {
		try {
			return (T) super.clone();
		} catch (final CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected T asT() {
		return (T) this;
	}

	@Override
	public T rdfTermFactory(final RDF rdfTermFactory) {
		final AbstractRDFParser<T> c = clone();
		c.rdfTermFactory = Optional.ofNullable(rdfTermFactory);
		return c.asT();
	}

	@Override
	public T contentType(final RDFSyntax rdfSyntax) {
		final AbstractRDFParser<T> c = clone();
		c.contentTypeSyntax = Optional.ofNullable(rdfSyntax);
		c.contentType = c.contentTypeSyntax.map(syntax -> syntax.getmediaType());
		return c.asT();
	}

	@Override
	public T contentType(final String contentType) {
		final AbstractRDFParser<T> c = clone();
		c.contentType = Optional.ofNullable(contentType);
		c.contentTypeSyntax = c.contentType.flatMap(RDFSyntax::byMediaType);
		return c.asT();
	}

	@Override
	public T base(final IRI base) {
		final AbstractRDFParser<T> c = clone();
		c.base = Optional.ofNullable(base);
		c.base.ifPresent(i -> checkIsAbsolute(i));
		return c.asT();
	}

	@Override
	public T base(final String base) {
		return base(internalRdfTermFactory.createIRI(base));
	}

	@Override
	public T source(final InputStream inputStream) {
		final AbstractRDFParser<T> c = clone();
		c.resetSource();
		c.sourceInputStream = Optional.ofNullable(inputStream);
		return c.asT();
	}

	@Override
	public T source(final Path file) {
		final AbstractRDFParser<T> c = clone();
		c.resetSource();
		c.sourceFile = Optional.ofNullable(file);
		return c.asT();
	}

	@Override
	public T source(final IRI iri) {
		final AbstractRDFParser<T> c = clone();
		c.resetSource();
		c.sourceIri = Optional.ofNullable(iri);
		c.sourceIri.ifPresent(i -> checkIsAbsolute(i));
		return c.asT();
	}

	@Override
	public T source(final String iri) {
		final AbstractRDFParser<T> c = clone();
		c.resetSource();
		c.sourceIri = Optional.ofNullable(iri).map(internalRdfTermFactory::createIRI);
		c.sourceIri.ifPresent(i -> checkIsAbsolute(i));
		return source(internalRdfTermFactory.createIRI(iri));
	}

	/**
	 * Check if an iri is absolute.
	 * <p>
	 * Used by {@link #source(String)} and {@link #base(String)}.
	 * 
	 * @param iri
	 *            IRI to check @ If the IRI is not absolute
	 */
	protected void checkIsAbsolute(final IRI iri) {
		if (!URI.create(iri.getIRIString()).isAbsolute()) {
			throw new IllegalArgumentException("IRI is not absolute: " + iri);
		}
	}

	/**
	 * Check that one and only one source is present and valid.
	 * <p>
	 * Used by {@link #parse()}.
	 * <p>
	 * Subclasses might override this method, e.g. to support other source
	 * combinations, or to check if the sourceIri is resolvable.
	 * 
	 * @throws IOException
	 *             If a source file can't be read
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
	 * Check if base is required.
	 * 
	 * @throws IllegalStateException
	 *             if base is required, but not set.
	 */
	protected void checkBaseRequired() throws IllegalStateException {
		if (!base.isPresent() && sourceInputStream.isPresent()
				&& !contentTypeSyntax.filter(t -> t == RDFSyntax.NQUADS || t == RDFSyntax.NTRIPLES).isPresent()) {
			throw new IllegalStateException("base iri required for inputstream source");
		}
	}

	/**
	 * Reset all source* fields to Optional.empty()
	 * <p>
	 * Subclasses should override this and call <code>super.resetSource()</code>
	 * if they need to reset any additional source* fields.
	 * 
	 */
	protected void resetSource() {
		sourceInputStream = Optional.empty();
		sourceIri = Optional.empty();
		sourceFile = Optional.empty();
	}

	/**
	 * Reset all optional target* fields to {@link Optional#empty()}.
	 * <p>
	 * Note that the consumer set for {@link #getTarget()} is
	 * <strong>note</strong> reset.
	 * <p>
	 * Subclasses should override this and call <code>super.resetTarget()</code>
	 * if they need to reset any additional target* fields.
	 * 
	 */
	protected void resetTarget() {
		targetDataset = Optional.empty();
		targetGraph = Optional.empty();
	}

	/**
	 * Parse {@link #sourceInputStream}, {@link #sourceFile} or
	 * {@link #sourceIri}.
	 * <p>
	 * One of the source fields MUST be present, as checked by
	 * {@link #checkSource()}.
	 * <p>
	 * {@link #checkBaseRequired()} is called to verify if {@link #getBase()} is
	 * required.
	 * 
	 * @throws IOException
	 *             If the source could not be read
	 * @throws RDFParseException
	 *             If the source could not be parsed (e.g. a .ttl file was not
	 *             valid Turtle)
	 */
	protected abstract void parseSynchronusly() throws IOException, RDFParseException;

	/**
	 * Prepare a clone of this RDFParser which have been checked and completed.
	 * <p>
	 * The returned clone will always have {@link #getTarget()} and
	 * {@link #getRdfTermFactory()} present.
	 * <p>
	 * If the {@link #getSourceFile()} is present, but the {@link #getBase()} is
	 * not present, the base will be set to the <code>file:///</code> IRI for
	 * the Path's real path (e.g. resolving any symbolic links).
	 * 
	 * @return A completed and checked clone of this RDFParser
	 * @throws IOException
	 *             If the source was not accessible (e.g. a file was not found)
	 * @throws IllegalStateException
	 *             If the parser was not in a compatible setting (e.g.
	 *             contentType was an invalid string)
	 */
	protected T prepareForParsing() throws IOException, IllegalStateException {
		checkSource();
		checkBaseRequired();
		checkContentType();
		checkTarget();

		// We'll make a clone of our current state which will be passed to
		// parseSynchronously()
		final AbstractRDFParser<T> c = clone();

		// Use a fresh SimpleRDF for each parse
		if (!c.rdfTermFactory.isPresent()) {
			c.rdfTermFactory = Optional.of(createRDFTermFactory());
		}
		// sourceFile, but no base? Let's follow any symlinks and use
		// the file:/// URI
		if (c.sourceFile.isPresent() && !c.base.isPresent()) {
			final URI baseUri = c.sourceFile.get().toRealPath().toUri();
			c.base = Optional.of(internalRdfTermFactory.createIRI(baseUri.toString()));
		}

		return c.asT();
	}

	/**
	 * Subclasses can override this method to check the target is valid.
	 * <p>
	 * The default implementation throws an IllegalStateException if the target
	 * has not been set.
	 */
	protected void checkTarget() {
		if (target == null) {
			throw new IllegalStateException("target has not been set");
		}
		if (targetGraph.isPresent() && targetDataset.isPresent()) {
			// This should not happen as each target(..) method resets the
			// optionals
			throw new IllegalStateException("targetGraph and targetDataset can't both be set");
		}
	}

	/**
	 * Subclasses can override this method to check compatibility with the
	 * contentType setting.
	 * 
	 * @throws IllegalStateException
	 *             if the {@link #getContentType()} or
	 *             {@link #getContentTypeSyntax()} is not compatible or invalid
	 */
	protected void checkContentType() throws IllegalStateException {
	}

	/**
	 * Guess RDFSyntax from a local file's extension.
	 * <p>
	 * This method can be used by subclasses if {@link #getContentType()} is not
	 * present and {@link #getSourceFile()} is set.
	 * 
	 * @param path
	 *            Path which extension should be checked
	 * @return The {@link RDFSyntax} which has a matching
	 *         {@link RDFSyntax#fileExtension}, otherwise
	 *         {@link Optional#empty()}.
	 */
	protected static Optional<RDFSyntax> guessRDFSyntax(final Path path) {
		return fileExtension(path).flatMap(RDFSyntax::byFileExtension);
	}

	/**
	 * Return the file extension of a Path - if any.
	 * <p>
	 * The returned file extension includes the leading <code>.</code>
	 * <p>
	 * Note that this only returns the last extension, e.g. the file extension
	 * for <code>archive.tar.gz</code> would be <code>.gz</code>
	 * 
	 * @param path
	 *            Path which filename might contain an extension
	 * @return File extension (including the leading <code>.</code>, or
	 *         {@link Optional#empty()} if the path has no extension
	 */
	private static Optional<String> fileExtension(final Path path) {
		final Path fileName = path.getFileName();
		if (fileName == null) {
			return Optional.empty();
		}
		final String filenameStr = fileName.toString();
		final int last = filenameStr.lastIndexOf('.');
		if (last > -1) {
			return Optional.of(filenameStr.substring(last));
		}
		return Optional.empty();
	}

	/**
	 * Create a new {@link RDF} for a parse session.
	 * <p>
	 * This is called by {@link #parse()} to set {@link #rdfTermFactory(RDF)} if
	 * it is {@link Optional#empty()}.
	 * <p>
	 * As parsed blank nodes might be made with
	 * {@link RDF#createBlankNode(String)}, each call to this method SHOULD
	 * return a new RDF instance.
	 * 
	 * @return A new {@link RDF}
	 */
	protected RDF createRDFTermFactory() {
		return new SimpleRDF();
	}

	@Override
	public Future<ParseResult> parse() throws IOException, IllegalStateException {
		final AbstractRDFParser<T> c = prepareForParsing();
		return threadpool.submit(() -> {
			c.parseSynchronusly();
			return null;
		});
	}

	@Override
	public T target(final Consumer<Quad> consumer) {
		final AbstractRDFParser<T> c = clone();
		c.resetTarget();
		c.target = consumer;
		return c.asT();
	}

	@Override
	public T target(final Dataset dataset) {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		final AbstractRDFParser<T> c = (AbstractRDFParser) RDFParser.super.target(dataset);
		c.resetTarget();
		c.targetDataset = Optional.of(dataset);
		return c.asT();
	}

	@Override
	public T target(final Graph graph) {
		@SuppressWarnings({ "rawtypes", "unchecked" }) // super calls our
		final
		// .clone()
		AbstractRDFParser<T> c = (AbstractRDFParser) RDFParser.super.target(graph);
		c.resetTarget();
		c.targetGraph = Optional.of(graph);
		return c.asT();
	}

}
