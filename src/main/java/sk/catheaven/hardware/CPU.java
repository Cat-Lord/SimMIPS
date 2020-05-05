/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.instructionEssentials.AssembledInstruction;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.instructionEssentials.Instruction;
import sk.catheaven.utils.Tie;

/**
 * Represents the CPU itself, main working unit of the simulation.
 * @author catlord
 */
public final class CPU {
	public final static int PHASE_COUNT = 5;
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	// mapping unique labels of components as a TUPLE and the signal connecting them
	private final Map<String, Tie> connections;
	
	private final List<Component>[] phases;					// list of components for each phase
	private final Assembler assembler;
	private final Map<String, Component> components;
	private InstructionMemory instructionMemory;			// entry point for program execution
	private final Map<String, Instruction> instructionSet;
	
	public CPU(JSONObject cpuJson, Map<String, Instruction> instructionSet) throws Exception, JSONException {
		if(cpuJson == null) throw new Exception("No CPU json file provided !");
		
		this.instructionSet = instructionSet;
		phases = new List[PHASE_COUNT];
		components = parseComponents(cpuJson.getJSONArray("components"));
		
		if(components.isEmpty())
			throw new Exception("No components were added !");
		if(instructionMemory == null)
			throw new Exception("No Instruction Memory");
		
		// log every component
		String debugString = "CPU Components:\n";
		for(Component c : getComponents()){
			
			debugString = debugString.concat("\t`" + c.getLabel() + "`\n");
		}
		logger.log(Level.INFO, debugString);
		
		// log components of every phase
		for(int phase_index = 0; phase_index < PHASE_COUNT; phase_index++){
			debugString = debugString.concat("Phase : " + phase_index + "\n");
			for(Component c : getComponentsOfPhase(phase_index))
				debugString = debugString.concat("\t`" + c.getLabel() + "`\n");
			debugString = debugString.concat("- - - - - - - - - - - -\n");
		}
		logger.log(Level.INFO, debugString);
		
		this.assembler = new Assembler(instructionSet);
		connections = parseConnections(cpuJson.getJSONArray("connections"));
	}
	
	private Map<String, Component> parseComponents(JSONArray cpuJson) throws Exception {
		Map<String, Component> componentsMap = new LinkedHashMap<>();
		Iterator<Object> componentIter = cpuJson.iterator();
		
		int phase_index = 0;
		boolean phase_advance = false;
		phases[phase_index] = new ArrayList<>();
		
		while(componentIter.hasNext()){
			JSONObject componentJO = (JSONObject) componentIter.next();
			String cLabel = componentJO.getString("label");
			String type = componentJO.getString("type");
			
			Component comp = null;
			
			switch(type.toLowerCase()){
				case "mux": comp = new MUX(cLabel, componentJO); break;
				case "pc": comp = new PC(cLabel, componentJO); break;
				case "constadder": comp = new ConstAdder(cLabel, componentJO); break;
				case "instructionmemory": { 
					instructionMemory = new InstructionMemory(cLabel, componentJO);
					comp = instructionMemory;
					break; 
				}
				case "latchregister": { 
					comp = new LatchRegister(cLabel, componentJO);
					phase_advance = true;
					break; 
				}
				
				case "controlunit": comp = new ControlUnit(cLabel, componentJO); break;
				case "constmux": comp = new ConstMUX(cLabel, componentJO); break;
				case "regbank": comp = new RegBank(cLabel, componentJO); break;
				case "signext": comp = new SignExtend(cLabel, componentJO); break;
				
				case "adder": comp = new Adder(cLabel, componentJO); break;
				case "alu": comp = new ALU(cLabel, componentJO); break;
				
				case "and": comp = new AND(cLabel, componentJO); break;
				case "datamemory": comp = new DataMemory(cLabel, componentJO); break;
				
				case "fork": comp = new Fork(cLabel, componentJO); break;
				
				default: break;
			}
			if(comp == null)
				throw new Exception("Unknown component type " + type);
			
			componentsMap.put(cLabel, comp);
			if(phase_advance){
				phase_advance = false;
				phase_index++;
				if(phase_index >= phases.length)
					throw new Exception("Too many latch registers for " + PHASE_COUNT + " phases !" );
				else
					phases[phase_index] = new ArrayList<>();
			}	
			phases[phase_index].add(comp);
		}
		
		return componentsMap;
	}
	
	/**
	 * Executing cycle means first passing output values of components to inputs of 
	 * target components. After this step the components are ready to handle input
	 * values and construct output values themselves, hence calling <code>execute</code>.
	 * 
	 * TODO: remove throwing an exception
	 */
	public void executeCycle() throws Exception {
		String message = "%15s ==> %15s | Set %16s  || Before and after || %s --> %s";
		String[] toPrint = new String[150];
		
		int index = 0;
		
		for(int phase_index = PHASE_COUNT - 1; phase_index >= 0; phase_index--){
			
			// set up inputs for every component in a phase
			for(Component c : phases[phase_index]){
				String from = c.getLabel();
				
				components.get(from).execute();		// execute first, to handle the input values and AFTER pass the output to other components

				if(connections.get(from) == null)
					throw new Exception("UNDEFINED ??!! " + from);

				Map<String, List<String>> ties = connections.get(from).getTies();

				// for every possible target component that is connected to 'from' component
				for(String targetComponent : ties.keySet()){
					List<String> selectorsList = ties.get(targetComponent);			// get all the selectors for one target component

					for(String selector : selectorsList){
						components.get(targetComponent).setInput(selector, components.get(from).getOutput(selector));

						Data d = components.get(targetComponent).getInput(selector);
						
						toPrint[index++] = (String.format(message, 
															from, 
															targetComponent, 
															selector, 
															components.get(from).getOutput(selector).getHex(),
															((d == null) ? (components.get(targetComponent).getLabel() + " ?") : d.getHex())
														));
					}
				}				
			}
		}
	}
	
	/**
	 * Parses connections between components and their input/output.
	 * @param jsonArray
	 * @return Map of component names and ties to other components	
	 */
	private Map<String, Tie> parseConnections(JSONArray jsonArray) throws Exception {
		Map<String, Tie> ties = new HashMap<>();
		
		Iterator<Object> jitter = jsonArray.iterator();
		while(jitter.hasNext()){
			JSONObject jo = (JSONObject) jitter.next();
			String from = jo.getString("from");
			String to = jo.getString("to");
			String selector = jo.getString("selector");
			
			testComponent(from, to, selector);
			
			if(ties.get(from) == null){
				Tie tie = new Tie();
				tie.addTie(to, selector);
				ties.put(from, tie);
			}
			else
				ties.get(from).addTie(to, selector);
		}
		
		return ties;
	}
	
	public void assembleCode(String code) {
		try {
			List<AssembledInstruction> program = assembler.assembleCode(code);
			instructionMemory.setProgram(program);
		} catch(SyntaxException e ) { System.out.println(e.getMessage()); }
	}
	
	public Collection<Component> getComponents(){
		return components.values();
	}
	
	public Assembler getAssembler(){
		return assembler;
	}
	
	/**
	 * Return list of components for a specific pipeline phase.
	 * @param index Index of specific pipeline phase. List of phases goes like this:
	 * 0: Instruction Fetch (IF)
	 * 1: Instruction Decode (ID)
	 * 2: Execute (EX)
	 * 3: Memory (MEM)
	 * 4: Writeback (WB)
	 * @return List of components of each phase
	 */
	public List<Component> getComponentsOfPhase(int index){
		if(index >= 0  &&  index < phases.length)
			return phases[index];
		return null;
	}
	
	/**
	 * Returns component specified by label.
	 * @param label Components unique label.
	 * @return 
	 */
	public Component getComponent(String label){
		return components.get(label);
	}
	
	/**
	 * Private error-checking method. Tries to locate components by 
	 * string parameters <i>from</i> and <i>to</i>. If successful,
	 * tries to set dummy input using selector and checks return 
	 * value.
	 * @param from Source element.
	 * @param to Target element.
	 * @param selector Label of this connector.
	 */
	private void testComponent(String from, String to, String selector) throws Exception {
		// test component existence
			if(components.get(from) == null)
				throw new Exception("Unknown source component `" + from + "` | target is `" + to + "` | selector `" + selector + "`");
			
			// test component existence
			if(components.get(to) == null)
				throw new Exception("Source component is `" + from + "` | Unknown target component `" + from + "` | selector `" + selector + "`");
			else {
				if(components.get(to).setInput(selector, new Data()) == false)
					throw new Exception("Source component is `" + from + "` | Target component `" + to + "`: -- unable to set input for selector `" + selector + "`");
			}
	}

}
