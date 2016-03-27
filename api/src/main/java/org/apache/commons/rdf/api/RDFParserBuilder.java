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
import java.net.URL;
import java.nio.file.Path;

/**
 * Builder for parsing an RDF source into a Graph.
 * <p>
 * This interface follows the
 * <a href="https://en.wikipedia.org/wiki/Builder_pattern">Builder pattern</a>,
 * allowing to set parser settings like {@link #contentType(RDFSyntax)} and
 * {@link #base(IRI)}. A caller MUST at least call one of the
 * <code>source</code> methods before calling {@link #parse()} on the returned
 * RDFParserBuilder.
 * <p>
 * It is undefined if a RDFParserBuilder is mutable or thread-safe, callers
 * should always use the returned modified RDFParserBuilder from the builder
 * methods. The builder may return its own mutated instance, or a cloned builder
 * with the modified setting.
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
	 * If the content type is a 
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
	 * This method will override any contentType set with
	 * {@link #contentType(RDFSyntax)}.
	 * 
	 * @see #contentType(RDFSyntax)
	 * @param contentType
	 *            A content-type string, e.g. <code>text/turtle</code>
	 * @throws IllegalArgumentException
	 *             If this RDFParserBuilder does not support the specified
	 *             content-type, or it has an invalid syntax.
	 * @return An {@link RDFParserBuilder} that will use the specified content
	 *         type.
	 */
	RDFParserBuilder contentType(String contentType);

	/**
	 * Specify which {@link Graph} to add triples into.
	 * <p>
	 * The default if this option has not been set is that 
	 * each call to {@link #parse()} will return a new {@link Graph} (created using
	 * {@link RDFTermFactory#createGraph() if #rdfTermFactory(RDFTermFactory)
	 * has been set).
	 * 
	 * @param graph The {@link Graph} to add triples into.
	 * @return An {@link RDFParserBuilder} that will insert triples into the specified graph.
	 */
	RDFParserBuilder intoGraph(Graph graph);

	/**
	 * Specify a base IRI to use for parsing any relative IRI references.
	 * <p>
	 * Setting this option will override any protocol-specific base IRI 
	 * (e.g. <code>Content-Location</code> header) or the {@link #source(URL)} URL, 
	 * but does not override any base IRIs set within the source document
	 * (e.g. <code>@base</code> in Turtle documents).
	 * <p>
	 * If the source is in a syntax that does not support relative IRI references, 
	 * e.g. {@link RDFSyntax#NTRIPLES}, setting the base has no effect.
	 * <p>
	 * This method will override any base IRI set with {@link #base(String)}.
	 *
	 * @see #base(String)
	 * @param base An absolute IRI to use as a base
	 * @return An {@link RDFParserBuilder} that will use the specified base IRI
	 */
	RDFParserBuilder base(IRI base);

	/**
	 * Specify a base IRI to use for parsing any relative IRI references.
	 * <p>
	 * Setting this option will override any protocol-specific base IRI 
	 * (e.g. <code>Content-Location</code> header) or the {@link #source(URL)} URL, 
	 * but does not override any base IRIs set within the source document
	 * (e.g. <code>@base</code> in Turtle documents).
	 * <p>
	 * If the source is in a syntax that does not support relative IRI references, 
	 * e.g. {@link RDFSyntax#NTRIPLES}, setting the base has no effect.
	 * <p>
	 * This method will override any base IRI set with {@link #base(IRI)}.
	 *
	 * @see #base(IRI)
	 * @param base An absolute IRI to use as a base
	 * @return An {@link RDFParserBuilder} that will use the specified base IRI
	 */	
	RDFParserBuilder base(String base);

	/**
	 * Specify a source to parse.
	 * <p>
	 * The source set will not be accessed before the call to {@link #parse()}. 
	 * <p>
	 * The InputStream will not be closed after parsing. The
	 * InputStream does not need to support {@link InputStream#markSupported()}.
	 * <p>
	 * The {@link #contentType(RDFSyntax)} SHOULD be set unless the implementation
	 * supports syntax guessing.
	 * <p> 
	 * The {@link #base(IRI)} MUST be set unless the syntax does not permit relative IRIs
	 * (e.g.  {@link RDFSyntax#NTRIPLES}).
	 * 
	 * @param inputStream
	 * @return
	 */
	RDFParserBuilder source(InputStream inputStream);

	RDFParserBuilder source(Path file);

	RDFParserBuilder source(IRI iri);

	RDFParserBuilder source(String iri);

	/**
	 * Parse the specified source.
	 * 
	 * @return The graph containing (at least) the parsed triples.
	 * 
	 * @throws IOException
	 *             If an error occurred while reading the source.
	 * @throws IllegalStateException
	 *             If the builder is in an invalid state, e.g. a
	 *             <code>source</code> has not been set.
	 */
	Graph parse() throws IOException, IllegalStateException;
}
