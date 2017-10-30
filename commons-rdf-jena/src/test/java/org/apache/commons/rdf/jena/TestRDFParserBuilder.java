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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.rdf.api.Graph;
import org.apache.commons.rdf.api.RDFSyntax;
import org.apache.commons.rdf.experimental.RDFParser.ParseResult;
import org.apache.commons.rdf.jena.experimental.JenaRDFParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestRDFParserBuilder {

    private Path turtleFile;

    @Before
    public void preparePath() throws IOException {
        turtleFile = Files.createTempFile("commonsrdf", "test.ttl");
        Files.copy(getClass().getResourceAsStream("/D.ttl"), turtleFile, StandardCopyOption.REPLACE_EXISTING);
    }

    @After
    public void deletePath() throws IOException {
        if (turtleFile != null) {
            Files.deleteIfExists(turtleFile);
        }
    }

    @Test
    public void parseTurtle() throws Exception {
        try (final Graph g = new JenaRDF().createGraph()) {
            final Future<ParseResult> gFuture = new JenaRDFParser().contentType(RDFSyntax.TURTLE).source(turtleFile)
                    .target(g).parse();
            gFuture.get(5, TimeUnit.SECONDS);
            assertEquals(3, g.size());
        }
    }
}
