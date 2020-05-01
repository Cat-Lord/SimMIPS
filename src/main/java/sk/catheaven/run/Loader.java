/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.logging.Logger;
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
import sk.catheaven.instructionEssentials.Instruction;
import sk.catheaven.instructionEssentials.InstructionType;
import sk.catheaven.instructionEssentials.Field;
import sk.catheaven.instructionEssentials.argumentTypes.ArgumentType;

/**
 *
 * @author catlord
 */
public class Loader {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private CPU cpu;
	
	/**
	 * Used for debugging. Allows separate method calls.
	 */
	public Loader(){
		
	}
	
	public Loader(String layoutPath, String cpuPath) throws IOException, URISyntaxException {
		
		
		try {
			Map<String, Instruction> instructionsSet = parseLayout(new JSONObject(readFile(layoutPath)));
			this.cpu = new CPU(new JSONObject(readFile(cpuPath)), instructionsSet);
		} catch(Exception e) {
			e.printStackTrace(); 
			System.out.println(e.getLocalizedMessage());
			System.out.println(e.getClass().getCanonicalName());
			System.err.println("Failed to create CPU -- " + e.getMessage()); 
		}
	}
	
	public CPU getCPU(){
		return this.cpu;
	}
	
	// INSTRUCTION SET
	private Map<String, Instruction> parseLayout(JSONObject inFile) throws Exception, IOException, URISyntaxException {
		String debugString;
		JSONObject types = inFile.getJSONObject("types");
		List<InstructionType> iTypes = new ArrayList<>();
		
		Iterator<String> currType = types.keys();
		while(currType.hasNext()){
			String typeLabel = currType.next();
			iTypes.add(new InstructionType(typeLabel, types.getJSONArray(typeLabel)));
		}
		
		// testing
		debugString = "Instruction Types:\n";
		for(InstructionType currIType : iTypes){
			debugString = debugString.concat(currIType.getTypeLabel() + "\n");
			debugString = debugString.concat("Fields:\n");
			for(Field f : currIType.getFields()){
				debugString = debugString.concat(f.getLabel() + " of size " + f.getBitSize() + "b\n");
			}
			debugString = debugString.concat("\n");
		}
		logger.log(Level.INFO, debugString);
		
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
						throw new Exception("Duplicate instruction `" + mnemo + "` !");
					
					break;
				}
			}
		}
		
		// arguments
		debugString = "Parsed instructions (" + instructions.size() + ")\n";
		for(String mnemo : instructions.keySet()) {
			Instruction i = instructions.get(mnemo);
			debugString = debugString.concat(i.getMnemo() + " (type " + i.getInstructionType().getTypeLabel() + ") -- ");
			
			// print arguments (REG, INT, ... )
			debugString = debugString.concat("Args: [");
			List<ArgumentType> argTypes = i.getAllArguments();
			if(argTypes.size() > 0){
				for(int ati = 0; ati < argTypes.size()-1; ati++)
					debugString = debugString.concat(argTypes.get(ati).toString() + ", ");
				debugString = debugString.concat(argTypes.get(argTypes.size()-1).toString() + "]\n");
			}
			
			// print each field mapping
			List<Field> fields = i.getInstructionType().getFields();
			for(Field f : fields){
				if(i.getFieldValue(f.getLabel()) == null)
					logger.log(Level.WARNING, "Unknown field/field value relationship error !");
				else
					debugString = debugString.concat(f.getLabel() + ": " + i.getFieldValue(f.getLabel()) + "\n");
			}
			logger.log(Level.INFO, debugString);
			logger.log(Level.INFO, "Description: " + i.getDescription());
		}
		
		return instructions;
	}
	
	public String readFile(String fileName) throws IOException, URISyntaxException{
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		URI uri = classloader.getResource(fileName).toURI();
		List<String> lines = Files.readAllLines(Paths.get(uri));
		String json = "";
		
		for(String line : lines)
			json += line;
		return json;
	}
}