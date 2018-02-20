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

import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.io.NullWriterConfig.SnapshotWriterConfig;

@SuppressWarnings("rawtypes")
public interface WriterConfig {
	Optional<WriterSource> source();
	Optional<WriterTarget> target();
	Optional<RDFSyntax> syntax();
	Optional<RDF> rdf();
	Map<Option, Object> options();
	
	WriterConfig withSyntax(RDFSyntax syntax);
	WriterConfig withSource(WriterSource source);
	WriterConfig withTarget(WriterTarget target);
	WriterConfig withRDF(RDF rdf);

	<V> WriterConfig withOption(Option<V> o, V v);	
	
	static WriterConfig immutable() {
		return new NullWriterConfig();
	}

	static WriterConfig mutable() {		
		return new MutableWriterConfig();
	}
	
	default WriterConfig asMutableConfig() {
		if (this instanceof MutableWriterConfig) {
			return this;
		} else {
			return new MutableWriterConfig(this);
		}
	}
	
	default ImmutableWriterConfig asImmutableConfig() {
		if (this instanceof ImmutableWriterConfig) {
			return (ImmutableWriterConfig) this;
		} else {
			return new SnapshotWriterConfig(this);
		}
	}
	
	interface ImmutableWriterConfig extends WriterConfig, Serializable {} 

}
