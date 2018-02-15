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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;

import org.apache.commons.rdf.api.IRI;

final class IRIParserSource implements ParserSource<IRI> {
	private final IRI iri;

	IRIParserSource(IRI iri) {
		this.iri = iri;
	}

	@Override
	public IRI src() {
		return iri;
	}

	@Override
	public InputStream inputStream() throws IOException {
		return new URL(iri.getIRIString()).openStream();
	}

	@Override
	public Optional<IRI> iri() {
		return Optional.of(iri);
	}
}