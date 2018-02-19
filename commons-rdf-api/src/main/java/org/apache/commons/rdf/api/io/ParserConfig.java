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

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.io.NullParserConfig.SnapshotParserConfig;

@SuppressWarnings("rawtypes")
interface ParserConfig {
	Optional<ParserSource> source();
	Optional<IRI> base();
	Optional<ParserTarget> target();
	Optional<RDFSyntax> syntax();
	Optional<RDF> rdf();
	Map<Option, Object> options();
	
	ParserConfig withSyntax(RDFSyntax syntax);

	ParserConfig withSource(ParserSource source);

	ParserConfig withTarget(ParserTarget target);

	ParserConfig withRDF(RDF rdf);

	ParserConfig withBase(IRI base);

	<V> ParserConfig withOption(Option<V> o, V v);	
	
	static ParserConfig immutable() {
		return new NullParserConfig();
	}

	static ParserConfig mutable() {
		return new MutableParserConfig();
	}
	
	default ParserConfig asMutableConfig() {
		if (this instanceof MutableParserConfig) {
			return this;
		} else {
			return new MutableParserConfig(this);
		}
	}
	
	default ParserConfig asImmutableConfig() {
		if (this instanceof ImmutableParserConfig) {
			return this;
		} else {
			return new SnapshotParserConfig(this);
		}
	}
	
	interface ImmutableParserConfig extends ParserConfig, Serializable {} 

}
