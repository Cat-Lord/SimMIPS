package sk.catheaven.model.instructions;

import sk.catheaven.core.Data;
import sk.catheaven.core.instructions.AssembledInstruction;
import sk.catheaven.core.instructions.Instruction;

/**
 * Stores original line of code along with its instruction code, instruction format, line number and address.
 * Used to represent assembled program instruction. Can be stored in instruction memory component.
 * @author catlord
 */
public class AssembledInstructionImpl implements AssembledInstruction {
    private final int lineIndex;
    private final Instruction instruction;
    private final String lineOfCode;        // line of code as entered by the user (for example 'sw r4,0(r3)')
    private final Data instructionCode;
    private final Data address;
    
    public AssembledInstructionImpl(int lineIndex, String lineOfCode, Instruction instruction, Data instructionCode, Data address) {
        this.lineIndex = lineIndex;
        this.instruction = instruction;
        this.lineOfCode = lineOfCode;
        this.instructionCode = instructionCode;
        this.address = address;
    }
    
    @Override
    public Data getIcode() {
        return instructionCode;
    }
    
    @Override
    public int getLineIndex() {
        return lineIndex;
    }
    
    @Override
    public Instruction getInstruction() {
        return instruction;
    }
    
    @Override
    public String getLineOfCode() {
        return lineOfCode;
    }
    
    @Override
    public Data getInstructionCode() {
        return instructionCode;
    }
    
    @Override
    public Data getAddress() {
        return address;
    }

}
