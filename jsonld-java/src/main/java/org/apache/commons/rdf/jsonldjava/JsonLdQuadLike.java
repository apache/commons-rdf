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
package org.apache.commons.rdf.jsonldjava;

import java.util.Optional;

import org.apache.commons.rdf.api.QuadLike;
import org.apache.commons.rdf.api.RDFTerm;

import com.github.jsonldjava.core.RDFDataset.Quad;

public interface JsonLdQuadLike<S extends RDFTerm, P extends RDFTerm, O extends RDFTerm, G extends RDFTerm> extends QuadLike<S,P,O,G> {
	
	public Quad asJsonLdQuad();
	
	class JsonLdQuadLikeImpl<S extends RDFTerm, P extends RDFTerm, O extends RDFTerm, G extends RDFTerm> implements JsonLdQuadLike<S,P,O,G> {
		
		private static JsonLdRDFTermFactory rdfTermFactory = new JsonLdRDFTermFactory();
		
		private final Quad quad;
		private String blankNodePrefix;
		
		JsonLdQuadLikeImpl(Quad jsonldQuad, String blankNodePrefix) {
			this.quad = jsonldQuad;
			this.blankNodePrefix = blankNodePrefix;			
		}
	
		@SuppressWarnings("unchecked")	
		@Override
		public Optional<G> getGraphName() {
			G g = (G) rdfTermFactory.asTerm(quad.getGraph(), blankNodePrefix);
			return Optional.ofNullable(g);
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public S getSubject() {
			return (S) rdfTermFactory.asTerm(quad.getSubject(), blankNodePrefix);
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public P getPredicate() {
			return (P) rdfTermFactory.asTerm(quad.getPredicate(), blankNodePrefix);
		}
	
		@SuppressWarnings("unchecked")
		@Override
		public O getObject() {
			return (O) rdfTermFactory.asTerm(quad.getObject(), blankNodePrefix);
		}
	
		public Quad asJsonLdQuad() {
			return quad;
		}
	}

}