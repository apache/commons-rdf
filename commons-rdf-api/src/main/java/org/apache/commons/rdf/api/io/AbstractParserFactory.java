/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.rdf.api.io;

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.fluentparser.Async;
import org.apache.commons.rdf.api.fluentparser.NeedSourceBased;
import org.apache.commons.rdf.api.fluentparser.NeedSourceOrBase;
import org.apache.commons.rdf.api.fluentparser.NeedTargetOrRDF;
import org.apache.commons.rdf.api.fluentparser.NeedTargetOrRDFOrSyntax;
import org.apache.commons.rdf.api.fluentparser.OptionalTarget;
import org.apache.commons.rdf.api.fluentparser.Sync;

@SuppressWarnings({ "unchecked", "rawtypes" })
public final class AbstractParserFactory implements Cloneable, Serializable, NeedTargetOrRDF, NeedTargetOrRDFOrSyntax,
		NeedSourceOrBase, NeedSourceBased, OptionalTarget, Sync, Async {

	private static final long serialVersionUID = 1L;

	public AbstractParserFactory(RDF rdf) {
		
	}
	
	@Override
	public AbstractParserFactory clone() {
		try {
			AbstractParserFactory c = (AbstractParserFactory) super.clone();
			c.config = (ParserConfigImpl) config.clone();
			return c;
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException("AbstractParserFactory was not Cloneable", e);
		}
	}

	private boolean mutable = false;
	private ParserConfigImpl config = new ParserConfigImpl();

	@Override
	public NeedTargetOrRDF syntax(RDFSyntax syntax) {
		AbstractParserFactory c = mutable();
		c.config.withSyntax(syntax);
		return c;
	}

	private AbstractParserFactory mutable(boolean mutable) {
		if (this.mutable == mutable) {
			return this;
		} else {
			AbstractParserFactory c = clone();
			c.mutable = mutable;
			return c;
		}
	}

	private AbstractParserFactory mutable() {
		return mutable(true);
	}

	@Override
	public AbstractParserFactory build() {
		return mutable(false);
	}

	@Override
	public NeedSourceOrBase target(Dataset dataset) {
		return target(dataset::add);

	}

	@Override
	public NeedSourceOrBase<Graph> target(Graph graph) {
		return target(q -> {
			if (q.getGraphName().isPresent()) {
				// Only add if q is in default graph
				graph.add(q.asTriple());
			}
		});
	}

	@Override
	public <T> NeedSourceOrBase<T> target(ParserTarget<T> target) {
		AbstractParserFactory c = mutable();
		c.config.withTarget(target);
		return c;
	}

	@Override
	public NeedSourceBased base(IRI iri) {
		AbstractParserFactory c = mutable();
		c.config.withBase(iri);
		return c;
	}

	@Override
	public NeedSourceBased base(String iri) {
		AbstractParserFactory c = mutable();
		c.config.withBase(new IRIImpl(iri));
		return c;
	}

	@Override
	public Sync source(final IRI iri) {
		return source(new IRIParserSource(iri));
	}

	public Sync source(Path path) {
		return source(new PathParserSource(path));
	}

	@Override
	public OptionalTarget<Dataset> rdf(RDF rdf) {
		AbstractParserFactory c = mutable();
		c.config.withRDF(rdf);
		return c;
	}

	@Override
	public Sync source(ParserSource source) {
		AbstractParserFactory c = mutable();
		c.config.withSource(source);
		return c;
	}

	@Override
	public Sync source(String iri) {
		return source(new IRIImpl(iri));
	}

	@Override
	public AbstractParserFactory option(Option option, Object value) {
		AbstractParserFactory c = mutable();
		c.config.withOption(option, value);
		return c;
	}

	@Override
	public Future parseAsync() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Async async() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Async async(ExecutorService executor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parsed parse() {
		return null;
	}

	@Override
	public Sync source(InputStream is) {
		return source(new InputParserSource(is));
	}

}
