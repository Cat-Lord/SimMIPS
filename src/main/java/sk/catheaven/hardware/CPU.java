/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.instructionEssentials.Instruction;

/**
 * Represents the CPU itself, main working unit of the simulation.
 * @author catlord
 */
public final class CPU {
	private static Logger logger;

	private final Assembler assembler;
	private final Map<String, Component> components;
	private InstructionMemory instructionMemory;			// entry point for program execution
	private final Map<String, Instruction> instructionSet;
	
	public CPU(JSONObject cpuJson, Map<String, Instruction> instructionSet) throws Exception, JSONException {
		if(cpuJson == null) throw new Exception("No CPU json file provided !");
		
		CPU.logger = System.getLogger(this.getClass().getName());
		
		this.instructionSet = instructionSet;
		components = parseComponents(cpuJson.getJSONObject("components"));
		
		if(components.isEmpty())
			throw new Exception("No components were added !");
		if(instructionMemory == null)
			throw new Exception("No Instruction Memory");
		
		String debugString = "CPU Components:\n";
		for(Component c : getComponents())
			debugString = debugString.concat("\t" + c.getLabel() + "\n");
		logger.log(Logger.Level.DEBUG, debugString);
		
		this.assembler = new Assembler(instructionSet);
	}
	
	private Map<String, Component> parseComponents(JSONObject cpuJson) throws Exception {
		Map<String, Component> cpuComponents = new HashMap<>();
		Iterator<String> componentIter = cpuJson.keys();
		
		while(componentIter.hasNext()){
			String cLabel = componentIter.next();
			JSONObject componentJO = cpuJson.getJSONObject(cLabel);
			String type = componentJO.getString("type");
			
			switch(type.toLowerCase()){
				case "mux": cpuComponents.put(cLabel, new MUX(cLabel, componentJO)); break;
				case "pc": cpuComponents.put(cLabel, new PC(cLabel, componentJO)); break;
				case "constadder": cpuComponents.put(cLabel, new ConstAdder(cLabel, componentJO)); break;
				case "instructionmemory": { 
					instructionMemory = new InstructionMemory(cLabel, componentJO);
					cpuComponents.put(cLabel, instructionMemory); 
					
					break; 
				}
				case "latchregister": cpuComponents.put(cLabel, new LatchRegister(cLabel, componentJO)); break;
				
				case "controlunit": cpuComponents.put(cLabel, new ControlUnit(cLabel, componentJO)); break;
				case "constmux": cpuComponents.put(cLabel, new ConstMUX(cLabel, componentJO)); break;
				case "regbank": cpuComponents.put(cLabel, new RegBank(cLabel, componentJO)); break;
				case "signext": cpuComponents.put(cLabel, new SignExtend(cLabel, componentJO)); break;
				
				case "adder": cpuComponents.put(cLabel, new Adder(cLabel, componentJO)); break;
				case "alu": cpuComponents.put(cLabel, new ALU(cLabel, componentJO)); break;
				
				case "and": cpuComponents.put(cLabel, new AND(cLabel, componentJO)); break;
				case "datamemory": cpuComponents.put(cLabel, new DataMemory(cLabel, componentJO)); break;
				
				case "fork": cpuComponents.put(cLabel, new Fork(cLabel, componentJO)); break;
				
				default: System.err.println("Unknown Type: " + type); break;
			}
		}
		
		return cpuComponents;
	}
	
	public Collection<Component> getComponents(){
		return components.values();
	}
	
	public Assembler getAssembler(){
		return assembler;
	}
}
