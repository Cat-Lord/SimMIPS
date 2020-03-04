/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.essentials;

import java.util.List;
import org.json.JSONObject;
import sk.catheaven.utils.Field;

/**
 *
 * @author catlord
 */
public class InstructionType {
	private String type;		// I, R, ...
	private List<Field> fields;
	
	public InstructionType(JSONObject json){
		parseJson(json);
	}
	
	private void parseJson(JSONObject json){
		JSONObject types = json.getJSONObject("types");
		
	}
	
}
