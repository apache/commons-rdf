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
package org.apache.commons.rdf.jena;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDFParserBuilder;
import org.apache.commons.rdf.api.RDFTermFactory;
import org.apache.commons.rdf.simple.AbstractRDFParserBuilder;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.system.StreamRDF;

public class JenaRDFParserBuilder extends AbstractRDFParserBuilder implements RDFParserBuilder {

	protected RDFTermFactory createRDFTermFactory() {
		return new RDFTermFactoryJena();
	}

	@Override
	protected void parseSynchronusly() throws IOException {
		StreamRDF dest = JenaCommonsRDF.streamJenaToCommonsRDF(getRdfTermFactory().get(), getIntoGraph().get());

		Lang lang = getContentTypeSyntax().flatMap(JenaCommonsRDF::rdfSyntaxToLang).orElse(null);
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

}
