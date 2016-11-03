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
package org.apache.commons.rdf.simple;

import static org.junit.Assert.*;

import org.apache.commons.rdf.api.AbstractDatasetTest;
import org.apache.commons.rdf.api.RDF;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test SimpleRDF with AbstractGraphTest
 */
public class SimpleDatasetTest extends AbstractDatasetTest {

    @Override
    public RDF createFactory() {
        return new SimpleRDF();
    }

    @Test
    public void datasetToString() {
        Assume.assumeNotNull(aliceName, companyName);
        //System.out.println(dataset);
        assertTrue(
                dataset.toString().contains("<http://example.com/alice> <http://xmlns.com/foaf/0.1/name> \"Alice\" <http://example.com/graph1> ."));
        assertTrue(dataset.toString().contains(" <http://xmlns.com/foaf/0.1/name> \"A company\" _:"));
        assertTrue(dataset.toString().contains("<http://example.com/alice> <http://xmlns.com/foaf/0.1/isPrimaryTopicOf> <http://example.com/graph1> ."));

    }

}
