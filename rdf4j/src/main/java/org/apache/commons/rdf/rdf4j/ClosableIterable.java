package org.apache.commons.rdf.rdf4j;

/**
 * An {@link Iterable} which should be {@link #close()}d after use.
 * <p>
 * A good pattern to use this iterator is with an outer try-with-resources
 * block: 
 * <code>
 * for (ClosableIterable&lt;Triple&gt; triples : graph.iterate()) {
 *     for (Triple t : triples) {
 *       return t; // OK to terminate for-loop early
 *     }
 * }
 * </code> 
 * The above will ensure that underlying resources are closed even if
 * the iteration does not exhaust all triples.
 *
 * @param <T> type of elements returned by the iterator 
 */
public interface ClosableIterable<T> extends Iterable<T>, AutoCloseable {

}
