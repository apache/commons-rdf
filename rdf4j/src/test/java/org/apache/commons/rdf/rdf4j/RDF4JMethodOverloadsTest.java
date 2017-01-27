package org.apache.commons.rdf.rdf4j;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Literal;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RDF4JMethodOverloadsTest {

    @Test
    public void testAsRDFTermOverloads() {
        RDF4J rdf4J = new RDF4J();

        final ValueFactory valueFactory = SimpleValueFactory.getInstance();

        final Value bNode = valueFactory.createBNode("b1");
        final Value iri = valueFactory.createIRI("http://ex.org");
        final Value literal = valueFactory.createLiteral("b1");

        assertEquals(rdf4J.asRDFTerm(bNode), rdf4J.asRDFTerm((BNode) bNode));
        assertEquals(rdf4J.asRDFTerm(iri), rdf4J.asRDFTerm((IRI) iri));
        assertEquals(rdf4J.asRDFTerm(literal), rdf4J.asRDFTerm((Literal) literal));
        assertEquals(rdf4J.asRDFTerm(bNode), rdf4J.asRDFTerm((Resource) bNode));
        assertEquals(rdf4J.asRDFTerm(iri), rdf4J.asRDFTerm((Resource) iri));
    }
}
