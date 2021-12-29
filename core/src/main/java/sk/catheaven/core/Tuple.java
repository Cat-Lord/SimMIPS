package sk.catheaven.core;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import sk.catheaven.model.TupleImpl;
import sk.catheaven.utils.TupleDeserializer;

@JsonDeserialize(as = TupleImpl.class, using = TupleDeserializer.class)
public interface Tuple<A, B> {
    A getLeft();

    B getRight();
}
