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
package org.apache.commons.rdf.rdf4j;

import org.apache.commons.rdf.api.BlankNodeOrIRI;
import org.apache.commons.rdf.api.Triple;
import org.eclipse.rdf4j.model.Resource;

/**
 * Marker interface for RDF4J implementations of Commons RDF
 * {@link BlankNodeOrIRI} (e.g. the subject of a {@link Triple}).
 * <p>
 * The underlying RDF4J {@link org.eclipse.rdf4j.model.Resource} instance can be
 * retrieved with {@link #asValue()}.
 */
public interface RDF4JBlankNodeOrIRI extends RDF4JTerm, BlankNodeOrIRI {

    @Override
    Resource asValue();

}
