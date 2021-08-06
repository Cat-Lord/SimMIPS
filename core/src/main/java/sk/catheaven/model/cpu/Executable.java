package sk.catheaven.model.cpu;

import sk.catheaven.model.Data;

public interface Executable {
    boolean setInput(String inputLabel, Data data);
    boolean setOutput(String outputLabel, Data data);
    Data getInput(String inputLabel);
    Data getOutput(String outputLabel);
    void execute();
}
