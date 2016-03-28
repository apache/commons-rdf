package org.apache.commons.rdf.jena;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.ParseException;

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
	protected void parseSynchronusly() throws IOException, IllegalStateException, ParseException {
		StreamRDF dest = JenaCommonsRDF.streamJenaToCommonsRDF(rdfTermFactory.get(), intoGraph.get());

		Lang lang = contentTypeSyntax.flatMap(JenaCommonsRDF::rdfSyntaxToLang).orElse(null);
		String baseStr = base.map(IRI::getIRIString).orElse(null);

		if (sourceIri.isPresent()) {
			RDFDataMgr.parse(dest, sourceIri.get().toString(), baseStr, lang, null);
		} else if (sourceFile.isPresent()) {
			try (InputStream s = Files.newInputStream(sourceFile.get())) {
				RDFDataMgr.parse(dest, s, baseStr, lang, null);
			}
		} else {
			RDFDataMgr.parse(dest, sourceInputStream.get(), baseStr, lang, null);
		}
	}

}
