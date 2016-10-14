/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

package org.apache.commons.rdf.jena.experimental;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.function.Consumer;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.QuadLike;
import org.apache.commons.rdf.api.RDFTerm;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.api.TripleLike;
import org.apache.commons.rdf.experimental.RDFParser;
import org.apache.commons.rdf.jena.JenaGraph;
import org.apache.commons.rdf.jena.JenaRDFTermFactory;
import org.apache.commons.rdf.simple.experimental.AbstractRDFParser;
import org.apache.jena.graph.Graph;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.StreamRDFLib;

public class JenaRDFParser extends AbstractRDFParser<JenaRDFParser> implements RDFParser {

	private Consumer<TripleLike> generalizedConsumerTriple;
	private Consumer<QuadLike<RDFTerm>> generalizedConsumerQuad;

	protected RDFTermFactory createRDFTermFactory() {
		return new JenaRDFTermFactory();
	}

	public JenaRDFParser targetGeneralizedTriple(Consumer<TripleLike> consumer) {
		JenaRDFParser c = this.clone();
		c.resetTarget();		
		c.generalizedConsumerTriple = consumer;
		return c;
	}

	public JenaRDFParser targetGeneralizedQuad(Consumer<QuadLike<RDFTerm>> consumer) {
		JenaRDFParser c = this.clone();
		c.resetTarget();		
		c.generalizedConsumerQuad = consumer;
		return c;
	}
	
	@Override
	protected void resetTarget() {		
		super.resetTarget();
		this.generalizedConsumerTriple = null;
		this.generalizedConsumerQuad = null;
	}
	
	@Override
	protected void parseSynchronusly() throws IOException {
		StreamRDF dest;
		JenaRDFTermFactory jenaFactory = getJenaFactory();
		if (getTargetGraph().isPresent() && getTargetGraph().get() instanceof JenaGraph) {
			Graph jenaGraph = ((JenaGraph) getTargetGraph().get()).asJenaGraph();
			dest = StreamRDFLib.graph(jenaGraph);
		} else {
			if (generalizedConsumerQuad != null) {				
				dest = jenaFactory.streamJenaToGeneralizedQuad(generalizedConsumerQuad);			
			} else if (generalizedConsumerTriple != null) {				
				dest = jenaFactory.streamJenaToGeneralizedTriple(generalizedConsumerTriple);			
			} else {
				dest = JenaRDFTermFactory.streamJenaToCommonsRDF(getRdfTermFactory().get(), getTarget());
			}
		}

		Lang lang = getContentTypeSyntax().flatMap(jenaFactory::rdfSyntaxToLang).orElse(null);
		String baseStr = getBase().map(IRI::getIRIString).orElse(null);

		if (getSourceIri().isPresent()) {
			RDFDataMgr.parse(dest, getSourceIri().get().toString(), baseStr, lang, null);
		} else if (getSourceFile().isPresent()) {
			try (InputStream s = Files.newInputStream(getSourceFile().get())) {
				RDFDataMgr.parse(dest, s, baseStr, lang, null);
			}
		} else {
			RDFDataMgr.parse(dest, getSourceInputStream().get(), baseStr, lang, null);
		}
	}

	private JenaRDFTermFactory getJenaFactory() {
		return (JenaRDFTermFactory) getRdfTermFactory()
				.filter(JenaRDFTermFactory.class::isInstance)
				.orElseGet(this::createRDFTermFactory);		
	}

}
