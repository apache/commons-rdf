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

class DummyTriple implements Triple {
    @Override
    public BlankNodeOrIRI getSubject() {
        return new DummyIRI(1);
    }
    @Override
    public IRI getPredicate() {
        return new DummyIRI(2);
    }
    @Override
    public RDFTerm getObject() {
        return new DummyIRI(3);
    }

    private static List<RDFTerm> tripleList(final Triple q) {
         return Arrays.asList(
             q.getSubject(),
             q.getPredicate(),
             q.getObject());
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof Triple)) {
            return false;
        }
        return tripleList(this).equals(tripleList((Triple) obj));
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubject(), getPredicate(), getObject());
    }
}