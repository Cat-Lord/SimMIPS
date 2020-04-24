/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class BinaryComponent extends Component {
	protected Data inputA, inputB;
	protected Data output;
	
	public BinaryComponent(String label, JSONObject json) {
		super(label);
		
		setupIO(json);
	}
	
	protected void setupIO(JSONObject json) throws JSONException {
		inputA = new Data(json.getInt("inputA"));
		inputB = new Data(json.getInt("inputB"));
		output = new Data(json.getInt("output"));
	}
}
