package org.apache.commons.rdf.api.io;

import java.io.IOException;
import java.io.OutputStream;

public interface WriterTarget {
    OutputStream outputStream() throws IOException;
}
