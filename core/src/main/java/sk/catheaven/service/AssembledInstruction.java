package sk.catheaven.service;

import sk.catheaven.model.Data;

public class AssembledInstruction {
    public Data getIcode() {
        return new Data();
    }
    
    public static AssembledInstruction getNOPInstructionInstance() {
        return new AssembledInstruction();
    }
}
