/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionEssentials;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.argumentTypes.ArgumentType;
import sk.catheaven.instructionEssentials.argumentTypes.DataArgumentType;
import sk.catheaven.instructionEssentials.argumentTypes.IntArgumentType;
import sk.catheaven.instructionEssentials.argumentTypes.LabelArgumentType;
import sk.catheaven.instructionEssentials.argumentTypes.RegArgumentType;

/**
 *
 * @author catlord
 */
public class Instruction {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private final InstructionType type;				// needs to be stored because we have to know how many bits should each field have
    private final String mnemo;
    private final List<ArgumentType> arguments;
	private final Map<String, String> fieldValues;	// mapping each instruction field to a value (constant, argument or offset(base))
	private String description;
    
    public Instruction(String mnemo, JSONObject json, InstructionType iType){
		
		arguments = new ArrayList<>();
		fieldValues = new HashMap<>();
		
		this.mnemo = mnemo;
		this.type = iType;
		this.description = "";
		parseInstruction(json);
	}
    
	
	
	/**
	 * 
	 * @param json
	 * @param type 
	 * @throws JSONException In case we don't find fields, we cant create this instruction.
	 */
	private void parseInstruction(JSONObject json) throws JSONException {
		parseArgs(json.getJSONArray("args"));
		parseFields(json.getJSONObject("fields"));
		this.description = json.getString("desc");
	}
	
	private void parseArgs(JSONArray jargs){
		Iterator<Object> jiter = jargs.iterator();
		while(jiter.hasNext()){
			String arg = (String) jiter.next();
			
			switch(arg.toLowerCase()){
				case "reg": arguments.add(new RegArgumentType());	  break;
				case "int": arguments.add(new IntArgumentType());	  break;
				case "label": arguments.add(new LabelArgumentType()); break;
				case "data": arguments.add(new DataArgumentType());	  break;
				default: logger.log(Level.WARNING, "Unknown argument " + arg); break;	
			}
		}
	}

	/**
	 * Finds all fields defined in instruction type in fields and stores it's value in a map.
	 * Mapping is done like this: Field --> FieldValue.
	 * @param json Field json object. It should be possible to search for specific fields in this json object.
	 */
	private void parseFields(JSONObject json){
		List<Field> typeArgs = type.getFields();
		
		for(Field f : typeArgs){
			String fieldVal = json.getString(f.getLabel());		// example: get me value of "rs" -> returns "#2"
			fieldValues.put(f.getLabel(), fieldVal);
		}

	}
	
	public String getMnemo(){
		return mnemo;
	}
	
	public InstructionType getInstructionType(){
		return type;
	}
	
	public ArgumentType getArgument(int i) {
		return arguments.get(i);
	}
	
	public List<ArgumentType> getAllArguments(){
		return arguments;
	}
	
	public String getFieldValue(String fieldName){
		return fieldValues.get(fieldName);
	}
	
	public String getDescription(){
		return description;
	}
	
}
