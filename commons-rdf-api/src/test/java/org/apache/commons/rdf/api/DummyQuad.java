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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

final class DummyQuad implements Quad {
    private static List<RDFTerm> quadList(final Quad q) {
         return Arrays.asList(
             q.getGraphName().orElse(null),
             q.getSubject(),
             q.getPredicate(),
             q.getObject());
    }
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Quad)) {
            return false;
        }
        return quadList(this).equals(quadList((Quad) obj));
    }
    @Override
    public Optional<BlankNodeOrIRI> getGraphName() {
        return Optional.empty();
    }
    @Override
    public RDFTerm getObject() {
        return new DummyIRI(3);
    }

    @Override
    public IRI getPredicate() {
        return new DummyIRI(2);
    }

    @Override
    public BlankNodeOrIRI getSubject() {
        return new DummyIRI(1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubject(), getPredicate(), getObject(), getGraphName());
    }
}