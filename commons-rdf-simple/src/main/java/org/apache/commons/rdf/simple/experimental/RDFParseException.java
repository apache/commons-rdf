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

package org.apache.commons.rdf.simple.experimental;

import org.apache.commons.rdf.experimental.RDFParser;

/**
 * A form of Exception for Apache Commons RDF.
 */
public class RDFParseException extends Exception {

    private static final long serialVersionUID = 5427752643780702976L;
    /**
     * RDF parser.
     */
    private final RDFParser rdfParser;

    /**
     * Constructs a new instance.
     *
     * @param rdfParser RDF parser.
     */
    public RDFParseException(final RDFParser rdfParser) {
        this.rdfParser = rdfParser;
    }

    /**
     * Constructs a new instance.
     *
     * @param rdfParser RDF parser.
     * @param message   the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public RDFParseException(final RDFParser rdfParser, final String message) {
        super(message);
        this.rdfParser = rdfParser;
    }

    /**
     * Constructs a new instance.
     *
     * @param rdfParser RDF parser.
     * @param message   the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause     the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null} value is permitted, and indicates that
     *                  the cause is nonexistent or unknown.)
     */
    public RDFParseException(final RDFParser rdfParser, final String message, final Throwable cause) {
        super(message, cause);
        this.rdfParser = rdfParser;
    }

    /**
     * Constructs a new instance.
     *
     * @param rdfParser RDF parser.
     * @param cause     the cause (which is saved for later retrieval by the {@link #getCause()} method). (A {@code null} value is permitted, and indicates that
     *                  the cause is nonexistent or unknown.)
     */
    public RDFParseException(final RDFParser rdfParser, final Throwable cause) {
        super(cause);
        this.rdfParser = rdfParser;
    }

    /**
     * Gets the builder.
     *
     * @return the builder.
     */
    public RDFParser getRDFParserBuilder() {
        return rdfParser;
    }
}
