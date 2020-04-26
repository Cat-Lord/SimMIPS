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
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.instructionEssentials.Instruction;
import sk.catheaven.instructionEssentials.InstructionType;
import sk.catheaven.instructionEssentials.Field;
import sk.catheaven.instructionEssentials.argumentTypes.DataArgumentType;

/**
 * Represents the CPU itself, main working unit of the simulation.
 * @author catlord
 */
public class CPU {
	private static Logger logger;

	private final Assembler assembler;
	private final List<Component> components;
	private final Map<String, Instruction> instructionSet;
	
	public CPU(JSONObject cpuJson, Map<String, Instruction> instructionSet) throws Exception, JSONException {
		if(cpuJson == null) throw new Exception("No CPU json file provided !");
		
		CPU.logger = System.getLogger(this.getClass().getName());
		components = new ArrayList<>();
		this.instructionSet = instructionSet;
		//parseComponents(cpuJson.getJSONObject("components"));		//TODO	
		
		this.assembler = new Assembler(instructionSet);
	}
	
	private void parseComponents(JSONObject cpuJson) throws Exception {
		Iterator<String> componentIter = cpuJson.keys();
		
		while(componentIter.hasNext()){
			String label = componentIter.next();
			JSONObject componentJO = cpuJson.getJSONObject(label);
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
				
				case "fork": components.add(new Fork(label, componentJO)); break;
				
				default: System.err.println("Unknown Type: " + type); break;
			}
		}
		
		if(components.isEmpty()) throw new Exception("No components were added !");
	}
	
	public List<Component> getComponents(){
		return components;
	}
	
	public Assembler getAssembler(){
		return assembler;
	}
}
