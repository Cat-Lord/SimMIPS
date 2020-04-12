/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author catlord
 */
public class ALU extends Component {
	private final Map<Integer, String> operations;
	
	public ALU(String label, JSONObject componentJO) throws JSONException {
		super(label);
		
		this.operations = assignOperations(componentJO.getJSONArray("operations"));
	}
	
	/**
	 * Creates map, which binds code value to specific operation, for example 1 could be <i>add</i>.
	 * @param operations Array of code values and respective operations.
	 * @return 
	 */
	private Map<Integer, String> assignOperations(JSONArray operations){
		Map<Integer, String> map = new HashMap<>();
		
		Iterator<Object> i = operations.iterator();
		while(i.hasNext()){
			JSONObject o = (JSONObject) i.next();
			map.put(o.getInt("code"), o.getString("operation"));
		}
		
		System.out.println("Map:");
		for (int opCode : map.keySet()) {
			System.out.println("" + opCode + " --> " + map.get(opCode));
		}
		
		return map;
	}
}
