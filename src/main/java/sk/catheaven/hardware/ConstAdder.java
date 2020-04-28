/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Tuple;

/**
 * Adder, which adds a constant to the input. The constant is fixed after first 
 * loaded from constructor (in json).
 * @author catlord
 */
public class ConstAdder extends Component {
	private final Tuple<String, Data> input;
	private final Data constant;
	private final Data output;
	
	public ConstAdder(String label, JSONObject json) {
		super(label);
		
		JSONObject inJson = json.getJSONObject("input");
		input = new Tuple<>(inJson.getString("label"), new Data(inJson.getInt("bitSize")));
		
		output = new Data(json.getInt("output"));
		constant = new Data();
		constant.setData(json.getInt("const"));
	}
	
	@Override
	public void execute() {
		output.setData(
			input.getRight().getData() + constant.getData()
		);
	}

	@Override
	public Data getOutput(String selector) {
		return output.duplicate();
	}

	@Override
	public boolean setInput(String selector, Data data) {
		input.getRight().setData(data.getData());
		return true;
	}
}
