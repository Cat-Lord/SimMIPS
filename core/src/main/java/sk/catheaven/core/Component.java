package sk.catheaven.core;

import sk.catheaven.model.Data;

import java.util.Map;

public interface Component {
    String getLabel();
    Map<String, Data> getInputs();
    Map<String, Data> getOutputs();
    Map<String, Data> getSelectors();

    boolean setInput(String inputLabel, Data data);
    boolean setOutput(String outputLabel, Data data);
    Data getInput(String inputLabel);
    Data getOutput(String outputLabel);
    void execute();
    void reset();

    boolean isSingleInput();
    boolean isSingleOutput();
}
