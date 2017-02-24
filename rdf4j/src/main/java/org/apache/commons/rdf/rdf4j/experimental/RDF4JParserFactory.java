package org.apache.commons.rdf.rdf4j.experimental;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.rdf4j.RDF4J;
import org.apache.commons.rdf.rdf4j.RDF4JBlankNodeOrIRI;
import org.apache.commons.rdf.rdf4j.RDF4JDataset;
import org.apache.commons.rdf.rdf4j.RDF4JGraph;
import org.apache.commons.rdf.simple.experimental.IRISource;
import org.apache.commons.rdf.simple.experimental.ParserBuilder;
import org.apache.commons.rdf.simple.experimental.ParserFactoryImpl;
import org.apache.commons.rdf.simple.experimental.ParserImplementation;
import org.apache.commons.rdf.simple.experimental.PathSource;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.util.RDFInserter;
import org.eclipse.rdf4j.repository.util.RDFLoader;
import org.eclipse.rdf4j.rio.ParserConfig;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandler;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.helpers.AbstractRDFHandler;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerBase;
import org.eclipse.rdf4j.rio.helpers.RDFHandlerWrapper;

public class RDF4JParserFactory extends ParserFactoryImpl {

    public RDF4JParserFactory() {
        super(new RDF4JParserImpl());
    }
    
    private static RDFHandler makeRDFHandler(
            @SuppressWarnings("rawtypes") Target target, 
            RDF rdf) {
        Object t = target.target();
        if (t instanceof RDF4JDataset) {
            // One of us, we can add them as Statements directly
            final RDF4JDataset dataset = (RDF4JDataset) t;
            if (dataset.asRepository().isPresent()) {
                return new RDFInserter(dataset.asRepository().get().getConnection());
            }
            if (dataset.asModel().isPresent()) {
                final Model model = dataset.asModel().get();
                return new AddToModel(model);
            }
            // Not backed by Repository or Model?
            // Third-party RDF4JDataset subclass, so we'll fall through to the
            // Dataset handling
        }
        if (t instanceof RDF4JGraph) {
            final RDF4JGraph graph = (RDF4JGraph) t;

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
        }
        // Fall thorough: let target consume our converted quads.
        return new AddToQuadConsumer(target, rdf);
    }

    private static final class RDF4JParserImpl implements ParserImplementation {
        @Override
        @SuppressWarnings("rawtypes") 
        public long parse(Source source, Optional<RDFSyntax> rdfSyntax, 
               Target target, Optional<RDF> rdf, Map<Option, Object> map) {
            final CountingHandler rdfHandler = new CountingHandler(makeRDFHandler(target, rdf));
            RDF4J rdf4j;
            if (rdf.isPresent() && rdf.get() instanceof RDF4J) { 
                rdf4j = (RDF4J) rdf.get();
            } else { 
                rdf4j = new RDF4J();
            }            
            // TODO: Support setting ParserConfig as Option?
            ParserConfig parserConfig = new ParserConfig();
            final RDFLoader loader = new RDFLoader(parserConfig, rdf4j.getValueFactory());
            
            
            IRI baseIri = (IRI) map.getOrDefault(ParserBuilder.BaseOption.BASE, source.iri());
            String base; // handle null
            
            if (source instanceof PathSource) {
                Path p = ((PathSource)source).source();
                
                // rdfSyntax might not be present, or might not match RDF4J's known mime types
                Optional<RDFFormat> formatByMimeType = rdfSyntax.flatMap(Rio::getParserFormatForMIMEType);
                
                // but RDF4J  can also try to guess it by the file name                
                final Optional<RDFFormat> formatByFilename = p.map(Path::getFileName).map(Path::toString)
                        .flatMap(Rio::getParserFormatForFileName);
                // TODO: for the excited.. what about the extension after following
                // symlinks? 
                
                final RDFFormat format = formatByMimeType.orElse(formatByFilename.orElse(null));
                
                try (InputStream in = Files.newInputStream(p)) { 
                    loader.load(in, base.getIRIString(), format, rdfHandler);
                }
                return rdfHandler.count();
                
            }
            if (source instanceof IRISource) { 
                // TODO: Do we support other iri()'s?
                IRI i = source.iri();
                
                return rdfHandler.count()
            } else {
                // Fallback to parsing InputStream
                InputStream in = source.inputStream();
                // MUST have base and format
                IRI base = (IRI) map.getOrDefault(ParserBuilder.BaseOption.BASE, source.iri());
                RDFFormat format = Rio.getParserFormatForMIMEType(rdfSyntax.mediaType()).get();
                try {
                    loader.load(in, base.getIRIString(), format, rdfHandler);
                } catch (RDFParseException | RDFHandlerException | IOException e) {
                    throw new RuntimeException("Can't parse", e);
                }
                        
                return rdfHandler.count();
            }
        }
    }

    public static class CountingHandler extends RDFHandlerWrapper implements RDFHandler {
        private final AtomicLong counter = new AtomicLong();
        public CountingHandler(RDFHandler makeRDFHandler) {
            super(makeRDFHandler);
        }
        @Override
        public void handleStatement(Statement st) throws RDFHandlerException {
            super.handleStatement(st);
            counter.incrementAndGet();
        }
        public long count() {
            return counter.get();
        }        
    }
    
    private static final class AddToQuadConsumer extends AbstractRDFHandler {
        private final Consumer<Quad> quadTarget;
        private final RDF4J rdf4j;

        private AddToQuadConsumer(final Consumer<Quad> quadTarget, final RDF rdf) {
            this.quadTarget = quadTarget;
            if (rdf instanceof RDF4J) { 
                this.rdf4j = (RDF4J) rdf;
            } else { 
                this.rdf4j = new RDF4J();
            }
        }

        @Override
        public void handleStatement(final org.eclipse.rdf4j.model.Statement st)
                throws org.eclipse.rdf4j.rio.RDFHandlerException {
            quadTarget.accept(rdf4j.asQuad(st));
            // Performance note:
            // Graph/Quad.add should pick up again our
            // RDF4JGraphLike.asStatement()
            // and avoid double conversion.
            // Additionally the RDF4JQuad and RDF4JTriple implementations
            // are lazily converting subj/obj/pred/graph.s
        }
    }

    private static final class AddToModel extends AbstractRDFHandler {
        private final Model model;

        public AddToModel(final Model model) {
            this.model = model;
        }

        @Override
        public void handleStatement(final org.eclipse.rdf4j.model.Statement st)
                throws org.eclipse.rdf4j.rio.RDFHandlerException {
            model.add(st);
        }

        @Override
        public void handleNamespace(final String prefix, final String uri) throws RDFHandlerException {
            model.setNamespace(prefix, uri);
        }
    }

    
}
