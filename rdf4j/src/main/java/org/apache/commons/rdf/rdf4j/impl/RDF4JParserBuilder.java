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
package org.apache.commons.rdf.rdf4j.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFParserBuilder;
import org.apache.commons.rdf.rdf4j.RDF4JDataset;
import org.apache.commons.rdf.rdf4j.RDF4JGraph;
import org.apache.commons.rdf.rdf4j.RDF4JTermFactory;
import org.apache.commons.rdf.simple.AbstractRDFParserBuilder;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.repository.util.RDFInserter;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

public class RDF4JParserBuilder extends AbstractRDFParserBuilder implements RDFParserBuilder {

	private final class AddToQuadConsumer extends AbstractRDFHandler {
		private final Consumer<Quad> quadTarget;

		private AddToQuadConsumer(Consumer<Quad> quadTarget) {
			this.quadTarget = quadTarget;
		}

		public void handleStatement(org.eclipse.rdf4j.model.Statement st)
				throws org.eclipse.rdf4j.rio.RDFHandlerException {
			// TODO: if getRdfTermFactory() is a non-rdf4j factory, should
			// we use factory.createQuad() instead?
			// Unsure what is the promise of setting getRdfTermFactory() --
			// does it go all the way down to creating BlankNode, IRI and
			// Literal?
			quadTarget.accept(rdf4jTermFactory.asQuad(st));
			// Performance note:
			// Graph/Quad.add should pick up again our
			// RDF4JGraphLike.asStatement()
			// and avoid double conversion.
			// Additionally the RDF4JQuad and RDF4JTriple implementations
			// are lazily converting subj/obj/pred/graph.s
		}
	}

	private final static class AddToModel extends AbstractRDFHandler {
		private final Model model;

		public AddToModel(Model model) {
			this.model = model;
		}

		public void handleStatement(org.eclipse.rdf4j.model.Statement st)
				throws org.eclipse.rdf4j.rio.RDFHandlerException {
			model.add(st);
		}
		
		@Override
		public void handleNamespace(String prefix, String uri) throws RDFHandlerException {
			model.setNamespace(prefix, uri);
		}
	}

	private RDF4JTermFactory rdf4jTermFactory;

	@Override
	protected RDF4JTermFactory createRDFTermFactory() {
		return new RDF4JTermFactory();
	}

	@Override
	protected AbstractRDFParserBuilder prepareForParsing() throws IOException, IllegalStateException {
		RDF4JParserBuilder c = (RDF4JParserBuilder) prepareForParsing();
		// Ensure we have an RDF4JTermFactory for conversion.
		// We'll make a new one if user has provided a non-RDF4J factory
		c.rdf4jTermFactory = (RDF4JTermFactory) getRdfTermFactory().filter(RDF4JTermFactory.class::isInstance)
				.orElseGet(c::createRDFTermFactory);
		return c;
	}

	@Override
	protected void parseSynchronusly() throws IOException, RDFParseException {
		if (getContentType().isPresent()) {
			Rio.getParserFormatForMIMEType(getContentType().get());
		}

		Optional<RDFFormat> formatByMimeType = getContentType().flatMap(Rio::getParserFormatForMIMEType);
		Optional<RDFFormat> formatByFilename = getSourceFile().map(Path::getFileName).map(Path::toString)
				.flatMap(Rio::getParserFormatForFileName);
		RDFFormat format = formatByMimeType.orElse(
				formatByFilename.orElseThrow(() -> new RDFParseException("Unrecognized or missing content type")));

		RDFParser parser = Rio.createParser(format);

		parser.setRDFHandler(makeRDFHandler());
	}

	protected RDFHandler makeRDFHandler() {

		// TODO: Can we join the below DF4JDataset and RDF4JGraph cases
		// using RDF4JGraphLike<TripleLike<BlankNodeOrIRI,IRI,RDFTerm>>
		// or will that need tricky generics types?

		if (getTargetDataset().filter(RDF4JDataset.class::isInstance).isPresent()) {
			// One of us, we can add them as Statements directly
			RDF4JDataset dataset = (RDF4JDataset) getTargetDataset().get();
			if (dataset.asRepository().isPresent()) {				
				return new RDFInserter(dataset.asRepository().get().getConnection());
			}
			if (dataset.asModel().isPresent()) {
				Model model = dataset.asModel().get();
				return new AddToModel(model);
			}
			// Not backed by Repository or Model?
			// Third-party RDF4JDataset subclass, so we'll fall through to the
			// getTarget() handling further down
		} else if (getTargetGraph().filter(RDF4JGraph.class::isInstance).isPresent()) {
			RDF4JGraph graph = (RDF4JGraph) getTargetGraph().get();

			if (graph.asRepository().isPresent()) {
				RDFInserter inserter = new RDFInserter(graph.asRepository().get().getConnection());
				graph.getContextFilter().ifPresent(inserter::enforceContext);
				return inserter;
			}
			if (graph.asModel().isPresent() && graph.getContextFilter().isPresent()) {
				Model model = graph.asModel().get();
				return new AddToModel(model);
			}
			// else - fall through
		}

		// Fall thorough: let target() consume our converted quads.
		return new AddToQuadConsumer(getTarget());
	}

}
