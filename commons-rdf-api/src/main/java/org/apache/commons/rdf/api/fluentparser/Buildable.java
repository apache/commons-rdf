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
package org.apache.commons.rdf.api.fluentparser;

import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.io.Option;
import org.apache.commons.rdf.api.io.Option.RequiredOption;
import org.apache.commons.rdf.api.io.Parser;
import org.apache.commons.rdf.api.io.ParserConfig;
import org.apache.commons.rdf.api.io.ParserConfig.ImmutableParserConfig;

public interface Buildable {
    /**
     * Return an immutable builder at the current state. The returned builder
     * can be re-used multiple times in a thread-safe way.
     * 
     * @return An immutable builder
     */
	Buildable immutable();
	
	/**
	 * Build the (potentially partial) parser config.
	 * <p>
	 * The parser configuration can be further modified, e.g.
	 * {@link ParserConfig#withOption(Option, Object)} or used with a {@link Parser}
	 * as retrieved from {@link RDF#parser(org.apache.commons.rdf.api.RDFSyntax)}.
	 * <p>
	 * The returned {@link ParserConfig} is immutable and is not affected by any
	 * further modifications to this builder.
	 * 
	 * @return An Immutable {@link ParserConfig} as configured by the current builder. 
	 */
	ImmutableParserConfig build();
    	
    /**
     * Return a builder with the given option set.
     * <p>
     * Note that implementations of {@link Parser} may support different
     * vendor-specific {@link Option} types, and are free to ignore the set
     * option (unless it is a {@link RequiredOption}).
     * <p>
     * It is undefined if setting multiple values for the same (equal) option
     * are accumulative or overriding.
     * 
     * @param <V> The type of the {@link Option} value 
     * @param option Option to set
     * @param value Value to set for option
     * @return A builder with the given option set
     */
    <V> Buildable option(Option<V> option, V value);

}
