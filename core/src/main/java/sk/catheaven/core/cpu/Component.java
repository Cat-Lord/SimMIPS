package sk.catheaven.core.cpu;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sk.catheaven.core.Data;
import sk.catheaven.model.cpu.ComponentImpl;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, defaultImpl = ComponentImpl.class)
@JsonSubTypes({
    @JsonSubTypes.Type(value = ComponentImpl.class)
})
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
