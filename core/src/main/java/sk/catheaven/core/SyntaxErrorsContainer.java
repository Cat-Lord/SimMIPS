package sk.catheaven.core;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import sk.catheaven.model.SyntaxErrorsContainerImpl;
import sk.catheaven.core.Tuple;

import java.util.List;

@JsonDeserialize(as = SyntaxErrorsContainerImpl.class)
public interface SyntaxErrorsContainer {
    void addError(int lineNumber, String errorMessage);

    void addError(String errorMessage);

    void addAllLineErrors(List<Tuple<Integer, String>> errors);

    void addAllMessageErrors(List<String> errorMessages);

    List<Tuple<Integer, String>> getLineErrors();

    List<String> getMessageErrors();

    void clear();

    int size();

    boolean isEmpty();
}
