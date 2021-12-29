package sk.catheaven.core.instructions;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import sk.catheaven.core.Data;
import sk.catheaven.model.DataImpl;
import sk.catheaven.model.instructions.AssembledInstructionImpl;

@JsonDeserialize(as = AssembledInstructionImpl.class)
public interface AssembledInstruction {
    static AssembledInstruction getNOPInstructionInstance() {
        return new AssembledInstructionImpl(
                0,
                "NOP",
                null,
                new DataImpl(),
                new DataImpl()
        );
    }

    Data getIcode();

    int getLineIndex();

    Instruction getInstruction();

    String getLineOfCode();

    Data getInstructionCode();

    Data getAddress();
}
