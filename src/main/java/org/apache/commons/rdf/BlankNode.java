package org.apache.commons.rdf;

public interface BlankNode extends Resource {
    /**
     * Return a label for the blank node.
     * This is not a serialization/syntax label.
     * It should be uniquely identifying within this system
     * but has no uniqueness guarantees other than that.
     *
     * Sergio: toString()?
     */
    Object getLabel();

}
