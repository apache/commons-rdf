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
package org.apache.commons.rdf.api.fluentparser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.apache.commons.rdf.api.Dataset;
import org.apache.commons.rdf.api.IRI;
import org.apache.commons.rdf.api.RDF;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.api.io.Option;
import org.apache.commons.rdf.api.io.ParserConfig;
import org.apache.commons.rdf.api.io.ParserConfig.ImmutableParserConfig;
import org.apache.commons.rdf.api.io.ParserSource;
import org.apache.commons.rdf.api.io.ParserTarget;
import org.junit.Test;
import org.mockito.Mockito;

public class TestParserConfig {

	RDF rdf = Mockito.mock(RDF.class);
	ParserSource<Path> source = ParserSource.fromPath(Paths.get("test")); 
	Dataset targetDataset = Mockito.mock(Dataset.class);
	ParserTarget<Dataset> target = ParserTarget.toDataset(targetDataset);
	IRI base = Mockito.mock(IRI.class);
	RDFSyntax syntax = RDFSyntax.NQUADS;
	@SuppressWarnings("unchecked")
	Option<String> option = Mockito.mock(Option.class); 
	String optionV = "Hello";

	@Test
	public void mutable() throws Exception {
		ParserConfig p = ParserConfig.mutable();
		assertNotSame(p, ParserConfig.mutable());
				
		assertFalse(p.rdf().isPresent());
		assertFalse(p.source().isPresent());
		assertFalse(p.target().isPresent());
		assertFalse(p.base().isPresent());
		assertFalse(p.syntax().isPresent());
		assertTrue(p.options().isEmpty());
		Map<Option, Object> options = p.options();
		assertSame(p, p.asMutableConfig());
				
		assertSame(p, p.withRDF(rdf));
		assertSame(p, p.withSource(source));
		assertSame(p, p.withTarget(target));
		assertSame(p, p.withBase(base));
		assertSame(p, p.withSyntax(syntax));
		assertSame(p, p.withOption(option, optionV));
		
		assertSame(rdf, p.rdf().get());
		assertSame(source, p.source().get());
		assertSame(base, p.base().get());
		assertSame(target, p.target().get());
		assertSame(syntax, p.syntax().get());		
		assertFalse(p.options().isEmpty());
		assertSame(options, p.options());
		assertEquals(optionV, p.options().get(option));
		assertSame(p, p.asMutableConfig());
	}
	
	@Test
	public void mutableAsImmutable() throws Exception {
		ParserConfig mutable = ParserConfig.mutable();
		ImmutableParserConfig empty = mutable.asImmutableConfig();
		
		// Set some values in mutable
		mutable.withRDF(rdf).withBase(base).withSource(source);
		mutable.withTarget(target).withSyntax(syntax);
		mutable.withOption(option, optionV);
		// (already tested in mutable() above that these are preserved)
		
		// Our previous immutable snapshot is untouched
		assertFalse(empty.rdf().isPresent());
		assertFalse(empty.source().isPresent());
		assertFalse(empty.target().isPresent());
		assertFalse(empty.base().isPresent());
		assertFalse(empty.syntax().isPresent());
		assertTrue(empty.options().isEmpty());
		assertNotSame(empty.options(), mutable.options());

		// new snapshot
		ImmutableParserConfig everything = mutable.asImmutableConfig();		
		// reset mutable to ensure new snapshot is not touched
		mutable.withBase(null).withRDF(null).withSource(null);
		mutable.withSyntax(null).withTarget(null);
		mutable.withOption(option, null);
		
		// Now let's check our snapshot was preserved
		assertSame(rdf, everything.rdf().get());
		assertSame(source, everything.source().get());
		assertSame(base, everything.base().get());
		assertSame(target, everything.target().get());
		assertSame(syntax, everything.syntax().get());		
		assertFalse(everything.options().isEmpty());
		assertEquals(optionV, everything.options().get(option));		
		assertNotSame(everything.options(), mutable.options());		
	}

	@Test
	public void immutable() throws Exception {
		ParserConfig empty = ParserConfig.immutable();
		assertSame(empty, empty.asImmutableConfig());
		
		ParserConfig withRDF = empty.withRDF(rdf);
		assertSame(rdf, withRDF.rdf().get());
		assertNotSame(empty, withRDF); 
		
		ParserConfig withSource = withRDF.withSource(source);
		assertSame(source, withSource.source().get());
		assertNotSame(withRDF, withSource);
		
		ParserConfig withTarget = withSource.withTarget(target);
		assertSame(target, withTarget.target().get());
		assertNotSame(withSource, withTarget);
		
		ParserConfig withBase = withTarget.withBase(base);
		assertSame(base, withBase.base().get());
		assertNotSame(withTarget, withBase);
		
		ParserConfig withSyntax = withBase.withSyntax(syntax);
		assertSame(syntax, withSyntax.syntax().get());		
		assertNotSame(withBase, withSyntax);
		
		ParserConfig withOption = withSyntax.withOption(option, optionV);
		assertFalse(withOption.options().isEmpty());
		assertEquals(optionV, withOption.options().get(option));
		assertNotSame(withSyntax, withOption);

		// Our initial empty remains the same
		assertFalse(empty.rdf().isPresent());
		assertFalse(empty.source().isPresent());
		assertFalse(empty.target().isPresent());
		assertFalse(empty.base().isPresent());
		assertFalse(empty.syntax().isPresent());
		assertTrue(empty.options().isEmpty());
		Map<Option, Object> options = empty.options();
		
		// And check final withOption has propagated
		// all options
		assertSame(rdf, withOption.rdf().get());
		assertSame(source, withOption.source().get());
		assertSame(base, withOption.base().get());
		assertSame(target, withOption.target().get());
		assertSame(syntax, withOption.syntax().get());		
		assertNotSame(withOption, empty.options());
	}
	@Test
	public void immutableAsMutable() throws Exception {
		ParserConfig immutable = ParserConfig.immutable();
		ParserConfig mutable = immutable.asMutableConfig();
		
		// Set some values in immutable
		ParserConfig everything = immutable.withRDF(rdf)
				.withBase(base).withSource(source).withTarget(target)
				.withSyntax(syntax).withOption(option, optionV);
		// (already tested in mutable() above that these are preserved)
		
		// Our previous mutable snapshot is untouched
		assertFalse(mutable.rdf().isPresent());
		assertFalse(mutable.source().isPresent());
		assertFalse(mutable.target().isPresent());
		assertFalse(mutable.base().isPresent());
		assertFalse(mutable.syntax().isPresent());
		assertTrue(mutable.options().isEmpty());
		assertNotSame(mutable.options(), immutable.options());

		
		// new mutable
		ParserConfig mutable2 = everything.asMutableConfig();
		assertSame(mutable2, mutable2.asMutableConfig());
		assertNotSame(mutable, mutable2);
		
		// Below should be a no-op as everything is immutable
		everything.withRDF(null).withSyntax(null).withBase(null).withTarget(null);
		
		// Now let's check everything was preserved
		assertSame(rdf, mutable2.rdf().get());
		assertSame(source, mutable2.source().get());
		assertSame(base, mutable2.base().get());
		assertSame(target, mutable2.target().get());
		assertSame(syntax, mutable2.syntax().get());		
		assertFalse(mutable2.options().isEmpty());
		assertEquals(optionV, mutable2.options().get(option));		
		assertNotSame(mutable2.options(), mutable.options());
		
		// changing mutable2 does not modify the immutable
		mutable2.withRDF(null).withSyntax(null).withBase(null)
			.withTarget(null).withOption(option, null);		
		
		assertSame(rdf, everything.rdf().get());
		assertSame(syntax, everything.syntax().get());		
		assertSame(base, everything.base().get());
		assertSame(target, everything.target().get());
		assertEquals(optionV, everything.options().get(option));		
		assertNotSame(mutable2.options(), mutable.options());
		
		
	}
	
	
}
