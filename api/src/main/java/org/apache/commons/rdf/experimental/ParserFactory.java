/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed  this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * OUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rdf.experimental;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.experimental.ParserFactory.NeedSourceBased;
import org.apache.commons.rdf.experimental.ParserFactory.NeedSourceOrBase;
import org.apache.commons.rdf.experimental.ParserFactory.OptionalTarget;
import org.apache.commons.rdf.experimental.ParserFactory.Sync;
import org.apache.commons.rdf.experimental.ParserFactory.Source;
import org.apache.commons.rdf.experimental.ParserFactory.Target;

public interface ParserFactory extends _NeedTargetOrRDF {
    
    interface Async<T, S> {
        <V> Async<T, S> option(Option<V> option, V value);

        Future<Parsed<T, S>> parseAsync();
    }

    interface NeedSourceBased<T> extends _NeedIdentifiedSource<T> {
        <V> NeedSourceBased<T> option(Option<V> option, V value);

        Sync<T, InputStream> source(InputStream is);
    }

    interface NeedSourceOrBase<T> extends _OptionalBase<T>, _NeedIdentifiedSource<T> {
        <V> NeedSourceOrBase<T> option(Option<V> option, V value);
    }

    interface NeedSourceOrBaseOrSyntax<T> extends _OptionalBase<T>, _NeedIdentifiedSource<T> {
        <V> NeedSourceOrBaseOrSyntax<T> option(Option<V> option, V value);

        NeedSourceOrBase<T> syntax(RDFSyntax syntax);
    }

    interface NeedTarget extends _NeedTarget {
        <V> NeedTarget option(Option<V> option, V value);
    }

    interface NeedTargetOrRDF extends _NeedTargetOrRDF {
        <V> NeedTargetOrRDF option(Option<V> option, V value);
    }

    interface NeedTargetOrRDFOrSyntax extends _NeedTargetOrRDF {
        <V> NeedTargetOrRDFOrSyntax option(Option<V> option, V value);
    }

    interface Option<V> {
    }

    interface OptionalTarget<T> extends _NeedTarget, NeedSourceOrBase<T> {
        <V> OptionalTarget<T> option(Option<V> option, V value);
    }

    interface Parsed<T, S> {
        long count();

        Source<S> source();

        Target<T> target();
    }

    interface Sync<T, S> {
        Async<T, S> async();

        Async<T, S> async(ExecutorService executor);

        <V> Sync<T, S> option(Option<V> option, V value);

        Parsed<T, S> parse();
    }

    interface Source<T> {
        T source();
    }

    @FunctionalInterface
    interface Target<T> extends Consumer<Quad> {
        default T target() {
            return null; // unknown
        }
    }

    NeedTargetOrRDF syntax(RDFSyntax syntax);
}

interface _NeedIdentifiedSource<T> {
    Sync<T, IRI> source(IRI iri);

    Sync<T, Path> source(Path path);

    <S> Sync<T, S> source(Source<S> source);

    Sync<T, IRI> source(String iri);
}

interface _NeedTarget {
    NeedSourceOrBase<Dataset> target(Dataset dataset);

    NeedSourceOrBase<Graph> target(Graph graph);

    <T> NeedSourceOrBase<T> target(Target<T> target);
}

interface _NeedTargetOrRDF extends _NeedTarget, _OptionalRDF {
}

interface _OptionalBase<T> {
    NeedSourceBased<T> base(IRI iri);

    NeedSourceBased<T> base(String iri);
}

interface _OptionalRDF {
    OptionalTarget rdf(RDF rdf);
}
