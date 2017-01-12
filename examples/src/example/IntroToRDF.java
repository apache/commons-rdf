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

import java.util.Optional;
import org.apache.commons.rdf.api.*;
import org.apache.commons.rdf.simple.SimpleRDF;
import org.apache.commons.rdf.simple.Types;

/** See http://commonsrdf.incubator.apache.org/introduction.html
 */
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
              System.out.println("Yes, it is " + aliceFriend);
          }
      }

      Literal aliceName = rdf.createLiteral("Alice W. Land");
      IRI name = rdf.createIRI("name");
      graph.add(alice, name, aliceName);

      Optional<? extends Triple> nameTriple = graph.stream(alice, name, null).findAny();
      if (nameTriple.isPresent()) {
          System.out.println(nameTriple.get());
      }


      graph.stream(alice, name, null)
              .findAny().map(Triple::getObject)
              .filter(obj -> obj instanceof Literal)
              .map(literalName -> ((Literal)literalName).getLexicalForm())
              .ifPresent(System.out::println);

      IRI playerRating = rdf.createIRI("playerRating");
      Literal aliceRating = rdf.createLiteral("13.37", Types.XSD_FLOAT);
      graph.add(alice, playerRating, aliceRating);

      Literal footballInEnglish = rdf.createLiteral("football", "en");
      Literal footballInNorwegian = rdf.createLiteral("fotball", "no");
      graph.add(football, name, footballInEnglish);
      graph.add(football, name, footballInNorwegian);

      Literal footballInAmericanEnglish = rdf.createLiteral("soccer", "en-US");
      graph.add(football, name, footballInAmericanEnglish);

      BlankNode someone = rdf.createBlankNode();
      graph.add(charlie, knows, someone);
      graph.add(someone, plays, football);

      BlankNode someoneElse = rdf.createBlankNode();
      graph.add(charlie, knows, someoneElse);

      for (Triple heKnows : graph.iterate(charlie, knows, null)) {
          if (! (heKnows.getObject() instanceof BlankNodeOrIRI)) {
              continue;
          }
          BlankNodeOrIRI who = (BlankNodeOrIRI)heKnows.getObject();
          System.out.println("Charlie knows "+ who);
          for (Triple whoPlays : graph.iterate(who, plays, null)) {
              System.out.println("  who plays " + whoPlays.getObject());
          }
      }

      // Delete previous BlankNode statements
      graph.remove(null,null,someone);
      graph.remove(someone,null,null);

      // no Java variable for the new BlankNode instance
      graph.add(charlie, knows, rdf.createBlankNode("someone"));
      // at any point later (with the same RDF instance)
      graph.add(rdf.createBlankNode("someone"), plays, football);

      for (Triple heKnows : graph.iterate(charlie, knows, null)) {
          if (! (heKnows.getObject() instanceof BlankNodeOrIRI)) {
              continue;
          }
          BlankNodeOrIRI who = (BlankNodeOrIRI)heKnows.getObject();
          System.out.println("Charlie knows "+ who);
          for (Triple whoPlays : graph.iterate(who, plays, null)) {
              System.out.println("  who plays " + whoPlays.getObject());
          }
      }

  }
}
