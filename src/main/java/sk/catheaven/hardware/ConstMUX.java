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
 * MUX with one input and the second one is fixed constant. This constant set from input json
 * from the constructor.
 * @author catlord
 */
public class ConstMUX extends Component {
	private Tuple<String, Data> selector;
	private Data input, constant;
	private Data output;
	
	public ConstMUX(String label, JSONObject json) {
		super(label);
		
		setupIO(json);
	}
	
	private void setupIO(JSONObject json){
		input = new Data(json.getInt("input"));
		output = new Data(json.getInt("output"));
		
		constant = new Data();
		constant.setData(json.getInt("const"));
		
		JSONObject so = json.getJSONObject("selector");
		selector = new Tuple<>(so.getString("label"), new Data(so.getInt("bitSize")));
	}
	
	@Override
	public void execute() {
		output.setData(
			(selector.getRight().getData() == 0) ? input.getData() : constant.getData()
		);
	}

	@Override
	public Data getData(String selector) {
		return output.duplicate();
	}

	@Override
	public void setData(String selector, Data data) {
		if(this.selector.getLeft().equals(selector))
			this.selector.getRight().setData(data.getData());
		else {
			input.setData(data.getData());
		}
	}
	
}
