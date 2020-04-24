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
public class ConstAdder extends Component {
	private Data inputA, constant;
	private Data output;
	
	public ConstAdder(String label, JSONObject json) {
		super(label);
		
		setupIO(json);
	}

	private void setupIO(JSONObject json) {
		inputA = new Data(json.getInt("inputA"));
		output = new Data(json.getInt("output"));
		constant = new Data();
		constant.setData(json.getInt("const"));
	}
	
	@Override
	public void execute() {
		output.setData(
			inputA.getData() + constant.getData()
		);
	}
}
