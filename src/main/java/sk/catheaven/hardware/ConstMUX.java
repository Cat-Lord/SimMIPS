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
public class ConstMUX extends Component {
	private Data selector;
	private Data inputA, constant;
	private Data output;
	
	public ConstMUX(String label, JSONObject json) {
		super(label);
		
		setupIO(json);
	}
	
	private void setupIO(JSONObject json){
		inputA = new Data(json.getInt("inputA"));
		output = new Data(json.getInt("output"));
		
		constant = new Data();
		constant.setData(json.getInt("const"));
		
		selector = new Data(1);
	}
	
	@Override
	public void execute() {
		output.setData(
			(selector.getData() == 0) ? inputA.getData() : constant.getData()
		);
	}
	
}
