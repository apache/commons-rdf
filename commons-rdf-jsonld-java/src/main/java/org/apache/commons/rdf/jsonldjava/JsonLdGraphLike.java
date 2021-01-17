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
package org.apache.commons.rdf.jsonldjava;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.GraphLike;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Literal;
import org.apache.commons.rdf.api.RDFTerm;
// NOTE: To avod confusion, don't importing either of the Quad
import org.apache.commons.rdf.api.Triple;
import org.apache.commons.rdf.api.TripleLike;

import com.github.jsonldjava.core.RDFDataset;
import com.github.jsonldjava.core.RDFDataset.Node;

/**
 * Common abstract {@link GraphLike}.
 * <p>
 * Specialised by {@link JsonLdGraph}, {@link JsonLdUnionGraph} and
 * {@link JsonLdDataset}.
 *
 * @param <T>
 *            specialisation of {@link TripleLike}, e.g. {@link Triple} or
 *            {@link org.apache.commons.rdf.api.Quad}
 */
public interface JsonLdGraphLike<T extends TripleLike> extends GraphLike<T> {
    /**
     * Return the underlying JSONLD-Java {@link RDFDataset}.
     * <p>
     * Changes in the JSONLD-Java dataset is reflected in this class and vice
     * versa.
     *
     * @return The underlying JSONLD-JAva RDFDataset
     */
    RDFDataset getRdfDataSet();
}

abstract class AbstractJsonLdGraphLike<T extends TripleLike> implements JsonLdGraphLike<T> {

    /**
     * Used by {@link #bnodePrefix()} to get a unique UUID per JVM run
     */
    private static final UUID SALT = UUID.randomUUID();

    /**
     * Prefix to use in blank node identifiers
     */
    final String bnodePrefix;

    final JsonLdRDF factory;

    /**
     * The underlying JSON-LD {@link RDFDataset}.
     * <p>
     * Note: This is NOT final as it is reset to <code>null</code> by
     * {@link #close()} (to free memory).
     */
    RDFDataset rdfDataSet;

    AbstractJsonLdGraphLike(final RDFDataset rdfDataSet) {
        this(rdfDataSet, "urn:uuid:" + SALT + "#" + "g" + System.identityHashCode(rdfDataSet));
    }

    AbstractJsonLdGraphLike(final RDFDataset rdfDataSet, final String bnodePrefix) {
        this.rdfDataSet = Objects.requireNonNull(rdfDataSet);
        this.bnodePrefix = Objects.requireNonNull(bnodePrefix);
        this.factory = new JsonLdRDF(bnodePrefix);
    }

    AbstractJsonLdGraphLike(final String bnodePrefix) {
        this(new RDFDataset(), bnodePrefix);
    }

    @Override
    public void add(final T t) {
        // add triples to default graph by default
        BlankNodeOrIRI graphName = null;
        if (t instanceof org.apache.commons.rdf.api.Quad) {
            final org.apache.commons.rdf.api.Quad q = (org.apache.commons.rdf.api.Quad) t;
            graphName = q.getGraphName().orElse(null);
        }
        // FIXME: JSON-LD's rdfDataSet.addQuad method does not support
        // generalized RDF, so we have to do a naive cast here
        add(graphName, (BlankNodeOrIRI) t.getSubject(), (IRI) t.getPredicate(), t.getObject());
    }

    void add(final BlankNodeOrIRI graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        final String g = factory.asJsonLdString(graphName);
        final String s = factory.asJsonLdString(subject);
        final String p = factory.asJsonLdString(predicate);
        if (object instanceof BlankNodeOrIRI) {
            final String o = factory.asJsonLdString((BlankNodeOrIRI) object);
            rdfDataSet.addQuad(s, p, o, g);
        } else if (object instanceof Literal) {
            final Literal literal = (Literal) object;
            final String language = literal.getLanguageTag().orElse(null);
            final String datatype = literal.getDatatype().getIRIString();
            rdfDataSet.addQuad(s, p, literal.getLexicalForm(), datatype, language, g);
        }
    }

    public void close() {
        // Drop the memory reference, but don't clear it
        rdfDataSet = null;
    }

    @Override
    public void clear() {
        filteredGraphs(null).forEach(List::clear);
        // In theory we could use
        // rdfDataSet.clear();
        // but then we would need to also do
        // rdfDataSet.put("@default", new ArrayList());
        // .. both of which seems to be touching too much on JsonLd-Java's
        // internal structure
    }

    @Override
    public boolean contains(final T tripleOrQuad) {
        return stream().anyMatch(Predicate.isEqual(tripleOrQuad));
    }

    @Override
    public RDFDataset getRdfDataSet() {
        return rdfDataSet;
    }

    @Override
    public Stream<? extends T> stream() {
        return rdfDataSet.graphNames().parallelStream().map(rdfDataSet::getQuads)
                .flatMap(List<RDFDataset.Quad>::parallelStream).map(this::asTripleOrQuad);
    }

    /**
     * Convert JsonLd Quad to a Commons RDF {@link Triple} or
     * {@link org.apache.commons.rdf.api.Quad}
     *
     *
     * @see JsonLdRDF#asTriple(Quad)
     * @see JsonLdRDF#asQuad(Quad)
     * @param jsonldQuad
     *            jsonld quad to convert
     * @return converted {@link TripleLike}
     */
    abstract T asTripleOrQuad(RDFDataset.Quad jsonldQuad);

    // This will be made public in JsonLdDataset
    // and is used by the other methods.
    boolean contains(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI s, final IRI p, final RDFTerm o) {
        return filteredGraphs(graphName).flatMap(List::stream).anyMatch(quadFilter(s, p, o));
    }

    Stream<List<RDFDataset.Quad>> filteredGraphs(final Optional<BlankNodeOrIRI> graphName) {
        return rdfDataSet.graphNames().parallelStream()
                // if graphName == null (wildcard), select all graphs,
                // otherwise check its jsonld string
                // (including @default for default graph)
                .filter(g -> graphName == null || g.equals(graphName.map(factory::asJsonLdString).orElse("@default")))
                // remove the quads which match our filter (which could have
                // nulls as wildcards)
                .map(rdfDataSet::getQuads);
    }

    String graphNameAsJsonLdString(final T tripleOrQuad) {
        if (tripleOrQuad instanceof org.apache.commons.rdf.api.Quad) {
            final org.apache.commons.rdf.api.Quad quad = (org.apache.commons.rdf.api.Quad) tripleOrQuad;
            return quad.getGraphName().map(factory::asJsonLdString).orElse("@default");
        }
        return "@default";
    }

    Predicate<RDFDataset.Quad> quadFilter(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        final Optional<Node> subjectNode = Optional.ofNullable(subject).map(factory::asJsonLdNode);
        final Optional<Node> predicateNode = Optional.ofNullable(predicate).map(factory::asJsonLdNode);
        final Optional<Node> objectNode = Optional.ofNullable(object).map(factory::asJsonLdNode);

        return q -> {
            if (subjectNode.isPresent() && subjectNode.get().compareTo(q.getSubject()) != 0) {
                return false;
            }
            if (predicateNode.isPresent() && predicateNode.get().compareTo(q.getPredicate()) != 0) {
                return false;
            }
            if (objectNode.isPresent()) {
                if (object instanceof Literal && q.getObject().isLiteral()) {
                    // Special handling for COMMONSRDF-56, COMMONSRDF-51:
                    // Less efficient wrapper to a Commons RDF Literal so
                    // we can use our RDF 1.1-compliant .equals()
                    final RDFTerm otherObj = factory.asRDFTerm(q.getObject());
                    if (! (object.equals(otherObj))) {
                        return false;
                    }
                } else {
                    // JSONLD-Java's .compareTo can handle IRI, BlankNode and type-mismatch
                    if (objectNode.get().compareTo(q.getObject()) != 0) {
                        return false;
                    }
                }
            }
            // All patterns checked, must be good!
            return true;
        };
    }

    // NOTE: This is made public in JsonLdDataset and is used by the other
    // remove methods.
    void remove(final Optional<BlankNodeOrIRI> graphName, final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object) {
        // remove the quads which match our filter (which could have nulls as
        // wildcards)
        filteredGraphs(graphName).forEach(t -> t.removeIf(quadFilter(subject, predicate, object)));
    }

}
