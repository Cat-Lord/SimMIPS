/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import sk.catheaven.hardware.CPU;
import sk.catheaven.hardware.Component;
import sk.catheaven.instructionEssentials.Instruction;
import sk.catheaven.instructionEssentials.InstructionType;
import sk.catheaven.utils.Field;
import sk.catheaven.utils.argumentTypes.ArgumentType;

/**
 *
 * @author catlord
 */
public class Loader {
	private CPU cpu;
	
	public Loader(String layoutPath, String cpuPath) throws IOException, URISyntaxException {
		Map<String, Instruction> instructionsSet = parseLayout(new JSONObject(readFile(layoutPath)));
		
		try {
			parseCPU(new JSONObject(readFile(cpuPath)), instructionsSet);
		} catch(Exception e) { System.err.println("Failed to create CPU"); }
	}
	
	public CPU getCPU(){
		return this.cpu;
	}
	
	// CPU
	private void parseCPU(JSONObject inFile, Map<String, Instruction> instructionsSet) throws Exception {
		this.cpu = new CPU(inFile, instructionsSet);
		
		List<Component> cps = cpu.getComponents();
		
		System.out.println("CPU has total of " + cps.size() + " components");
		for(Component c : cps){
			System.out.println("" + c.getLabel());
		}
	}
	
	// INSTRUCTION SET
	private Map<String, Instruction> parseLayout(JSONObject inFile) throws IOException, URISyntaxException {
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
		Map<String, Instruction> instructions = new HashMap<>();
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
					if(instructions.get(mnemo) == null)
						instructions.put(mnemo, new Instruction(mnemo, currInstructionJson, currIType));
					else
						System.out.println("Duplicate instruction " + mnemo + " !");
					break;
				}
			}
		}
		
		// arguments
		System.out.println("Parsed instructions (" + instructions.size() + ")");
		for(String mnemo : instructions.keySet()) {
			Instruction i = instructions.get(mnemo);
			System.out.print(i.getMnemo() + " (type " + i.getInstructionType().getTypeLabel() + ") -- ");
			
			// print arguments (REG, INT, ... )
			System.out.print("Args: [");
			List<ArgumentType> argTypes = i.getAllArguments();
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
		}
		
		return instructions;
	}
	
	private String readFile(String fileName) throws IOException, URISyntaxException{
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		URI uri = classloader.getResource(fileName).toURI();
		List<String> lines = Files.readAllLines(Paths.get(uri));
		String json = "";
		
		for(String line : lines)
			json += line;
		return json;
	}
}