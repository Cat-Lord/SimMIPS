package sk.catheaven.service;

import sk.catheaven.model.Data;
import sk.catheaven.model.instructions.Instruction;

public class AssembledInstruction {
    private final int lineIndex;
    private final Instruction instruction;
    private final String instructionMnemo;
    private final Data instructionCode;
    private final Data address;
    
    public AssembledInstruction(int lineIndex, Instruction instruction, String instructionMnemo, Data instructionCode, Data address) {
        this.lineIndex = lineIndex;
        this.instruction = instruction;
        this.instructionMnemo = instructionMnemo;
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
    
    public String getInstructionMnemo() {
        return instructionMnemo;
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
                null,
                "NOP",
                new Data(),
                new Data()
        );
    }
}
