/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionEssentials;

/**
 *
 * @author catlord
 */
public class AssembledInstruction {
	private final Instruction instruction;			// the instruction, this one refers to (for example "add" instruction)
	private final String originalInstruction;		// original instruction as entered by user (without comments)
	private final Data iCode;
	
	public AssembledInstruction(Instruction instruction, String originalInstruction, Data iCode){
		this.instruction = instruction;
		this.originalInstruction = originalInstruction;
		this.iCode = iCode;
	}
	
	public Data getData(){
		return iCode;
	}
}
