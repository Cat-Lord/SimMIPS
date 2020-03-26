/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionEssentials;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import sk.catheaven.utils.Field;
import sk.catheaven.utils.argumentTypes.ArgumentType;
import sk.catheaven.utils.argumentTypes.DataArgumentType;
import sk.catheaven.utils.argumentTypes.IntArgumentType;
import sk.catheaven.utils.argumentTypes.LabelArgumentType;
import sk.catheaven.utils.argumentTypes.RegArgumentType;

/**
 *
 * @author catlord
 */
public class Instruction {
	private static Logger logger;
    private Data data;                         // representation in machine code (needed ?)
    private final String mnemo;
	private final InstructionType type;
    private final List<ArgumentType> arguments;
	private String description;
    
    public Instruction(String mnemo, JSONObject json, InstructionType type){
		Instruction.logger = System.getLogger(this.getClass().getName());
		arguments = new ArrayList<>();
		
		this.mnemo = mnemo;
		this.type = type;
		this.description = "";
		parseInstruction(json, type);
	}
    
	private void parseInstruction(JSONObject json, InstructionType type){
		parseArgs(json);
		//parseFields(json, type);		// TODO parse fields to reconstruct data from text ( `add r1,r1,r2` -> `0000001001010...` )
		this.description = json.getString("desc");
	}
	
	private void parseArgs(JSONObject json){
		JSONArray jargs = json.getJSONArray("args");	// json array arguments
		
		Iterator<Object> jiter = jargs.iterator();
		while(jiter.hasNext()){
			String arg = (String) jiter.next();
			
			switch(arg.toLowerCase()){
				case "reg": arguments.add(new RegArgumentType());	  break;
				case "int": arguments.add(new IntArgumentType());	  break;
				case "label": arguments.add(new LabelArgumentType()); break;
				case "data": arguments.add(new DataArgumentType());	  break;
				default: logger.log(System.Logger.Level.WARNING, "Unknown argument " + arg); break;	
			}
		}
	}

	private void parseFields(JSONObject json, InstructionType type){
		List<Field> typeArgs = type.getFields();
		
		for(Field f : typeArgs){
			// TODO resolve arguments representation
		}
		
	}
	
	public String getMnemo(){
		return mnemo;
	}
	
	public InstructionType getInstructionType(){
		return type;
	}
	
	public List<ArgumentType> getArguments(){
		return arguments;
	}
	
	public String getDescription(){
		return description;
	}
	
}
