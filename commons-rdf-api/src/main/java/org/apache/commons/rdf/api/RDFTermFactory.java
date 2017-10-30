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

/**
 * Factory for creating RDFTerm instances..
 * <p>
 * This interface is <strong>deprecated</strong> in favour of the richer
 * {@link RDF}.
 *
 * @see RDF
 */
@Deprecated
public interface RDFTermFactory {

    default BlankNode createBlankNode() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("createBlankNode() not supported");
    }

    default BlankNode createBlankNode(final String name) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("createBlankNode(String) not supported");
    }

    default Graph createGraph() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("createGraph() not supported");
    }

    default IRI createIRI(final String iri) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("createIRI(String) not supported");
    }

    default Literal createLiteral(final String lexicalForm) throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("createLiteral(String) not supported");
    }

    default Literal createLiteral(final String lexicalForm, final IRI dataType)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("createLiteral(String) not supported");
    }

    default Literal createLiteral(final String lexicalForm, final String languageTag)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("createLiteral(String,String) not supported");
    }

    default Triple createTriple(final BlankNodeOrIRI subject, final IRI predicate, final RDFTerm object)
            throws IllegalArgumentException, UnsupportedOperationException {
        throw new UnsupportedOperationException("createTriple(BlankNodeOrIRI,IRI,RDFTerm) not supported");
    }

}
