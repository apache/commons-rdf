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

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/** 
 * Enumeration of the RDF 1.1 serialization syntaxes.
 * <p>
 * This enumeration lists the W3C standardized 
 * RDF 1.1 syntaxes like {@link #TURTLE} and {@link #JSONLD}.  
 * Note the existence of other RDF syntaxes that are not included here, 
 * e.g. <a href="http://www.w3.org/TeamSubmission/n3/">N3</a> 
 * and <a href="https://en.wikipedia.org/wiki/TriX_%28syntax%29">TriX</a>.
 * 
 * @see <a href="https://www.w3.org/TR/rdf11-primer/#section-graph-syntax">RDF 1.1 Primer</a>
 *
 */
public enum RDFSyntax {
	
	/**
	 * JSON-LD 1.0
	 * 
	 * @see <a href="https://www.w3.org/TR/json-ld/">https://www.w3.org/TR/json-ld/</a>
	 * 
	 */
	JSONLD("JSON-LD 1.0", "application/ld+json", ".jsonld", true),

	/**
	 * RDF 1.1 Turtle
	 * 
	 * @see <a href="https://www.w3.org/TR/turtle/">https://www.w3.org/TR/turtle/</a>
	 *
	 */
	TURTLE("RDF 1.1 Turtle", "text/turtle", ".ttl", false), 

	/**
	 * RDF 1.1 N-Quads
	 * 
	 * @see <a href="https://www.w3.org/TR/n-quads/">https://www.w3.org/TR/n-quads/</a>
	 */
	NQUADS("RDF 1.1 N-Quads", "application/n-quads", ".nq", true),

	/**
	 * RDF 1.1 N-Triples
	 * 
	 * @see <a href="https://www.w3.org/TR/n-triples/">https://www.w3.org/TR/n-triples/</a>
	 */
	NTRIPLES("RDF 1.1 N-Triples", "application/n-triples", ".nt", false),
	
	/**
	 * HTML+RDFa 1.1
	 * 
	 * @see <a href="https://www.w3.org/TR/html-rdfa/">https://www.w3.org/TR/html-rdfa/</a>
	 */
	RDFA_HTML("HTML+RDFa 1.1", "text/html", ".html", false),
	
	/**
	 * XHTML+RDFa 1.1 
	 * 
	 * @see <a href="https://www.w3.org/TR/xhtml-rdfa/">https://www.w3.org/TR/xhtml-rdfa/</a> 
	 */
	RDFA_XHTML("XHTML+RDFa 1.1", "application/xhtml+xml", ".xhtml", false),
	
	/**
	 * RDF 1.1 XML Syntax
	 * 
	 * @see <a href="https://www.w3.org/TR/rdf-syntax-grammar/">https://www.w3.org/TR/rdf-syntax-grammar/</a>
	 */
	RDFXML("RDF 1.1 XML Syntax", "application/rdf+xml", ".rdf", false),
	
	/**
	 * RDF 1.1 TriG
	 * 
	 * @see <a href="https://www.w3.org/TR/trig/">https://www.w3.org/TR/trig/</a>
	 */
	TRIG("RDF 1.1 TriG", "application/trig", ".trig", true);

	/** 
	 * The <a href="https://tools.ietf.org/html/rfc2046">IANA media type</a> for the RDF syntax.
	 * <p> 
	 * The media type can be used as part of 
	 * <code>Content-Type</code> 
	 * and <code>Accept</code> for <em>content negotiation</em> in the 
	 * <a href="https://tools.ietf.org/html/rfc7231#section-3.1.1.1">HTTP protocol</a>. 
	 */
	public final String mediaType;
	
	/**
	 * The <a href="https://tools.ietf.org/html/rfc2046">IANA-registered</a> file extension.
	 * <p>
	 * The file extension includes the leading period, e.g. <code>.jsonld</code>
	 */
	public final String fileExtension;
	
	/**
	 * Indicate if this RDF syntax supports <a href="https://www.w3.org/TR/rdf11-concepts/#section-dataset">RDF Datasets</a>. 
	 */
	public final boolean supportsDataset;
	
	private final String name;
	
	/** 
	 * A human-readable name for the RDF syntax.
	 * <p>
	 * The name is equivalent to the the title of the corresponding W3C Specification.
	 */
	@Override
	public String toString() {
		return name;
	}
	
	private RDFSyntax(String name, String mediaType, String fileExtension, boolean supportsDataset) {
		this.name = name;
		this.mediaType = mediaType;
		this.fileExtension = fileExtension;
		this.supportsDataset = supportsDataset;
	}
	
	/**
	 * Return the RDFSyntax with the specified media type.
	 * <p>
	 * The <code>mediaType</code> is compared in lower case, therefore it might
	 * not be equal to the {@link RDFSyntax#mediaType} of the returned
	 * RDFSyntax.
	 * <p>
	 * For convenience matching of media types used in a
	 * <code>Content-Type</code> header, if the <code>mediaType</code> contains
	 * the characters <code>;</code>, <code>,</code> or white space, only the
	 * part of the string to the left of those characters are considered.
	 * 
	 * @param mediaType
	 *            The media type to match
	 * @return If {@link Optional#isPresent()}, the {@link RDFSyntax} which has
	 *         a matching {@link RDFSyntax#mediaType}, otherwise
	 *         {@link Optional#empty()} indicating that 
	 *         no matching syntax was found.
	 */
	public static Optional<RDFSyntax> byMediaType(String mediaType) {
		final String type = mediaType.toLowerCase(Locale.ENGLISH).
				split("\\s*[;,]", 2)[0];
		return Arrays.stream(RDFSyntax.values()).filter(
				t -> t.mediaType.equals(type)).findAny();		
	}

	/**
	 * Return the RDFSyntax with the specified file extension.
	 * <p>
	 * The <code>fileExtension</code> is compared in lower case, therefore it
	 * might not be equal to the {@link RDFSyntax#fileExtension} of the returned
	 * RDFSyntax.
	 * 
	 * @param fileExtension
	 *            The fileExtension to match, starting with <code>.</code>
	 * @return If {@link Optional#isPresent()}, the {@link RDFSyntax} which has
	 *         a matching {@link RDFSyntax#fileExtension}, otherwise
	 *         {@link Optional#empty()} indicating that no matching file
	 *         extension was found.
	 */
	public static Optional<RDFSyntax> byFileExtension(String fileExtension) {		
		final String ext = fileExtension.toLowerCase(Locale.ENGLISH);
		return Arrays.stream(RDFSyntax.values()).filter(
				t -> t.fileExtension.equals(ext)).findAny();		
	}

}
