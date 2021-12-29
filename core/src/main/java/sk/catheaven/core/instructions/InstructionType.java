package sk.catheaven.core.instructions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import sk.catheaven.model.instructions.InstructionTypeImpl;

import java.util.List;

@JsonDeserialize(as = InstructionTypeImpl.class)
public interface InstructionType {
    String getLabel();

    List<Field> getFields();
}
