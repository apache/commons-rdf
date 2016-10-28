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
import java.util.stream.Stream;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.Triple;

import com.github.jsonldjava.core.RDFDataset;

/**
 * A {@link Graph} view of a JsonLd {@link RDFDataset}.
 * 
 */
public interface JsonLdGraph extends JsonLdGraphLike<Triple>, Graph {
}

class JsonLdGraphImpl extends AbstractJsonLdGraphLike<Triple> implements JsonLdGraph {

    private final Optional<BlankNodeOrIRI> graphName;

    JsonLdGraphImpl(RDFDataset rdfDataSet) {
        super(rdfDataSet);
        this.graphName = Optional.empty();
    }

    JsonLdGraphImpl(RDFDataset rdfDataSet, Optional<BlankNodeOrIRI> graphName, String bnodePrefix) {
        super(rdfDataSet, bnodePrefix);
        this.graphName = Objects.requireNonNull(graphName);
    }

    JsonLdGraphImpl(String bnodePrefix) {
        super(bnodePrefix);
        this.graphName = Optional.empty();
    }

    @Override
    public void clear() {
        filteredGraphs(graphName).forEach(l -> l.clear());
    }

    @Override
    public void add(Triple t) {
        // Ensure it's added in the correct graph
        super.add(graphName.orElse(null), t.getSubject(), t.getPredicate(), t.getObject());
    }

    @Override
    public void add(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        super.add(graphName.orElse(null), subject, predicate, object);
    }

    @Override
    public boolean contains(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        return super.contains(graphName, subject, predicate, object);
    }

    @Override
    public boolean contains(Triple t) {
        return contains(graphName, t.getSubject(), t.getPredicate(), t.getObject());
    }

    @Override
    public void remove(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        super.remove(graphName, subject, predicate, object);
    }

    @Override
    public void remove(Triple t) {
        // Only remove from the particular graph
        remove(graphName, t.getSubject(), t.getPredicate(), t.getObject());
    }

    @Override
    public long size() {
        String g = graphName.map(factory::asJsonLdString).orElse("@default");
        return Optional.ofNullable(rdfDataSet.getQuads(g)).map(List::size).orElse(0);
    }

    @Override
    public Stream<JsonLdTriple> stream(BlankNodeOrIRI subject, IRI predicate, RDFTerm object) {
        return filteredGraphs(graphName).flatMap(List::stream).filter(quadFilter(subject, predicate, object))
                .map(factory::asTriple);
    }

    @Override
    JsonLdTriple asTripleOrQuad(com.github.jsonldjava.core.RDFDataset.Quad jsonldQuad) {
        return factory.asTriple(jsonldQuad);
    }
}
