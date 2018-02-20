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
package org.apache.commons.rdf.jena;

import java.util.function.Consumer;

import org.apache.commons.rdf.api.QuadLike;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.TripleLike;
import org.apache.commons.rdf.api.io.Parsed;
import org.apache.commons.rdf.api.io.Parser;
import org.apache.commons.rdf.api.io.ParserConfig;

public class JenaRDFParser implements Parser {

    private Consumer<TripleLike> generalizedConsumerTriple;
    private Consumer<QuadLike<RDFTerm>> generalizedConsumerQuad;

	public JenaRDFParser() {
	}

	@Override
	public Parsed parse(ParserConfig config) {
		if (! config.source().isPresent()) {
			throw new IllegalStateException("No source target configured");
		}
		if (! config.target().isPresent()) {
			throw new IllegalStateException("No parse target configured");
		}
		if (! config.syntax().isPresent() && ! config.source().get().iri().isPresent()) {
			throw new IllegalStateException("Can't guess syntax when source has no iri");			
		}		
		if (syntaxNeedsBase(config) && 
				! config.base().isPresent() && 
				! config.source().get().iri().isPresent()) {			
			throw new IllegalStateException("Can't guess syntax when source has no iri");			
		}
		
		
		
	}

	private boolean syntaxNeedsBase(ParserConfig config) {
		if (! config.syntax().isPresent()) {
			// guessing without source iri already covered
			return false;
		}
		RDFSyntax s = config.syntax().get();
		// If it's not Ntriples or Nquads, then we need base URI
		return ! (s.equals(RDFSyntax.NTRIPLES) || s.equals(RDFSyntax.NQUADS));
	}

}
 