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

/**
 * Builder for parsing an RDF source into a Graph.
 * <p>
 * This interface follows the
 * <a href="https://en.wikipedia.org/wiki/Builder_pattern">Builder pattern</a>,
 * allowing to set parser settings like {@link #contentType(RDFSyntax)} and
 * {@link #base(IRI)}. A caller MUST call one of the
 * {@link #source(IRI)} methods before calling {@link #parse()} on the returned
 * RDFParserBuilder.
 * <p>
 * It is undefined if a RDFParserBuilder is mutable or thread-safe, so callers
 * should always use the returned modified RDFParserBuilder from the builder
 * methods. The builder may return itself, or a cloned builder with the modified
 * settings applied.
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 *   Graph g1 = ExampleRDFParserBuilder.source("http://example.com/graph.rdf").parse();
 *   ExampleRDFParserBuilder.source(Paths.get("/tmp/graph.ttl").contentType(RDFSyntax.TURTLE).intoGraph(g1).parse();
 * </pre>
 * 
 *
 */
public interface RDFParserBuilder {

	/**
	 * Specify which {@link RDFTermFactory} to use for generating
	 * {@link RDFTerm}s.
	 * <p>
	 * This option may be used together with {@link #intoGraph(Graph)} to
	 * override the implementation's default factory and graph.
	 * 
	 * @see #intoGraph(Graph)
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
	 * media types permit it; the default charset 
	 * is {@link StandardCharsets#UTF_8} unless overridden within the
	 * document.
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
	 * @throws IllegalArgumentException
	 *             If the contentType has an invalid syntax, 
	 *             or this RDFParserBuilder does not support the specified
	 *             contentType.
	 * @return An {@link RDFParserBuilder} that will use the specified content
	 *         type.
	 */
	RDFParserBuilder contentType(String contentType);

	/**
	 * Specify which {@link Graph} to add triples to.
	 * <p>
	 * The default (if this option has not been set) is that each call to
	 * {@link #parse()} return a new {@link Graph}, which is created using
	 * {@link RDFTermFactory#createGraph()} 
	 * if {@link #rdfTermFactory(RDFTermFactory)}
	 * has been set.
	 * 
	 * @param graph
	 *            The {@link Graph} to add triples into.
	 * @return An {@link RDFParserBuilder} that will insert triples into the
	 *         specified graph.
	 */
	RDFParserBuilder intoGraph(Graph graph);

	/**
	 * Specify a base IRI to use for parsing any relative IRI references.
	 * <p>
	 * Setting this option will override any protocol-specific base IRI (e.g.
	 * <code>Content-Location</code> header) or the {@link #source(IRI)} IRI,
	 * but does not override any base IRIs set within the source document (e.g.
	 * <code>@base</code> in Turtle documents).
	 * <p>
	 * If the source is in a syntax that does not support relative IRI
	 * references, e.g. {@link RDFSyntax#NTRIPLES}, setting the base has no
	 * effect.
	 * <p>
	 * This method will override any base IRI set with {@link #base(String)}.
	 *
	 * @see #base(String)
	 * @param base
	 *            An absolute IRI to use as a base
	 * @return An {@link RDFParserBuilder} that will use the specified base IRI
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
	 * references, e.g. {@link RDFSyntax#NTRIPLES}, setting the base has no
	 * effect.
	 * <p>
	 * This method will override any base IRI set with {@link #base(IRI)}.
	 *
	 * @see #base(IRI)
	 * @param base
	 *            An absolute IRI to use as a base
	 * @return An {@link RDFParserBuilder} that will use the specified base IRI
	 */
	RDFParserBuilder base(String base);

	/**
	 * Specify a source to parse.
	 * <p>
	 * The source set will not be read before the call to {@link #parse()}.
	 * <p>
	 * The InputStream will not be closed after parsing. The InputStream does
	 * not need to support {@link InputStream#markSupported()}. The parser might
	 * not consume the complete stream (e.g. the parser may not read beyond the
	 * closing tag of <code>&lt;/rdf:Description&;gt</code>).
	 * <p>
	 * The {@link #contentType(RDFSyntax)} or {@link #contentType(String)}
	 * SHOULD be set before calling {@link #parse()}.
	 * <p>
	 * The character set is assumed to be {@link StandardCharsets#UTF_8} unless
	 * the {@link #contentType(String)} specifies otherwise.
	 * <p>
	 * The {@link #base(IRI)} or {@link #base(String)} MUST be set before
	 * calling {@link #parse()}, unless the syntax does not permit relative
	 * IRIs.
	 * <p>
	 * This method will override any source set with {@link #source(IRI)},
	 * {@link #source(Path)} or {@link #source(String)}.
	 * 
	 * @param inputStream
	 *            An InputStream to consume
	 * @return An {@link RDFParserBuilder} that will use the specified source.
	 */
	RDFParserBuilder source(InputStream inputStream);

	RDFParserBuilder source(Path file);

	RDFParserBuilder source(IRI iri);

	RDFParserBuilder source(String iri);

	/**
	 * Parse the specified source.
	 * 
	 * @return A Future that will return the graph containing (at least) the
	 *         parsed triples.
	 * 
	 * @throws IOException
	 *             If an error occurred while reading the source.
	 * @throws IllegalStateException
	 *             If the builder is in an invalid state, e.g. a
	 *             <code>source</code> has not been set.
	 */
	Future<Graph> parse() throws IOException, IllegalStateException;
}
