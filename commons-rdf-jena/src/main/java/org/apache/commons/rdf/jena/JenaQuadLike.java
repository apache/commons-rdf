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

import org.apache.commons.rdf.api.QuadLike;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.jena.sparql.core.Quad;

/**
 * A {@link QuadLike} wrapper of a Jena {@link Quad}.
 * <p>
 * This is a marker interface common to its specializations {@link JenaQuad} and
 * {@link JenaGeneralizedQuadLike}.
 *
 * @see JenaQuad
 * @see JenaGeneralizedQuadLike
 *
 */
public interface JenaQuadLike<G extends RDFTerm> extends JenaTripleLike, QuadLike<G> {

    /**
     * Return the adapted Jena quad
     *
     * @return Adapted Jena {@link Quad}.
     */
    Quad asJenaQuad();
}
