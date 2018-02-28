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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public interface Parser {

	@SuppressWarnings("rawtypes")
	Parsed parse(ParserConfig config) throws IOException;

	@SuppressWarnings("rawtypes")
	default Future<Parsed> parseAsync(ParserConfig config) {
		return new DefaultAsyncParser(this, config).submit();
	}

	class DefaultAsyncParser {
		private static final ThreadGroup THEAD_GROUP = new ThreadGroup("Commons RDF async parser");
		private static final ExecutorService DEFAULT_EXECUTOR = Executors
				.newCachedThreadPool(r -> new Thread(THEAD_GROUP, r));
		
		private final Parser syncParser;
		private final ParserConfig config;

		DefaultAsyncParser(Parser parser, ParserConfig config) {
			this.syncParser = parser;
			this.config = config.asImmutableConfig();
		}
		
		Parsed parse() throws IOException {
			return syncParser.parse(config);			
		}

		Future<Parsed> submit() {
			return DEFAULT_EXECUTOR.submit(this::parse);
		}
	}
}
