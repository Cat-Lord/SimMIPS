/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionEssentials;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author catlord
 */
public class InstructionType {
	private final String typeLabel;			// I, R, ...
	private final List<Field> fields;
	
	public InstructionType(String type, JSONArray fields){
		this.typeLabel = type;
		this.fields = new ArrayList<>();
		parseFields(fields);
	}
	
	/**
	 * Reads input file and parses specified instruction types.
	 * @param json Input file as JSON object
	 */
	private void parseFields(JSONArray jsonFields){
		for(int i = 0; i < jsonFields.length(); i++){
			JSONObject currField = jsonFields.getJSONObject(i);
			this.fields.add(new Field(currField.getString("label"), currField.getInt("bitSize")));
		}
	}

	/**
	 * @return the typeLabel
	 */
	public String getTypeLabel() {
		return typeLabel;
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
	 * @return specific field from this instruction typeLabel.
	 */
	public Field getField(String fieldLabel){
		for(Field f : fields){
			if(f.getLabel().equals(fieldLabel));
				return f;
		}
		return null;
	}
}
