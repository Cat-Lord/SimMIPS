/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import sk.catheaven.hardware.Component;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.instructionEssentials.Instruction;
import sk.catheaven.instructionEssentials.InstructionType;
import sk.catheaven.utils.Field;
import sk.catheaven.utils.argumentTypes.ArgumentType;
import sk.catheaven.hardware.*;

/**
 *
 * @author catlord
 */
public class Main {
    public static void main(String[] args) throws IOException, URISyntaxException {
		List<Instruction> instructions = parseLayout(new JSONObject(readFile("sk/catheaven/data/layout.json")));
		parseCPU(new JSONObject(readFile("sk/catheaven/data/cpu.json")));
    }
	
	// CPU
	public static void parseCPU(JSONObject inFile) throws JSONException {
		List<Component> components = new ArrayList<>();
		
		Iterator<String> componentIter = inFile.keys();
		while(componentIter.hasNext()){
			String label = componentIter.next();
			JSONObject componentJO = inFile.getJSONObject(label);
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
	}
	
	// INSTRUCTION SET
	public static List<Instruction> parseLayout(JSONObject inFile) throws IOException, URISyntaxException {
		JSONObject types = inFile.getJSONObject("types");
		List<InstructionType> iTypes = new ArrayList<>();
		
		Iterator<String> currType = types.keys();
		while(currType.hasNext()){
			String typeLabel = currType.next();
			iTypes.add(new InstructionType(typeLabel, types.getJSONArray(typeLabel)));
		}
		
		// testing
		iTypes.forEach(iType -> {
			System.out.println("" + iType.getTypeLabel());
			System.out.println("Fields: " );
			iType.getFields().forEach(field -> {
				System.out.println("" + field.getLabel() + " of size " + field.getBitSize() + "b");
			});
			System.out.println();
		});
		
		
		//	have: iTypes ============================================================================================================================
		
		// instructions
		List<Instruction> instructions = new ArrayList<>();
		JSONObject is = inFile.getJSONObject("instructions");

		Iterator<String> mnemoIter = is.keys();		// current instruction mnemo
		while(mnemoIter.hasNext()){
			String mnemo = mnemoIter.next();
			JSONObject currInstructionJson = is.getJSONObject(mnemo);
			String iType = currInstructionJson.getString("type");
			
			// find specified type in instruction types list
			for(int j = 0; j < iTypes.size(); j++){
				InstructionType currIType = iTypes.get(j);
				if(currIType.getTypeLabel().equals(iType)){
					instructions.add(new Instruction(mnemo, currInstructionJson, currIType));
					break;
				}
			}
		}
		
		// arguments
		System.out.println("Parsed instructions (" + instructions.size() + ")");
		instructions.forEach((Instruction i) -> {
			System.out.print(i.getMnemo() + " -- ");
			
			// print arguments (REG, INT, ... )
			System.out.print("Args: [");
			List<ArgumentType> argTypes = i.getArguments();
			for(int ati = 0; ati < argTypes.size()-1; ati++)
				System.out.print(argTypes.get(ati).toString() + ", ");
			System.out.print(argTypes.get(argTypes.size()-1).toString() + "]\n");

			// print each field mapping
			List<Field> fields = i.getInstructionType().getFields();
			for(Field f : fields){
				if(i.getFieldValule(f.getLabel()) == null)
					System.out.println(f.getLabel() + ": ???");
				else
					System.out.println(f.getLabel() + ": " + i.getFieldValule(f.getLabel()));
			}
			
			System.out.println("Description: " + i.getDescription());
			System.out.println();
		});
		
		return instructions;
	}
	
	private static String readFile(String fileName) throws IOException, URISyntaxException{
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		URI uri = classloader.getResource(fileName).toURI();
		List<String> lines = Files.readAllLines(Paths.get(uri));
		String json = "";
		
		for(String line : lines)
			json += line;
		return json;
	}
}
