/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class PC extends Component {
	private Data input, output;
	
	public PC(String label, JSONObject json) {
		super(label);
		
		setupIO(json);
	}

	private void setupIO(JSONObject json) {
		input = new Data();
		output = new Data();
	}

	@Override
	public void execute() {
		output.setData(input.getData());
	}
	
}
