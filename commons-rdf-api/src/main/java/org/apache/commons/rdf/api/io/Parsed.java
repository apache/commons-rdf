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
package org.apache.commons.rdf.api.io;

public interface Parsed<S, T> {

	ParserSource<S> from();

	ParserTarget<T> into();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <S, T> Parsed<S, T> from(
			final ParserSource<S> from, 
			final ParserTarget<T> into) {
		return new ParsedImpl(from, into);
	}
}

class ParsedImpl<S, T> implements Parsed<S, T> {
	private ParserSource<S> from;
	private ParserTarget<T> into;

	ParsedImpl(final ParserSource<S> from, final ParserTarget<T> into) {
		this.from = from;
		this.into = into;
	}

	@Override
	public ParserSource<S> from() {
		return from;
	}

	@Override
	public ParserTarget<T> into() {
		return into;
	}
}
