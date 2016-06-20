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
package org.apache.commons.rdf.api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Builder for parsing an RDF source into a target (e.g. a Graph/Dataset).
 * <p>
 * This interface follows the
 * <a href="https://en.wikipedia.org/wiki/Builder_pattern">Builder pattern</a>,
 * allowing to set parser settings like {@link #contentType(RDFSyntax)} and
 * {@link #base(IRI)}. A caller MUST call one of the <code>source</code> methods
 * (e.g. {@link #source(IRI)}, {@link #source(Path)},
 * {@link #source(InputStream)}), and MUST call one of the <code>target</code>
 * methods (e.g. {@link #target(Consumer)}, {@link #target(Dataset)},
 * {@link #target(Graph)}) before calling {@link #parse()} on the returned
 * RDFParserBuilder - however methods can be called in any order.
 * <p>
 * The call to {@link #parse()} returns a {@link Future}, allowing asynchronous
 * parse operations. Callers are recommended to check {@link Future#get()} to
 * ensure parsing completed successfully, or catch exceptions thrown during
 * parsing.
 * <p>
 * Setting a method that has already been set will override any existing value
 * in the returned builder - irregardless of the parameter type (e.g.
 * {@link #source(IRI)} will override a previous {@link #source(Path)}. Settings
 * can be unset by passing <code>null</code> - note that this may 
 * require casting, e.g. <code>contentType( (RDFSyntax) null )</code> 
 * to undo a previous call to {@link #contentType(RDFSyntax)}.
 * <p>
 * It is undefined if a RDFParserBuilder is mutable or thread-safe, so callers
 * should always use the returned modified RDFParserBuilder from the builder
 * methods. The builder may return itself after modification, 
 * or a cloned builder with the modified settings applied. 
 * Implementations are however encouraged to be immutable,
 * thread-safe and document this. As an example starting point, see
 * {@link org.apache.commons.rdf.simple.AbstractRDFParserBuilder}.
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 *   Graph g1 = rDFTermFactory.createGraph();
 *   new ExampleRDFParserBuilder()
 *    	.source(Paths.get("/tmp/graph.ttl"))
 *    	.contentType(RDFSyntax.TURTLE)
 *   	.target(g1)
 *   	.parse().get(30, TimeUnit.Seconds);
 * </pre>
 *
 */
public interface RDFParserBuilder {

	/** 
	 * The result of {@link RDFParserBuilder#parse()} indicating
	 * parsing completed.
	 * <p>
	 * This is a marker interface that may be subclassed to include
	 * parser details, e.g. warning messages or triple counts.
	 */
	public interface ParseResult {		
	}

	/**
	 * Specify which {@link RDFTermFactory} to use for generating
	 * {@link RDFTerm}s.
	 * <p>
	 * This option may be used together with {@link #target(Graph)} to
	 * override the implementation's default factory and graph.
	 * <p>
	 * <strong>Warning:</strong> Using the same {@link RDFTermFactory} for 
	 * multiple {@link #parse()} calls  may accidentally merge 
	 * {@link BlankNode}s having the same label, as the parser may 
	 * use the {@link RDFTermFactory#createBlankNode(String)} method
	 * from the parsed blank node labels.
	 * 
	 * @see #target(Graph)
	 * @param rdfTermFactory
	 *            {@link RDFTermFactory} to use for generating RDFTerms.
	 * @return An {@link RDFParserBuilder} that will use the specified
	 *         rdfTermFactory
	 */
	RDFParserBuilder rdfTermFactory(RDFTermFactory rdfTermFactory);

	/**
	 * Specify the content type of the RDF syntax to parse.
	 * <p>
	 * This option can be used to select the RDFSyntax of the source, overriding
	 * any <code>Content-Type</code> headers or equivalent.
	 * <p>
	 * The character set of the RDFSyntax is assumed to be
	 * {@link StandardCharsets#UTF_8} unless overridden within the document
	 * (e.g. <?xml version="1.0" encoding="iso-8859-1"?></code> in
	 * {@link RDFSyntax#RDFXML}).
	 * <p>
	 * This method will override any contentType set with
	 * {@link #contentType(String)}.
	 * 
	 * @see #contentType(String)
	 * @param rdfSyntax
	 *            An {@link RDFSyntax} to parse the source according to, e.g.
	 *            {@link RDFSyntax#TURTLE}.
	 * @throws IllegalArgumentException
	 *             If this RDFParserBuilder does not support the specified
	 *             RDFSyntax.
	 * @return An {@link RDFParserBuilder} that will use the specified content
	 *         type.
	 */
	RDFParserBuilder contentType(RDFSyntax rdfSyntax) throws IllegalArgumentException;

	/**
	 * Specify the content type of the RDF syntax to parse.
	 * <p>
	 * This option can be used to select the RDFSyntax of the source, overriding
	 * any <code>Content-Type</code> headers or equivalent.
	 * <p>
	 * The content type MAY include a <code>charset</code> parameter if the RDF
	 * media types permit it; the default charset is
	 * {@link StandardCharsets#UTF_8} unless overridden within the document.
	 * <p>
	 * This method will override any contentType set with
	 * {@link #contentType(RDFSyntax)}.
	 * 
	 * @see #contentType(RDFSyntax)
	 * @param contentType
	 *            A content-type string, e.g. <code>application/ld+json</code>
	 *            or <code>text/turtle;charset="UTF-8"</code> as specified by
	 *            <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.1">
	 *            RFC7231</a>.
	 * @return An {@link RDFParserBuilder} that will use the specified content
	 *         type.
	 * @throws IllegalArgumentException
	 *             If the contentType has an invalid syntax, or this
	 *             RDFParserBuilder does not support the specified contentType.
	 */
	RDFParserBuilder contentType(String contentType) throws IllegalArgumentException;

	/**
	 * Specify a {@link Graph} to add parsed triples to.
	 * <p>
	 * If the source supports datasets (e.g. the {@link #contentType(RDFSyntax)}
	 * set has {@link RDFSyntax#supportsDataset} is true)), then only quads in
	 * the <em>default graph</em> will be added to the Graph as {@link Triple}s.
	 * <p>
	 * It is undefined if any triples are added to the specified {@link Graph}
	 * if {@link #parse()} throws any exceptions. (However implementations are
	 * free to prevent this using transaction mechanisms or similar). If
	 * {@link Future#get()} does not indicate an exception, the parser
	 * implementation SHOULD have inserted all parsed triples to the specified
	 * graph.
	 * <p>
	 * Calling this method will override any earlier targets set with
	 * {@link #target(Graph)}, {@link #target(Consumer)} or
	 * {@link #target(Dataset)}.
	 * <p>
	 * The default implementation of this method calls {@link #target(Consumer)}
	 * with a {@link Consumer} that does {@link Graph#add(Triple)} with
	 * {@link Quad#asTriple()} if the quad is in the default graph.
	 * 
	 * @param graph
	 *            The {@link Graph} to add triples to.
	 * @return An {@link RDFParserBuilder} that will insert triples into the
	 *         specified graph.
	 */
	default RDFParserBuilder target(Graph graph) {		
		return target(q -> { 
			if (! q.getGraphName().isPresent()) { 
				graph.add(q.asTriple());
			}
		});
	}

	/**
	 * Specify a {@link Dataset} to add parsed quads to.
	 * <p>
	 * It is undefined if any quads are added to the specified
	 * {@link Dataset} if {@link #parse()} throws any exceptions. 
	 * (However implementations are free to prevent this using transaction 
	 * mechanisms or similar).  On the other hand, if {@link #parse()}
	 * does not indicate an exception, the 
	 * implementation SHOULD have inserted all parsed quads 
	 * to the specified dataset.
	 * <p>
	 * Calling this method will override any earlier targets set with 
	 * {@link #target(Graph)}, {@link #target(Consumer)} or {@link #target(Dataset)}.
	 * <p>
	 * The default implementation of this method calls {@link #target(Consumer)}
	 * with a {@link Consumer} that does {@link Dataset#add(Quad)}.
	 * 
	 * @param dataset
	 *            The {@link Dataset} to add quads to.
	 * @return An {@link RDFParserBuilder} that will insert triples into the
	 *         specified dataset.
	 */
	default RDFParserBuilder target(Dataset dataset) {
		return target(dataset::add);
	}

	/**
	 * Specify a consumer for parsed quads.
	 * <p>
	 * It is undefined if any quads are consumed if {@link #parse()} throws any
	 * exceptions. On the other hand, if {@link #parse()} does not indicate an
	 * exception, the implementation SHOULD have produced all parsed quads to
	 * the specified consumer.
	 * <p>
	 * Calling this method will override any earlier targets set with
	 * {@link #target(Graph)}, {@link #target(Consumer)} or
	 * {@link #target(Dataset)}.
	 * <p>
	 * The consumer is not assumed to be thread safe - only one
	 * {@link Consumer#accept(Object)} is delivered at a time for a given
	 * {@link RDFParserBuilder#parse()} call.
	 * <p>
	 * This method is typically called with a functional consumer, for example:
	 * <pre>
	 * List<Quad> quads = new ArrayList<Quad>;
	 * parserBuilder.target(quads::add).parse();
	 * </pre>
	 * 
	 * @param consumer
	 *            A {@link Consumer} of {@link Quad}s
	 * @return An {@link RDFParserBuilder} that will call the consumer for into
	 *         the specified dataset.
	 * @return
	 */
	RDFParserBuilder target(Consumer<Quad> consumer);
	
	/**
	 * Specify a base IRI to use for parsing any relative IRI references.
	 * <p>
	 * Setting this option will override any protocol-specific base IRI (e.g.
	 * <code>Content-Location</code> header) or the {@link #source(IRI)} IRI,
	 * but does not override any base IRIs set within the source document (e.g.
	 * <code>@base</code> in Turtle documents).
	 * <p>
	 * If the source is in a syntax that does not support relative IRI
	 * references (e.g. {@link RDFSyntax#NTRIPLES}), setting the
	 * <code>base</code> has no effect.
	 * <p>
	 * This method will override any base IRI set with {@link #base(String)}.
	 *
	 * @see #base(String)
	 * @param base
	 *            An absolute IRI to use as a base.
	 * @return An {@link RDFParserBuilder} that will use the specified base IRI.
	 */
	RDFParserBuilder base(IRI base);

	/**
	 * Specify a base IRI to use for parsing any relative IRI references.
	 * <p>
	 * Setting this option will override any protocol-specific base IRI (e.g.
	 * <code>Content-Location</code> header) or the {@link #source(IRI)} IRI,
	 * but does not override any base IRIs set within the source document (e.g.
	 * <code>@base</code> in Turtle documents).
	 * <p>
	 * If the source is in a syntax that does not support relative IRI
	 * references (e.g. {@link RDFSyntax#NTRIPLES}), setting the
	 * <code>base</code> has no effect.
	 * <p>
	 * This method will override any base IRI set with {@link #base(IRI)}.
	 *
	 * @see #base(IRI)
	 * @param base
	 *            An absolute IRI to use as a base.
	 * @return An {@link RDFParserBuilder} that will use the specified base IRI.
	 * @throws IllegalArgumentException
	 *             If the base is not a valid absolute IRI string
	 */
	RDFParserBuilder base(String base) throws IllegalArgumentException;

	/**
	 * Specify a source {@link InputStream} to parse.
	 * <p>
	 * The source set will not be read before the call to {@link #parse()}.
	 * <p>
	 * The InputStream will not be closed after parsing. The InputStream does
	 * not need to support {@link InputStream#markSupported()}.
	 * <p>
	 * The parser might not consume the complete stream (e.g. an RDF/XML parser
	 * may not read beyond the closing tag of
	 * <code>&lt;/rdf:Description&gt;</code>).
	 * <p>
	 * The {@link #contentType(RDFSyntax)} or {@link #contentType(String)}
	 * SHOULD be set before calling {@link #parse()}.
	 * <p>
	 * The character set is assumed to be {@link StandardCharsets#UTF_8} unless
	 * the {@link #contentType(String)} specifies otherwise or the document
	 * declares its own charset (e.g. RDF/XML with a
	 * <code>&lt;?xml encoding="iso-8859-1"&gt;</code> header).
	 * <p>
	 * The {@link #base(IRI)} or {@link #base(String)} MUST be set before
	 * calling {@link #parse()}, unless the RDF syntax does not permit relative
	 * IRIs (e.g. {@link RDFSyntax#NTRIPLES}).
	 * <p>
	 * This method will override any source set with {@link #source(IRI)},
	 * {@link #source(Path)} or {@link #source(String)}.
	 * 
	 * @param inputStream
	 *            An InputStream to consume
	 * @return An {@link RDFParserBuilder} that will use the specified source.
	 */
	RDFParserBuilder source(InputStream inputStream);

	/**
	 * Specify a source file {@link Path} to parse.
	 * <p>
	 * The source set will not be read before the call to {@link #parse()}.
	 * <p>
	 * The {@link #contentType(RDFSyntax)} or {@link #contentType(String)}
	 * SHOULD be set before calling {@link #parse()}.
	 * <p>
	 * The character set is assumed to be {@link StandardCharsets#UTF_8} unless
	 * the {@link #contentType(String)} specifies otherwise or the document
	 * declares its own charset (e.g. RDF/XML with a
	 * <code>&lt;?xml encoding="iso-8859-1"&gt;</code> header).
	 * <p>
	 * The {@link #base(IRI)} or {@link #base(String)} MAY be set before calling
	 * {@link #parse()}, otherwise {@link Path#toUri()} will be used as the base
	 * IRI.
	 * <p>
	 * This method will override any source set with {@link #source(IRI)},
	 * {@link #source(InputStream)} or {@link #source(String)}.
	 * 
	 * @param file
	 *            A Path for a file to parse
	 * @return An {@link RDFParserBuilder} that will use the specified source.
	 */
	RDFParserBuilder source(Path file);

	/**
	 * Specify an absolute source {@link IRI} to retrieve and parse.
	 * <p>
	 * The source set will not be read before the call to {@link #parse()}.
	 * <p>
	 * If this builder does not support the given IRI (e.g.
	 * <code>urn:uuid:ce667463-c5ab-4c23-9b64-701d055c4890</code>), this method
	 * should succeed, while the {@link #parse()} should throw an
	 * {@link IOException}.
	 * <p>
	 * The {@link #contentType(RDFSyntax)} or {@link #contentType(String)} MAY
	 * be set before calling {@link #parse()}, in which case that type MAY be
	 * used for content negotiation (e.g. <code>Accept</code> header in HTTP),
	 * and SHOULD be used for selecting the RDFSyntax.
	 * <p>
	 * The character set is assumed to be {@link StandardCharsets#UTF_8} unless
	 * the protocol's equivalent of <code>Content-Type</code> specifies
	 * otherwise or the document declares its own charset (e.g. RDF/XML with a
	 * <code>&lt;?xml encoding="iso-8859-1"&gt;</code> header).
	 * <p>
	 * The {@link #base(IRI)} or {@link #base(String)} MAY be set before calling
	 * {@link #parse()}, otherwise the source IRI will be used as the base IRI.
	 * <p>
	 * This method will override any source set with {@link #source(Path)},
	 * {@link #source(InputStream)} or {@link #source(String)}.
	 * 
	 * @param iri
	 *            An IRI to retrieve and parse
	 * @return An {@link RDFParserBuilder} that will use the specified source.
	 */
	RDFParserBuilder source(IRI iri);

	/**
	 * Specify an absolute source IRI to retrieve and parse.
	 * <p>
	 * The source set will not be read before the call to {@link #parse()}.
	 * <p>
	 * If this builder does not support the given IRI (e.g.
	 * <code>urn:uuid:ce667463-c5ab-4c23-9b64-701d055c4890</code>), this method
	 * should succeed, while the {@link #parse()} should throw an
	 * {@link IOException}.
	 * <p>
	 * The {@link #contentType(RDFSyntax)} or {@link #contentType(String)} MAY
	 * be set before calling {@link #parse()}, in which case that type MAY be
	 * used for content negotiation (e.g. <code>Accept</code> header in HTTP),
	 * and SHOULD be used for selecting the RDFSyntax.
	 * <p>
	 * The character set is assumed to be {@link StandardCharsets#UTF_8} unless
	 * the protocol's equivalent of <code>Content-Type</code> specifies
	 * otherwise or the document declares its own charset (e.g. RDF/XML with a
	 * <code>&lt;?xml encoding="iso-8859-1"&gt;</code> header).
	 * <p>
	 * The {@link #base(IRI)} or {@link #base(String)} MAY be set before calling
	 * {@link #parse()}, otherwise the source IRI will be used as the base IRI.
	 * <p>
	 * This method will override any source set with {@link #source(Path)},
	 * {@link #source(InputStream)} or {@link #source(IRI)}.
	 * 
	 * @param iri
	 *            An IRI to retrieve and parse
	 * @return An {@link RDFParserBuilder} that will use the specified source.
	 * @throws IllegalArgumentException
	 *             If the base is not a valid absolute IRI string
	 * 
	 */
	RDFParserBuilder source(String iri) throws IllegalArgumentException;

	/**
	 * Parse the specified source.
	 * <p>
	 * A source method (e.g. {@link #source(InputStream)}, {@link #source(IRI)},
	 * {@link #source(Path)}, {@link #source(String)} or an equivalent subclass
	 * method) MUST have been called before calling this method, otherwise an
	 * {@link IllegalStateException} will be thrown.
	 * <p>
	 * A target method (e.g. {@link #target(Consumer)}, {@link #target(Dataset)}
	 * , {@link #target(Graph)} or an equivalent subclass method) MUST have been
	 * called before calling this method, otherwise an
	 * {@link IllegalStateException} will be thrown.
	 * <p>
	 * It is undefined if this method is thread-safe, however the
	 * {@link RDFParserBuilder} may be reused (e.g. setting a different source)
	 * as soon as the {@link Future} has been returned from this method.
	 * <p>
	 * The RDFParserBuilder SHOULD perform the parsing as an asynchronous
	 * operation, and return the {@link Future} as soon as preliminary checks
	 * (such as validity of the {@link #source(IRI)} and
	 * {@link #contentType(RDFSyntax)} settings) have finished. The future
	 * SHOULD not mark {@link Future#isDone()} before parsing is complete. A
	 * synchronous implementation MAY be blocking on the <code>parse()</code>
	 * call and return a Future that is already {@link Future#isDone()}.
	 * <p>
	 * The returned {@link Future} contains a {@link ParseResult}. 
	 * Implementations may subclass this interface to provide any 
	 * parser details, e.g. list of warnings. <code>null</code> is a
	 * possible return value if no details are available, but 
	 * parsing succeeded.
	 * <p>
	 * If an exception occurs during parsing, (e.g. {@link IOException} or
	 * {@link org.apache.commons.rdf.simple.AbstractRDFParserBuilder.RDFParseException}), 
	 * it should be indicated as the
	 * {@link java.util.concurrent.ExecutionException#getCause()} in the
	 * {@link java.util.concurrent.ExecutionException} thrown on
	 * {@link Future#get()}.
	 * 
	 * @return A Future that will return the populated {@link Graph} when the
	 *         parsing has finished.
	 * @throws IOException
	 *             If an error occurred while starting to read the source (e.g.
	 *             file not found, unsupported IRI protocol). Note that IO
	 *             errors during parsing would instead be the
	 *             {@link java.util.concurrent.ExecutionException#getCause()} of
	 *             the {@link java.util.concurrent.ExecutionException} thrown on
	 *             {@link Future#get()}.
	 * @throws IllegalStateException
	 *             If the builder is in an invalid state, e.g. a
	 *             <code>source</code> has not been set.
	 */
	Future<? extends ParseResult> parse() throws IOException, IllegalStateException;
}
