package org.apache.commons.rdf.api.io;

import java.util.Set;

import org.apache.commons.rdf.api.RDFSyntax;

interface _SupportedSyntaxes {
    /**
     * Get set of syntaxes supported by this factory.
     * <p>
     * The returned syntaxes can be used with {@link #syntax(RDFSyntax)} and may
     * be used by this factory if no syntax is given.
     * <p>
     * Note that the factory may support additional syntaxes not returned
     * in this set.
     * 
     * @return Set of supported syntaxes
     */
    Set<RDFSyntax> supportedSyntaxes();
    
    /**
     * Use the specified RDF syntax
     * 
     * @param syntax RDFSyntax
     * @return Builder that uses specified syntax
     */
    Object syntax(RDFSyntax syntax);
}
