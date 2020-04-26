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
 * Program counter, which stores and forwards address of next instruction to load.
 * @author catlord
 */
public class PC extends Component {
	private Tuple<String, Data> input;
	private Data output;
	
	public PC(String label, JSONObject json) {
		super(label);
		
		setupIO(json);
	}

	private void setupIO(JSONObject json) {
		JSONObject inputJson = json.getJSONObject("input");
		input = new Tuple<>(inputJson.getString("label"), new Data(inputJson.getInt("bitSize")));
		output = new Data(json.getInt("output"));
	}

	@Override
	public void execute() {
		output.setData(input.getRight().getData());
	}

	@Override
	public Data getData(String selector) {
		return output.duplicate();
	}

	@Override
	public void setData(String selector, Data data) {
		input.getRight().setData(data.getData());
	}
	
}
