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

import org.apache.commons.rdf.api.TripleLike;
import org.apache.jena.graph.Triple;

/**
 * A {@link TripleLike} wrapper of a Jena {@link Triple}.
 * <p>
 * This is a marker interface common to its specializations {@link JenaTriple},
 * {@link JenaGeneralizedTripleLike}, {@link JenaQuad} and
 * {@link JenaGeneralizedQuadLike}.
 *
 * @see JenaTriple
 * @see JenaGeneralizedTripleLike
 * @see JenaQuad
 * @see JenaGeneralizedQuadLike
 *
 */
public interface JenaTripleLike extends org.apache.commons.rdf.api.TripleLike {

    /**
     * Return the adapted Jena triple
     *
     * @return Adapted Jena {@link Triple}.
     */
    Triple asJenaTriple();
}
