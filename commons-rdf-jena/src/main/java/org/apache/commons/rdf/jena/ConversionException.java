/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

package org.apache.commons.rdf.jena;

/**
 * Exception thrown when a problem arises across the Jena-CommonRDF boundary.
 * <p>
 * This should not happen in well-formed RDF data but, for example, Jena triples
 * allow
 * <a href="http://www.w3.org/TR/rdf11-concepts/#section-generalized-rdf">
 * generalized RDF</a>.
 */

public class ConversionException extends RuntimeException {
    private static final long serialVersionUID = -898179977312382568L;

    public ConversionException() {
    }

    public ConversionException(final String message) {
        super(message);
    }

    public ConversionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConversionException(final Throwable cause) {
        super(cause);
    }
}
