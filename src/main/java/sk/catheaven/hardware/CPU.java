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
import java.util.TreeMap;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.instructionEssentials.Instruction;
import sk.catheaven.utils.Connector;

/**
 * Represents the CPU itself, main working unit of the simulation.
 * @author catlord
 */
public final class CPU {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	// mapping unique labels of components as a TUPLE and the signal connecting them
	private final List<Connector> connections;
	
	private final Assembler assembler;
	private final Map<String, Component> components;
	private InstructionMemory instructionMemory;			// entry point for program execution
	private final Map<String, Instruction> instructionSet;
	
	public CPU(JSONObject cpuJson, Map<String, Instruction> instructionSet) throws Exception, JSONException {
		if(cpuJson == null) throw new Exception("No CPU json file provided !");
		
		this.instructionSet = instructionSet;
		components = parseComponents(cpuJson.getJSONObject("components"));
		
		if(components.isEmpty())
			throw new Exception("No components were added !");
		if(instructionMemory == null)
			throw new Exception("No Instruction Memory");
		
		String debugString = "CPU Components:\n";
		for(Component c : getComponents()){
			
			debugString = debugString.concat("\t`" + c.getLabel() + "`\n");
		}
		logger.log(Level.INFO, debugString);
		
		this.assembler = new Assembler(instructionSet);
		connections = parseConnections(cpuJson.getJSONArray("connections"));
	}
	
	private Map<String, Component> parseComponents(JSONObject cpuJson) throws Exception {
		Map<String, Component> componentsMap = new LinkedHashMap<>();
		ArrayList<Component> cpuComponents = new ArrayList<>();		// store the components here and traverse backwards when adding to map of components
		Iterator<String> componentIter = cpuJson.keys();
		
		while(componentIter.hasNext()){
			String cLabel = componentIter.next();
			JSONObject componentJO = cpuJson.getJSONObject(cLabel);
			String type = componentJO.getString("type");
			
			switch(type.toLowerCase()){
				case "mux": cpuComponents.add(new MUX(cLabel, componentJO)); break;
				case "pc": cpuComponents.add(new PC(cLabel, componentJO)); break;
				case "constadder": cpuComponents.add(new ConstAdder(cLabel, componentJO)); break;
				case "instructionmemory": { 
					instructionMemory = new InstructionMemory(cLabel, componentJO);
					cpuComponents.add(instructionMemory); 
					
					break; 
				}
				case "latchregister": cpuComponents.add(new LatchRegister(cLabel, componentJO)); break;
				
				case "controlunit": cpuComponents.add(new ControlUnit(cLabel, componentJO)); break;
				case "constmux": cpuComponents.add(new ConstMUX(cLabel, componentJO)); break;
				case "regbank": cpuComponents.add(new RegBank(cLabel, componentJO)); break;
				case "signext": cpuComponents.add(new SignExtend(cLabel, componentJO)); break;
				
				case "adder": cpuComponents.add(new Adder(cLabel, componentJO)); break;
				case "alu": cpuComponents.add(new ALU(cLabel, componentJO)); break;
				
				case "and": cpuComponents.add(new AND(cLabel, componentJO)); break;
				case "datamemory": cpuComponents.add(new DataMemory(cLabel, componentJO)); break;
				
				case "fork": cpuComponents.add(new Fork(cLabel, componentJO)); break;
				
				default: System.err.println("Unknown Type: " + type); break;
			}
		}
		
		// reverse the order of components
		for(int i = cpuComponents.size() - 1; i >= 0; i--)
			componentsMap.put(cpuComponents.get(i).getLabel(), cpuComponents.get(i));
		
		return componentsMap;
	}
	
	/**
	 * Executing cycle means first passing output values of components to inputs of 
	 * target components. After this step the components are ready to handle input
	 * values and construct output values themselves, hence calling <code>execute</code>.
	 */
	public void executeCycle(){
		String message = "%15s ==> %15s | Set %16s  || Before and after || %s --> ";
		String[] toPrint = new String[connections.size()];
		int index = 0;
		
		// print out connections and pass the values
		for(Connector c : connections){
			String from = c.getFrom();
			String to = c.getTo();
			String selector = c.getSelector();
			
			toPrint[index++] = (String.format(message, 
					from, 
					to, 
					selector, 
					components.get(from).getOutput(selector).getHex())
				);
			
			components.get(to).setInput(selector, components.get(from).getOutput(selector));
		}
		
		// execute each component
		components.values().forEach((c) -> {
			c.execute();
		});
		
		index = 0;
		// print out connections and pass the values
		for(Connector c : connections){
			String from = c.getFrom();
			String to = c.getTo();
			String selector = c.getSelector();
			
			System.out.println(toPrint[index++] + components.get(from).getOutput(selector).getHex());
		}
	}
	
	/**
	 * Parses connections between components and their input/output.
	 * @param jsonArray
	 * @return List of connections represented as Connector class.
	 */
	private List<Connector> parseConnections(JSONArray jsonArray) throws Exception {
		List<Connector> wires = new ArrayList<>();
		
		Iterator<Object> jitter = jsonArray.iterator();
		while(jitter.hasNext()){
			JSONObject jo = (JSONObject) jitter.next();
			String from = jo.getString("from");
			String to = jo.getString("to");
			String selector = jo.getString("selector");
			
			testComponent(from, to, selector);
			
			wires.add(new Connector(from, to, selector));
		}
		
		return wires;
	}
	
	public Collection<Component> getComponents(){
		return components.values();
	}
	
	public Assembler getAssembler(){
		return assembler;
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
