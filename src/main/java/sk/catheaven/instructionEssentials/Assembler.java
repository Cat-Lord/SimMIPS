/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionEssentials;

import java.lang.System.Logger;
import java.util.List;
import java.util.Map;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.hardware.CPU;
import sk.catheaven.utils.argumentTypes.DataArgumentType;

/**
 *
 * @author catlord
 */
public class Assembler {
	private final String DATA_CHAR = ".";				// if present, the string containing this symbol is a data reference (.base or .offset)
	private final char COMMENT_CHAR = ';';				// comment in user code
	private final String POSITIONAL_CHAR = "#";			// denotes position (can be found in field values of instruction).
														// Is of String type just to allow usage of contains(POSITIONAL_CHAR)
	private static Logger logger;
	private final Map<String, Instruction> instructionSet;
	
	public Assembler(Map<String, Instruction> instructionSet){
		Assembler.logger = System.getLogger(this.getClass().getName());
		
		this.instructionSet = instructionSet;
	}
	
	/**
	 * Assembles the instruction to machine code.
	 * @param instruction Instruction to parse (as entered by user).
	 * @return Assembled instruction in the form AssembledInstruction class.
	 * @throws SyntaxException In case of user error.
	 */
	public AssembledInstruction assembleInstruction(String instruction) throws SyntaxException {
		logger.log(System.Logger.Level.DEBUG, "Assembling `" + instruction + "`");
		if(instruction.isBlank()) throw new SyntaxException("Empty instruction !");
		instruction = trimAndDecomment(instruction);
		String args[] = getInstructionArguments(instruction);
		
		// if there are arguments, there is a space after mnemo, otherwise there is only mnemo (which is acceptable)
		String mnemo = instruction;
		if(args.length > 0) mnemo = instruction.substring(0, instruction.indexOf(" "));
		mnemo = mnemo.toLowerCase().trim();
		
		checkInstruction(mnemo, args);
		Data iCode = createICode(mnemo, args);
		System.out.println("Icode created: " + iCode.getBinary());
		return new AssembledInstruction(this.instructionSet.get(mnemo), instruction, iCode);
	}
	
	/**
	 * Only checks instruction by the Instruction class, which dictates its format.
	 * @param instructionMnemo Instruction mnemo.
	 * @param args Array of arguments provided for the instruction.
	 * @throws SyntaxException
	 */
	private void checkInstruction(String instructionMnemo, String args[]) throws SyntaxException {
		Instruction instruction = instructionSet.get(instructionMnemo);
		
		if(instruction == null) throw new SyntaxException("Unknown instruction `" + instructionMnemo + "` !");
		
		if(args.length < instruction.getAllArguments().size()) 
			throw new SyntaxException("Not enough arguments for instruction " + instruction.getMnemo() + " (" + instruction.getAllArguments().size() + " required)");
		else if(args.length > instruction.getAllArguments().size()) 
			throw new SyntaxException("Too many arguments for instruction " + instruction.getMnemo() + " (" + instruction.getAllArguments().size() + " required)");
		
		// check each argument against arguments in instruction (if are correcty formatted)
		for(int i = 0; i < args.length; i++)
			instruction.getArgument(i).parse(args[i]);
	}
	
	/**
	 * Trims whitespace characters and removes comments, if there are any.
	 * @param instruction Raw instruction, as entered by user.
	 * @return The same instruction in string format, with leading/trailing spaces (and possibly comment) removed.
	 */
	private String trimAndDecomment(String instruction){
		int commentIndex = instruction.indexOf(COMMENT_CHAR);	// remove comment, if there is any
		String safeInstruction = instruction.trim();			// leading and trailing tabs, spaces, etc.
		if(commentIndex >= 0)
			safeInstruction = instruction.substring(0, commentIndex).trim();
		logger.log(System.Logger.Level.DEBUG, "Instruction seems valid: " + instruction);
		return safeInstruction;
	}

	/**
	 * From the instruction mnemo and provided arguments creates instruction code.
	 * @param instructionMnemo
	 * @param args
	 * @throws SyntaxException 
	 */
	private Data createICode(String instructionMnemo, String args[]) throws SyntaxException {
		Data iCode = new Data();
		
		Instruction instruction = instructionSet.get(instructionMnemo);
		InstructionType iType = instruction.getInstructionType();
		List<Field> fields = iType.getFields();
		
		int shiftBy = 0;
		for(int i = 0; i < fields.size(); i++){
			String fieldValue = instruction.getFieldValule(fields.get(i).getLabel());
			int tempCode = iCode.getData();
			shiftBy = fields.get(i).getBitSize();		// remember, by ho many bits we need to shift in next iteration			
			tempCode <<= shiftBy;
			
			// there is no positional character
			if( ! fieldValue.contains(POSITIONAL_CHAR))	
				tempCode |= Integer.parseInt(fieldValue);
			else{
				// there is a positional character
				
				// first check, if the field is a data field
				if(fieldValue.contains(DATA_CHAR)) {
					// get the argument type, because it knows how to parse the argument to get specific parts of that argument
					// For example: Ask the data argument to get ".base" and it knows, how to get it.
					DataArgumentType dat = (DataArgumentType)instruction.getArgument(i);
					
					// cut off the positional part, so from "#3.offset" get just "#3"
					String seqString = fieldValue.substring(fieldValue.indexOf(POSITIONAL_CHAR) + 1, fieldValue.indexOf(DATA_CHAR));
					int seq = Integer.parseInt(seqString);
					seq--;				// numbering in layout.json starts from 1
					
					dat.getPart(fieldValue, args[seq]);
				}
				else {
					// only positional value, i.e. "#3"
					String seqString = fieldValue.substring(fieldValue.indexOf(POSITIONAL_CHAR) + 1, fieldValue.length());
					int seq = Integer.parseInt(seqString);
					seq--;				// numbering in layout.json starts from 1
					
					// get the data from argument according to its type
					try{
						tempCode |= instruction.getArgument(seq).getData(args[seq]);
					} catch(IndexOutOfBoundsException e){
						logger.log(System.Logger.Level.WARNING, "Argument data car: Index out of bounds ! Index: " + seq + ", number of args: " + args.length); 
					}
				}
			}
			
			iCode.setData(tempCode);
		}
		
		return iCode;
	}
	
	/**
	 * Form user-entered instruction remove instruction mnemo
	 * and return array of arguments after the mnemo.
	 * @param instruction String representation of one instruction with arguments entered by user
	 * @return If there are no arguments after mnemo, returns empty array. Otherwise returns array of strings representing individual arguments.
	 */
	private String[] getInstructionArguments(String instruction){
		instruction = instruction.trim();					// in case there are spaces after the mnemo, dont get confused it as arguments
		int indexOfFirstSpace = instruction.indexOf(" ");
		if(indexOfFirstSpace < 0)
			return new String[0];							// return empty array, there are no arguments
		
		// remove mnemo and get arguments as one string
		String argList = instruction.substring(indexOfFirstSpace+1, instruction.length()).trim();
		
		String argArr[] = argList.split(",");
		for(int i = 0; i < argArr.length; i++)
			argArr[i] = argArr[i].trim();					// remove spacing around each argument (that means from " r1  " -> "r1"
		
		return argArr;
	}
}
