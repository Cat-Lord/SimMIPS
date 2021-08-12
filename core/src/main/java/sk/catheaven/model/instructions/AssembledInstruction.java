package sk.catheaven.model.instructions;

import sk.catheaven.model.Data;

/**
 * Stores original line of code along with its instruction code, instruction format, line number and address.
 * Used to represent assembled program instruction. Can be stored in instruction memory component.
 * @author catlord
 */
public class AssembledInstruction {
    private final int lineIndex;
    private final Instruction instruction;
    private final String lineOfCode;        // line of code as entered by the user (for example 'sw r4,0(r3)
    private final Data instructionCode;
    private final Data address;
    
    public AssembledInstruction(int lineIndex, String lineOfCode, Instruction instruction, Data instructionCode, Data address) {
        this.lineIndex = lineIndex;
        this.instruction = instruction;
        this.lineOfCode = lineOfCode;
        this.instructionCode = instructionCode;
        this.address = address;
    }
    
    public Data getIcode() {
        return instructionCode;
    }
    
    public int getLineIndex() {
        return lineIndex;
    }
    
    public Instruction getInstruction() {
        return instruction;
    }
    
    public String getLineOfCode() {
        return lineOfCode;
    }
    
    public Data getInstructionCode() {
        return instructionCode;
    }
    
    public Data getAddress() {
        return address;
    }
    
    // todo - actually create NOP
    public static AssembledInstruction getNOPInstructionInstance() {
        return new AssembledInstruction(
                0,
                "NOP",
                null,
                new Data(),
                new Data()
        );
    }
}
