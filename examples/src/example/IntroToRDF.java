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
package example;

import org.apache.commons.rdf.api.*;
import org.apache.commons.rdf.simple.SimpleRDF;

public class IntroToRDF {
    public static void main(String[] args) {
        RDF rdf = new SimpleRDF();

        IRI alice = rdf.createIRI("Alice");
        System.out.println(alice.ntriplesString());

        IRI knows = rdf.createIRI("knows");
        IRI bob = rdf.createIRI("Bob");

        Triple aliceKnowsBob = rdf.createTriple(alice, knows, bob);
        System.out.println(aliceKnowsBob.getSubject().ntriplesString());

        System.out.println(aliceKnowsBob);

        Graph graph = rdf.createGraph();
        graph.add(aliceKnowsBob);

        IRI charlie = rdf.createIRI("Charlie");

        IRI plays = rdf.createIRI("plays");

        IRI football = rdf.createIRI("Football");
        IRI tennis = rdf.createIRI("Tennis");

        graph.add(alice, knows, charlie);
        graph.add(alice, plays, tennis);
        graph.add(bob, knows, charlie);
        graph.add(bob, plays, football);
        graph.add(charlie, plays, tennis);

        System.out.println("Who plays Tennis?");
        for (Triple triple : graph.iterate(null, plays, tennis)) {
            System.out.println(triple.getSubject());
            System.out.println(plays.equals(triple.getPredicate()));
            System.out.println(tennis.equals(triple.getObject()));
        }

        System.out.println("Who does Alice know?");
        for (Triple triple : graph.iterate(alice, knows, null)) {
            System.out.println(triple.getObject());
        }


        System.out.println("Does Alice anyone that plays Football?");
        for (Triple triple : graph.iterate(alice, knows, null)) {
            RDFTerm aliceFriend = triple.getObject();
            if (! (aliceFriend instanceof BlankNodeOrIRI)) {
                continue;
            }
            if (graph.contains( (BlankNodeOrIRI)aliceFriend, plays, football)) {
                System.out.println("Yes, " + aliceFriend);
            }
        }

    }
}
