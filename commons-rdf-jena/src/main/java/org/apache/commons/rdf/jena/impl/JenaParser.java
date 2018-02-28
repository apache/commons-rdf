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
package org.apache.commons.rdf.jena.impl;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.io.Parsed;
import org.apache.commons.rdf.api.io.Parser;
import org.apache.commons.rdf.api.io.ParserConfig;
import org.apache.commons.rdf.api.io.ParserConfig.ImmutableParserConfig;
import org.apache.commons.rdf.api.io.ParserSource;
import org.apache.commons.rdf.api.io.ParserTarget;
import org.apache.commons.rdf.jena.JenaDataset;
import org.apache.commons.rdf.jena.JenaGraph;
import org.apache.commons.rdf.jena.JenaRDF;
import org.apache.jena.riot.RDFParser;
import org.apache.jena.riot.RDFParserBuilder;

public class JenaParser implements Parser {

	private final RDFSyntax defaultSyntax;

	public JenaParser() {
		defaultSyntax = null; // unspecified/guess
	}

	public JenaParser(RDFSyntax syntax) {
		this.defaultSyntax = syntax;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Parsed parse(ParserConfig config) throws IOException {
		return parseImpl(completeConfig(config));
	}

	private ImmutableParserConfig completeConfig(ParserConfig config) {
		ImmutableParserConfig completed = config.asImmutableConfig();
		if (!completed.source().isPresent()) {
			throw new IllegalStateException("source missing from ParserConfig");
		}

		if (!completed.syntax().isPresent()) {
			// Might still be null
			completed = completed.withSyntax(defaultSyntax);
		}
		if (!completed.rdf().isPresent()) {
			completed = completed.withRDF(new JenaRDF());
		}
		RDF rdf = completed.rdf().get();
		if (!completed.target().isPresent()) {
			Dataset ds = rdf.createDataset();
			completed = completed.withTarget(ParserTarget.toDataset(ds));
		}
		ParserSource<?> source = completed.source().get();
		if (!completed.base().isPresent() && source.iri().isPresent()) {
			// Use base from source.iri() - but only if it's from a source
			// type Jena does not recognize
			Object src = source.src();
			if (!(src instanceof IRI) && !(src instanceof Path)) {
				completed = completed.withBase(source.iri().get());
			}
		}
		return completed;
	}

	@SuppressWarnings("rawtypes")
	private Parsed parseImpl(ImmutableParserConfig config) throws IOException {
		RDF rdf = config.rdf().get();
		JenaRDF jenaRDF = jenaRDF(rdf);
		ParserSource<?> source = config.source().get();
		ParserTarget<?> target = config.target().get();
		RDFParserBuilder jenaParser = RDFParser.create();

		config.base().map(IRI::getIRIString).map(jenaParser::base);		
		config.syntax().flatMap(jenaRDF::asJenaLang).map(jenaParser::lang);
		
		// Handle jena-supported sources first
		if (source.src() instanceof Path) {
			Path path = (Path) source.src();
			jenaParser.source(path);
		} else if (source.src() instanceof IRI) {
			IRI iri = (IRI) source.src();
			jenaParser.source(iri.getIRIString());
		} else if (source.src() instanceof String) {
			jenaParser.fromString(source.src().toString());
		} else {
			// This fallback should always work
			jenaParser.source(source.inputStream());
		}
		
		// Handle jena implementations firsts
		if (target.dest() instanceof JenaDataset) {
			JenaDataset jenaDataset = (JenaDataset) target.dest();
			jenaParser.parse(jenaDataset.asJenaDatasetGraph());
		} else if (target.dest() instanceof JenaGraph) { 
			JenaGraph jenaGraph = (JenaGraph) target.dest();
			jenaParser.parse(jenaGraph.asJenaGraph());
		} else {
			// General approach, stream to Quads
			jenaParser.parse(JenaRDF.streamJenaToQuad(rdf, target));
		}
		
		// Parsing finished
		return Parsed.from(source, target);
	}

	private JenaRDF jenaRDF(RDF rdf) {
		if (rdf instanceof JenaRDF) { 
			return (JenaRDF) rdf;
		} else {
			return new JenaRDF();
		}
	}

}
