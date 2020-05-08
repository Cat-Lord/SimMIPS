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
	private final Tuple<String, Data> selector;
	private final Data input, output;
	private final Data constant;
	
	public ConstMUX(String label, JSONObject json) {
		super(label, json);
		
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
		
		notifySubs();
	}

	@Override
	public Data getOutput(String selector) {
		return output.duplicate();
	}

	@Override
	public boolean setInput(String selector, Data data) {
		if(this.selector.getLeft().equals(selector))
			this.selector.getRight().setData(data.getData());
		else
			input.setData(data.getData());
		return true;
	}
	
	public String getStatus(){
		String s = "";
		s = s.concat(String.format(statusFormat, new Object[]{selector.getLeft(), selector.getRight().getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{"Input", input.getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{"Output", output.getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{"Const", constant.getHex()}));
		return s;
	}

	@Override
	public Data getInput(String selector) {
		if(selector.equals(this.selector.getLeft()))
			return this.selector.getRight().duplicate();
		return input.duplicate();
	}

	@Override
	public void reset() {
		input.setData(0);
		output.setData(0);
		selector.getRight().setData(0);
	}
}
