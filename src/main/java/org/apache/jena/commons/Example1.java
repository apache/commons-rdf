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

package org.apache.jena.commons;

import org.apache.commons.rdf.api.Graph ;
import org.apache.commons.rdf.api.RDFTermFactory ;
import org.apache.jena.atlas.logging.LogCtl ;
import org.apache.jena.riot.RDFDataMgr ;

public class Example1 {
    static { LogCtl.setCmdLogging();}
    
    public static void main(String... args) {
        main_ex2() ;
    }
    
    public static void main_ex1(String... args) {
        RDFTermFactory rft = new RDFTermFactoryJena() ;
        ToGraph dest = new ToGraph(rft) ;
        RDFDataMgr.parse(dest, "D.ttl") ;
        dest.getGraph().getTriples().forEach(System.out::println) ;
    }

    public static void main_ex2(String... args) {
        RDFTermFactory rft = new RDFTermFactoryJena() ;
        Graph graph = rft.createGraph() ;
        ToGraph dest = new ToGraph(graph, rft) ;
        RDFDataMgr.parse(dest, "D.ttl") ;
        graph.getTriples().forEach(System.out::println) ;
    }

    
}

