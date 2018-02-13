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
package org.apache.commons.rdf.rdf4j.experimental;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.apache.commons.rdf.rdf4j.RDF4JBlankNodeOrIRI;
import org.apache.commons.rdf.rdf4j.RDF4JDataset;
import org.apache.commons.rdf.rdf4j.RDF4JGraph;
import org.apache.commons.rdf.simple.experimental.AbstractRDFParser;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.repository.util.RDFInserter;
import org.eclipse.rdf4j.repository.util.RDFLoader;
import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;

/**
 * RDF4J-based parser.
 * <p>
 * This can handle the RDF syntaxes {@link RDFSyntax#JSONLD},
 * {@link RDFSyntax#NQUADS}, {@link RDFSyntax#NTRIPLES},
 * {@link RDFSyntax#RDFXML}, {@link RDFSyntax#TRIG} and {@link RDFSyntax#TURTLE}
 * - additional syntaxes can be supported by including the corresponding
 * <em>rdf4j-rio-*</em> module on the classpath.
 *
 */
public class RDF4JParser extends AbstractRDFParser<RDF4JParser> {

    private final class AddToQuadConsumer extends AbstractRDFHandler {
        private final Consumer<Quad> quadTarget;

        private AddToQuadConsumer(final Consumer<Quad> quadTarget) {
            this.quadTarget = quadTarget;
        }

        @Override
        public void handleStatement(final org.eclipse.rdf4j.model.Statement st) throws RDFHandlerException {
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

        public AddToModel(final Model model) {
            this.model = model;
        }

        @Override
        public void handleStatement(final org.eclipse.rdf4j.model.Statement st) throws RDFHandlerException {
            model.add(st);
        }

        @Override
        public void handleNamespace(final String prefix, final String uri) throws RDFHandlerException {
            model.setNamespace(prefix, uri);
        }
    }

    private RDF4J rdf4jTermFactory;
    private ParserConfig parserConfig = new ParserConfig();

    @Override
    protected RDF4J createRDFTermFactory() {
        return new RDF4J();
    }

    @Override
    protected RDF4JParser prepareForParsing() throws IOException, IllegalStateException {
        final RDF4JParser c = super.prepareForParsing();
        // Ensure we have an RDF4J for conversion.
        // We'll make a new one if user has provided a non-RDF4J factory
        c.rdf4jTermFactory = (RDF4J) getRdfTermFactory().filter(RDF4J.class::isInstance)
                .orElseGet(c::createRDFTermFactory);
        return c;
    }

    @Override
    protected void parseSynchronusly() throws IOException {
        final Optional<RDFFormat> formatByMimeType = getContentType().flatMap(Rio::getParserFormatForMIMEType);
        final String base = getBase().map(IRI::getIRIString).orElse(null);

        final ParserConfig parserConfig = getParserConfig();
        // TODO: Should we need to set anything?
        final RDFLoader loader = new RDFLoader(parserConfig, rdf4jTermFactory.getValueFactory());
        final RDFHandler rdfHandler = makeRDFHandler();
        if (getSourceFile().isPresent()) {
            // NOTE: While we could have used
            // loader.load(sourcePath.toFile()
            // if the path fs provider == FileSystems.getDefault(),
            // that RDFLoader method does not use absolute path
            // as the base URI, so to be consistent
            // we'll always do it with our own input stream
            //
            // That means we may have to guess format by extensions:
            final Optional<RDFFormat> formatByFilename = getSourceFile().map(Path::getFileName).map(Path::toString)
                    .flatMap(Rio::getParserFormatForFileName);
            // TODO: for the excited.. what about the extension after following
            // symlinks?

            final RDFFormat format = formatByMimeType.orElse(formatByFilename.orElse(null));
            try (InputStream in = Files.newInputStream(getSourceFile().get())) {
                loader.load(in, base, format, rdfHandler);
            }
        } else if (getSourceIri().isPresent()) {
            try {
                // TODO: Handle international IRIs properly
                // (Unicode support for for hostname, path and query)
                final URL url = new URL(getSourceIri().get().getIRIString());
                // TODO: This probably does not support https:// -> http://
                // redirections
                loader.load(url, base, formatByMimeType.orElse(null), makeRDFHandler());
            } catch (final MalformedURLException ex) {
                throw new IOException("Can't handle source URL: " + getSourceIri().get(), ex);
            }
        }
        // must be getSourceInputStream then, this is guaranteed by
        // super.checkSource();
        loader.load(getSourceInputStream().get(), base, formatByMimeType.orElse(null), rdfHandler);
    }

    /**
     * Get the RDF4J {@link ParserConfig} to use.
     * <p>
     * If no parser config is set, the default configuration is provided.
     * <p>
     * <strong>Note:</strong> The parser config is mutable - changes in the
     * returned config is reflected in this instance of the parser. To avoid
     * mutation, create a new {@link ParserConfig} and set
     * {@link #setParserConfig(ParserConfig)}.
     *
     * @return The RDF4J {@link ParserConfig}
     */
    public ParserConfig getParserConfig() {
        return parserConfig;
    }

    /**
     * Set an RDF4J {@link ParserConfig} to use
     *
     * @param parserConfig
     *            Parser configuration
     */
    public void setParserConfig(final ParserConfig parserConfig) {
        this.parserConfig = parserConfig;
    }

    protected RDFHandler makeRDFHandler() {

        // TODO: Can we join the below DF4JDataset and RDF4JGraph cases
        // using RDF4JGraphLike<TripleLike<BlankNodeOrIRI,IRI,RDFTerm>>
        // or will that need tricky generics types?

        if (getTargetDataset().filter(RDF4JDataset.class::isInstance).isPresent()) {
            // One of us, we can add them as Statements directly
            final RDF4JDataset dataset = (RDF4JDataset) getTargetDataset().get();
            if (dataset.asRepository().isPresent()) {
                return new RDFInserter(dataset.asRepository().get().getConnection());
            }
            if (dataset.asModel().isPresent()) {
                final Model model = dataset.asModel().get();
                return new AddToModel(model);
            }
            // Not backed by Repository or Model?
            // Third-party RDF4JDataset subclass, so we'll fall through to the
            // getTarget() handling further down
        } else if (getTargetGraph().filter(RDF4JGraph.class::isInstance).isPresent()) {
            final RDF4JGraph graph = (RDF4JGraph) getTargetGraph().get();

            if (graph.asRepository().isPresent()) {
                final RDFInserter inserter = new RDFInserter(graph.asRepository().get().getConnection());
                if (!graph.getContextMask().isEmpty()) {
                    final Stream<RDF4JBlankNodeOrIRI> b = graph.getContextMask().stream();
                    final Stream<Resource> c = b.map(RDF4JBlankNodeOrIRI::asValue);
                    final Resource[] contexts = c.toArray(Resource[]::new);
                    inserter.enforceContext(contexts);
                }
                return inserter;
            }
            if (graph.asModel().isPresent() && graph.getContextMask().isEmpty()) {
                // the model accepts any quad
                final Model model = graph.asModel().get();
                return new AddToModel(model);
            }
            // else - fall through
        }

        // Fall thorough: let target() consume our converted quads.
        return new AddToQuadConsumer(getTarget());
    }

}
