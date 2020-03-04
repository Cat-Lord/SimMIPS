/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.essentials;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.scene.SnapshotResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.utils.Field;

/**
 *
 * @author catlord
 */
public class InstructionType {
	private final String type;		// I, R, ...
	private List<Field> fields;
	
	public InstructionType(String type, JSONArray fields){
		this.type = type;
		this.fields = new ArrayList<>();
		parseFields(fields);
	}
	
	/**
	 * Reads input file and parses specified instruction types.
	 * @param json Input file as JSON object
	 */
	private void parseFields(JSONArray jsonFields){
		System.out.println("Parsing for " + this.type);
		System.out.println("Json array of length: " + jsonFields.length());
		for(int i = 0; i < jsonFields.length(); i++){
			JSONObject currField = jsonFields.getJSONObject(i);
			this.fields.add(new Field(currField.getString("label"), currField.getInt("bitSize")));
		}		//TODO: check everything from this point on
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the fields
	 */
	public List<Field> getFields() {
		return fields;
	}
	
	/**
	 * Iterates through list of fields and returns field specified by label
	 * @param fieldLabel A field to return.
	 * @return specific field from this instruction type.
	 */
	public Field getField(String fieldLabel){
		for(Field f : fields){
			if(f.getLabel().equals(fieldLabel));
				return f;
		}
		return null;
	}
}
