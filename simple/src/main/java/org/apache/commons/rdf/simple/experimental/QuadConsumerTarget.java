package org.apache.commons.rdf.simple.experimental;

import java.util.function.Consumer;

import org.apache.commons.rdf.api.Quad;
import org.apache.commons.rdf.experimental.ParserFactory.Target;

public class QuadConsumerTarget implements Target<Consumer<Quad>> {

    private final Consumer<? super Quad> consumer;

    public QuadConsumerTarget(Consumer<? super Quad> consumer) {
        this.consumer = consumer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Consumer<Quad> target() {
        return (Consumer<Quad>) consumer;
    }

}
