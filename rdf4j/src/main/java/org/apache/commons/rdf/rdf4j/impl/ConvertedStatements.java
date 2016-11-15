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
package org.apache.commons.rdf.rdf4j.impl;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Supplier;

import org.apache.commons.rdf.rdf4j.ClosableIterable;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryResult;

final class ConvertedStatements<T> implements ClosableIterable<T> {

    private final RepositoryConnection conn;
    private final RepositoryResult<Statement> results;
    private final Function<Statement, T> statementAdapter;

    ConvertedStatements(final Supplier<RepositoryConnection> repositoryConnector, final Function<Statement, T> statementAdapter,
            final Resource subj, final org.eclipse.rdf4j.model.IRI pred, final Value obj, final Resource... contexts) {
        this.statementAdapter = statementAdapter;
        this.conn = repositoryConnector.get();
        this.results = conn.getStatements(subj, pred, obj, contexts);
    }

    @Override
    public Iterator<T> iterator() {
        return new ConvertedIterator();
    }

    @Override
    public void close() {
        results.close();
        conn.close();
    }

    private final class ConvertedIterator implements Iterator<T> {
        @Override
        public boolean hasNext() {
            final boolean hasNext = results.hasNext();
            if (!hasNext) {
                close();
            }
            return hasNext;
        }

        @Override
        public T next() {
            return statementAdapter.apply(results.next());
        }
    }

}