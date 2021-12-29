package sk.catheaven.core.instructions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import sk.catheaven.model.instructions.InstructionImpl;

import java.util.List;
import java.util.Map;

@JsonDeserialize(as = InstructionImpl.class)
public interface Instruction {
    InstructionType getType();

    String getMnemo();

    List<ArgumentType> getArguments();

    Map<String, String> getFields();

    String getDescription();
}
