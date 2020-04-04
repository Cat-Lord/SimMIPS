/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.instructionEssentials.AssembledInstruction;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.instructionEssentials.Instruction;
import sk.catheaven.instructionEssentials.InstructionType;
import sk.catheaven.utils.Field;
import sk.catheaven.utils.argumentTypes.DataArgumentType;

/**
 *
 * @author catlord
 */
public class CPU {
	private static Logger logger;
	private final char COMMENT_CHAR = ';';				// comment in user code
	private final String POSITIONAL_CHAR = "#";			// denotes position (can be found in field values of instruction).
														// Is of String type just to allow usage of "containst(POSITIONAL_CHAR)
	private final String DATA_CHAR = ".";				// if present, the string containing this symbol is a data reference (.base or .offset)
	private final List<Component> components;
	private final Map<String, Instruction> instructionSet;
	
	public CPU(JSONObject cpuJson, Map<String, Instruction> instructionSet) throws Exception, JSONException {
		if(cpuJson == null) throw new Exception("No CPU json file provided !");
		
		CPU.logger = System.getLogger(this.getClass().getName());
		components = new ArrayList<>();
		this.instructionSet = instructionSet;
		parseComponents(cpuJson.getJSONObject("components"));
	}
	/**
	 * Assembles the instruction to machine code.
	 * @param instruction Instruction to parse (as entered by user).
	 * @throws SyntaxException In case of user error.
	 */
	public AssembledInstruction assembleInstruction(String instruction) throws SyntaxException {
		instruction = checkInstructionFormat(instruction);
		String args[] = getInstructionArguments(instruction);
		String mnemo = instruction;
		
		// if there are arguments, there is a space after mnemo, otherwise there is only mnemo (which is acceptable)
		if(args.length > 0) mnemo = instruction.substring(0, instruction.indexOf(" "));
		
		checkInstruction(mnemo, args);
		Data iCode = createICode(mnemo, args);
		
		return null;	// new AssembledInstruction(); // TODO
	}
	
	/**
	 * Only checks instruction by the Instruction class, which dictates its format.
	 * @param instructionMnemo Instruction mnemo.
	 * @param args Array of arguments provided for the instruction.
	 * @throws SyntaxException
	 */
	private void checkInstruction(String instructionMnemo, String args[]) throws SyntaxException {
		Instruction instruction = instructionSet.get(instructionMnemo);
		
		if(instruction == null) throw new SyntaxException("Unknown instruction " + instructionMnemo + " !");
		
		if(args.length < instruction.getAllArguments().size()) 
			throw new SyntaxException("Not enough arguments for instruction " + instruction.getMnemo() + " (" + instruction.getAllArguments().size() + " required)");
		else if(args.length > instruction.getAllArguments().size()) 
			throw new SyntaxException("Too many arguments for instruction " + instruction.getMnemo() + " (" + instruction.getAllArguments().size() + " required)");
		
		// check each argument against arguments in instruction (if are correcty formatted)
		for(int i = 0; i < args.length; i++){
			try{
				instruction.getArgument(i).parse(args[i]);
			} catch(Exception e){
				logger.log(Logger.Level.WARNING, "checkInstruction(): " + instructionMnemo + " argument " + i + " exception " + e.getMessage());
			}
		}
	}
	
	private String checkInstructionFormat(String instruction){
		String safeInstruction = instruction.trim();			// leading and trailing tabs, spaces, etc.
		int commentIndex = instruction.indexOf(COMMENT_CHAR);	// remove comment, if there is any
		if(commentIndex >= 0)
			safeInstruction = instruction.substring(0, commentIndex);
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
		
		for(int i = 0; i < fields.size(); i++){
			String fieldValue = instruction.getFieldValule(fields.get(i).getLabel());
			int tempCode = iCode.getData();
			tempCode <<= fields.get(i).getBitSize();
			
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
					int seq = 0;
					try{
						seq = Integer.parseInt(seqString);
					} catch(NumberFormatException e) { 
						logger.log(Logger.Level.WARNING, "createICode(): Number format exception, check argumentType parsing ! Argument: " + args[seq]); 
					}
					
					dat.getPart(fieldValue, args[seq]);
				}
				else {
					// only positional value, i.e. "#3"
					String seqString = fieldValue.substring(fieldValue.indexOf(POSITIONAL_CHAR) + 1, fieldValue.length());
					int seq = 0;
					try{
						seq = Integer.parseInt(seqString);
					} catch(NumberFormatException e) { 
						logger.log(Logger.Level.WARNING, "createICode(): Number format exception, check argumentType parsing ! Argument: " + seqString); 
					}
					
					// sanity check
					try{
						tempCode |= Integer.parseInt(args[seq]);
					} catch(IndexOutOfBoundsException e){
						logger.log(Logger.Level.WARNING, "createICode(): Index out of bounds ! Index: " + seq + ", number of args: " + args.length); 
					} catch(NumberFormatException e) { 
						logger.log(Logger.Level.WARNING, "createICode(): Number format exception, check argumentType parsing ! Argument: " + args[seq]); 
					}
				}
			}
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
		int indexOfFirstSpace = instruction.indexOf(" ");
		if(indexOfFirstSpace < 0)
			return new String[0];			// return empty array, there are no arguments
		
		return (instruction.substring(indexOfFirstSpace+1, instruction.length()).split(" "));
	}
	
	private void parseComponents(JSONObject json) throws Exception {
		Iterator<String> componentIter = json.keys();
		
		while(componentIter.hasNext()){
			String label = componentIter.next();
			JSONObject componentJO = json.getJSONObject(label);
			String type = componentJO.getString("type");
			
			switch(type.toLowerCase()){
				case "mux": components.add(new MUX(label, componentJO)); break;
				case "pc": components.add(new PC(label, componentJO)); break;
				case "constadder": components.add(new ConstAdder(label, componentJO)); break;
				case "instructionmemory": components.add(new InstructionMemory(label, componentJO)); break;
				case "latchregister": components.add(new LatchRegister(label, componentJO)); break;
				
				case "controlunit": components.add(new ControlUnit(label, componentJO)); break;
				case "constmux": components.add(new ConstMUX(label, componentJO)); break;
				case "regbank": components.add(new RegBank(label, componentJO)); break;
				case "signext": components.add(new SignExtend(label, componentJO)); break;
				
				case "adder": components.add(new Adder(label, componentJO)); break;
				case "alu": components.add(new ALU(label, componentJO)); break;
				
				case "and": components.add(new AND(label, componentJO)); break;
				case "datamemory": components.add(new DataMemory(label, componentJO)); break;
				
				default: System.err.println("Unknown Type: " + type); break;
			}
		}
		
		if(components.isEmpty()) throw new Exception("No components were added !");
	}
	
	public List<Component> getComponents(){
		return components;
	}
	
}
