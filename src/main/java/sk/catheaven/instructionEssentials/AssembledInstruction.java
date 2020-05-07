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
	private final Data address;
	private final int lineCodeIndex;
	
	public AssembledInstruction(int lineCodeIndex, Instruction instruction, String originalInstruction, Data iCode, Data address){
		this.lineCodeIndex = lineCodeIndex;
		this.instruction = instruction;
		this.originalInstruction = originalInstruction;
		this.iCode = iCode;
		this.address = address;
	}
	
	
	/**
	 * @return the instruction
	 */
	public Instruction getInstruction() {
		return instruction;
	}

	/**
	 * @return OriginalInstruction in string format.
	 */
	public String getOriginalInstruction() {
		return originalInstruction;
	}

	/**
	 * @return the address
	 */
	public Data getAddress() {
		return address;
	}
	
	public Data getIcode(){
		return iCode;
	}
	
	public int getLineCodeIndex(){
		return lineCodeIndex;
	}
}
