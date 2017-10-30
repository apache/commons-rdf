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

import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDFTerm;

/**
 * A generalized quad representation.
 * <p>
 * A <em>generalized quad</em> is a quad-like object which allow any
 * {@link RDFTerm} type for its {@link #getSubject()}, {@link #getPredicate()}
 * {@link #getObject()} and {@link #getGraphName()}. This might be useful with
 * some serializations like JSON-LD.
 * <p>
 * Note that unlike {@link Quad}, this type does not have fixed semantics for
 * {@link Object#equals(Object)} or {@link Object#hashCode()} beyond object
 * identity.
 *
 * @see JenaGeneralizedTripleLike
 */
public interface JenaGeneralizedQuadLike extends JenaQuadLike<RDFTerm> {
}
