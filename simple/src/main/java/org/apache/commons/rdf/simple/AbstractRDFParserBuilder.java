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
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
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
 * asynchronou  remote execution).
 */
public abstract class AbstractRDFParserBuilder implements RDFParserBuilder, Cloneable {

	public class RDFParseException extends Exception {		
		private static final long serialVersionUID = 5427752643780702976L;
		public RDFParseException() {
			super();
		}
		public RDFParseException(String message, Throwable cause) {
			super(message, cause);
		}
		public RDFParseException(String message) {
			super(message);
		}
		public RDFParseException(Throwable cause) {
			super(cause);
		}
		public RDFParserBuilder getRDFParserBuilder() {
			return AbstractRDFParserBuilder.this;
		}
	}
	
	public static final ThreadGroup threadGroup = new ThreadGroup("Commons RDF parsers");
	private static final ExecutorService threadpool = Executors.newCachedThreadPool(r -> new Thread(threadGroup, r));

	// Basically only used for creating IRIs
	private static RDFTermFactory internalRdfTermFactory = new SimpleRDFTermFactory();

	/**
	 * Get the set {@link RDFTermFactory}, if any.
	 */
	public Optional<RDFTermFactory> getRdfTermFactory() {
		return rdfTermFactory;
	}

	/**
	 * Get the set content-type {@link RDFSyntax}, if any.
	 * <p>
	 * If this is {@link Optional#isPresent()}, then 
	 * {@link #getContentType()} contains the 
	 * value of {@link RDFSyntax#mediaType}. 
	 */
	public Optional<RDFSyntax> getContentTypeSyntax() {
		return contentTypeSyntax;
	}
	
	/**
	 * Get the set content-type String, if any.
	 * <p>
	 * If this is {@link Optional#isPresent()} and 
	 * is recognized by {@link RDFSyntax#byMediaType(String)}, then
	 * the corresponding {@link RDFSyntax} is set on 
	 * {@link #getContentType()}, otherwise that is
	 * {@link Optional#empty()}. 
	 */
	public final Optional<String> getContentType() {
		return contentType;
	}

	/**
	 * Get the target to consume parsed Quads.
	 * <p>
	 * From the call to {@link #parseSynchronusly()}, this
	 * method is always {@link Optional#isPresent()}.
	 * 
	 */	
	public Consumer<Quad> getTarget() {
		return target;
	}

	/**
	 * Get the set base {@link IRI}, if present.
	 * <p>
	 * 
	 */	
	public Optional<IRI> getBase() {
		return base;
	}

	/**
	 * Get the set source {@link InputStream}.
	 * <p>
	 * If this is {@link Optional#isPresent()}, then 
	 * {@link #getSourceFile()} and {@link #getSourceIri()}
	 * are {@link Optional#empty()}.
	 */
	public Optional<InputStream> getSourceInputStream() {
		return sourceInputStream;
	}

	/**
	 * Get the set source {@link Path}.
	 * <p>
	 * If this is {@link Optional#isPresent()}, then 
	 * {@link #getSourceInputStream()} and {@link #getSourceIri()}
	 * are {@link Optional#empty()}.
	 */	
	public Optional<Path> getSourceFile() {
		return sourceFile;
	}

	/**
	 * Get the set source {@link Path}.
	 * <p>
	 * If this is {@link Optional#isPresent()}, then 
	 * {@link #getSourceInputStream()} and {@link #getSourceInputStream()()}
	 * are {@link Optional#empty()}.
	 */		
	public Optional<IRI> getSourceIri() {
		return sourceIri;
	}

	private Optional<RDFTermFactory> rdfTermFactory = Optional.empty();
	private Optional<RDFSyntax> contentTypeSyntax = Optional.empty();
	private Optional<String> contentType = Optional.empty();
	private Optional<IRI> base = Optional.empty();
	private Optional<InputStream> sourceInputStream = Optional.empty();
	private Optional<Path> sourceFile = Optional.empty();
	private Optional<IRI> sourceIri = Optional.empty();
	private Consumer<Quad> target;

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
	public RDFParserBuilder contentType(String contentType) throws IllegalArgumentException {
		AbstractRDFParserBuilder c = clone();
		c.contentType = Optional.ofNullable(contentType);
		c.contentTypeSyntax = c.contentType.flatMap(RDFSyntax::byMediaType);
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
	 * <p>
	 * Subclasses might override this method, e.g. to support other
	 * source combinations, or to check if the sourceIri is 
	 * resolvable. 
	 * 
	 * @throws IOException If a source file can't be read
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
	 * @throws IllegalStateException if base is required, but not set.
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
	 * Parse {@link #sourceInputStream}, {@link #sourceFile} or
	 * {@link #sourceIri}.
	 * <p>
	 * One of the source fields MUST be present, as checked by {@link #checkSource()}.
	 * <p>
	 * {@link #checkBaseRequired()} is called to verify if {@link #getBase()} is required.
	 * <p>
	 * When this method is called, {@link #intoGraph} MUST always be present, as
	 * that is where the parsed triples MUST be inserted into.
	 * <p>
	 * 
	 * @throws IOException If the source could not be read 
	 * @throws RDFParseException If the source could not be parsed (e.g. a .ttl file was not valid Turtle)
	 */
	protected abstract void parseSynchronusly() throws IOException, RDFParseException;

	/**
	 * Prepare a clone of this RDFParserBuilder which have been checked and
	 * completed.
	 * <p>
	 * The returned clone will always have
	 * {@link #getIntoGraph()} and {@link #getRdfTermFactory()} present.
	 * <p>
	 * If the {@link #getSourceFile()} is present, but the 
	 * {@link #getBase()} is not present, the base will be set to the
	 * <code>file:///</code> IRI for the Path's real path (e.g. resolving any 
	 * symbolic links).  
	 *  
	 * @return A completed and checked clone of this RDFParserBuilder
	 * @throws IOException If the source was not accessible (e.g. a file was not found)
	 * @throws IllegalStateException If the parser was not in a compatible setting (e.g. contentType was an invalid string) 
	 */
	protected AbstractRDFParserBuilder prepareForParsing() throws IOException, IllegalStateException {
		checkSource();
		checkBaseRequired();		
		checkContentType();
		checkTarget();

		// We'll make a clone of our current state which will be passed to
		// parseSynchronously()
		AbstractRDFParserBuilder c = clone();

		// Use a fresh SimpleRDFTermFactory for each parse
		if (!c.rdfTermFactory.isPresent()) {
			c.rdfTermFactory = Optional.of(createRDFTermFactory());
		}
		// sourceFile, but no base? Let's follow any symlinks and use
		// the file:/// URI
		if (c.sourceFile.isPresent() && !c.base.isPresent()) {
			URI baseUri = c.sourceFile.get().toRealPath().toUri();
			c.base = Optional.of(internalRdfTermFactory.createIRI(baseUri.toString()));
		}

		return c;
	}
	
	/**
	 * Subclasses can override this method to check the target is 
	 * valid.
	 * <p>
	 * The default implementation throws an IllegalStateException if the 
	 * target has not been set.
	 */
	protected void checkTarget() {
		if (target == null) {
			throw new IllegalStateException("target has not been set");
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
	 * @param path Path which extension should be checked
	 * @return The {@link RDFSyntax} which has a matching {@link RDFSyntax#fileExtension}, 
	 * 	otherwise {@link Optional#empty()}. 
	 */
	protected static Optional<RDFSyntax> guessRDFSyntax(Path path) {
			return fileExtension(path).flatMap(RDFSyntax::byFileExtension);
	}

	/**
	 * Return the file extension of a Path - if any.
	 * <p>
	 * The returned file extension includes the leading <code>.</code>
	 * <p>
	 * Note that this only returns the last extension, e.g. the 
	 * file extension for <code>archive.tar.gz</code> would be <code>.gz</code>
	 * 
	 * @param path Path which filename might contain an extension
	 * @return File extension (including the leading <code>.</code>, 
	 * 	or {@link Optional#empty()} if the path has no extension
	 */
	private static Optional<String> fileExtension(Path path) {
		Path fileName = path.getFileName();
		if (fileName == null) { 
			return Optional.empty();
		}
		String filenameStr = fileName.toString();
		int last = filenameStr.lastIndexOf(".");
		if (last > -1) { 
			return Optional.of(filenameStr.substring(last));				
		}
		return Optional.empty();
	}
	

	/**
	 * Create a new {@link RDFTermFactory} for a parse session.
	 * <p>
	 * This is called by {@link #parse()} to set 
	 * {@link #rdfTermFactory(RDFTermFactory)} if it is
	 * {@link Optional#empty()}.
	 * <p>
	 * As parsed blank nodes might be made with 
	 * {@link RDFTermFactory#createBlankNode(String)}, 
	 * each call to this method SHOULD return 
	 * a new RDFTermFactory instance.
	 * 
	 * @return A new {@link RDFTermFactory}
	 */
	protected RDFTermFactory createRDFTermFactory() {
		return new SimpleRDFTermFactory();
	}

	@Override
	public Future<ParseResult> parse() throws IOException, IllegalStateException {
		final AbstractRDFParserBuilder c = prepareForParsing();
		return threadpool.submit(() -> {
			c.parseSynchronusly();
			return null;
		});
	}

	@Override
	public RDFParserBuilder target(Consumer<Quad> consumer) {
		AbstractRDFParserBuilder c = clone();
		c.target = consumer;
		return c;
	}

}
