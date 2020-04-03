/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.instructionEssentials.Instruction;

/**
 *
 * @author catlord
 */
public class CPU {
	private final char COMMENT_CHAR = ';';
	private final List<Component> components;
	private final Map<String, Instruction> instructionSet;
	
	public CPU(JSONObject cpuJson, Map<String, Instruction> instructionSet) throws Exception, JSONException {
		if(cpuJson == null) throw new Exception("No CPU json file provided !");
		
		components = new ArrayList<>();
		this.instructionSet = instructionSet;
		parseComponents(cpuJson.getJSONObject("components"));
	}
	/**
	 * Assembles the instruction to machine code.
	 * @param instruction - instruction to parse (as entered by user).
	 * @throws SyntaxException - in case of user error.
	 */
	public void assembleInstruction(String instruction) throws SyntaxException {
		instruction = checkInstructionFormat(instruction);
		String args[] = getInstructionArguments(instruction);
		String mnemo = instruction;
		
		// if there are arguments, there is a space after mnemo, otherwise there is only mnemo (which is acceptable)
		if(args.length > 0) mnemo = instruction.substring(0, instruction.indexOf(" "));
		
		checkInstruction(mnemo, args);
	}
	
	private void checkInstruction(String instructionMnemo, String args[]){
		// TODO
	}
	
	private String checkInstructionFormat(String instruction){
		String safeInstruction = instruction.trim();			// leading and trailing tabs, spaces, etc.
		int commentIndex = instruction.indexOf(COMMENT_CHAR);	// remove comment, if there is any
		if(commentIndex >= 0)
			safeInstruction = instruction.substring(0, commentIndex);
		return safeInstruction;
	}

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
