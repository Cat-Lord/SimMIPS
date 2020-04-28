/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.lang.System.Logger;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Tuple;

/**
 *
 * @author catlord
 */
public abstract class BinaryComponent extends Component implements Datapathable {
	protected final Tuple<String, Data> inputA, inputB;
	protected final Data output;
	
	public BinaryComponent(String label, JSONObject json) {
		super(label);
		
		JSONObject iA = json.getJSONObject("inputA");
		this.inputA = new Tuple<>(iA.getString("label"), new Data(iA.getInt("bitSize")));
		
		JSONObject iB = json.getJSONObject("inputB");
		this.inputB = new Tuple<>(iB.getString("label"), new Data(iB.getInt("bitSize")));
		
		this.output = new Data(json.getInt("output"));
	}
	
	@Override
	public Data getOutput(String outputLabel) {
		return output.duplicate();
	}

	@Override
	public boolean setInput(String inputLabel, Data data) {
		if(inputA.getLeft().equals(inputLabel))
			inputA.getRight().setData(data.getData());
		else if(inputB.getLeft().equals(inputLabel))
			inputB.getRight().setData(data.getData());
		else
			return false;
		
		return true;
	}
}
