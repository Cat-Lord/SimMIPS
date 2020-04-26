/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.lang.System.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Tuple;

/**
 *
 * @author catlord
 */
public abstract class BinaryComponent extends Component implements Datapathable {
	protected static Logger logger;
	protected Tuple<String, Data> inputA, inputB;
	protected Tuple<String, Data> output;
	
	public BinaryComponent(String label, JSONObject json) {
		super(label);
		
		setupIO(json);
	}
	
	protected final void setupIO(JSONObject json) throws JSONException {
		JSONObject iA = json.getJSONObject("inputA");
		this.inputA = new Tuple<>(iA.getString("label"), new Data(iA.getInt("bitSize")));
		
		JSONObject iB = json.getJSONObject("inputB");
		this.inputB = new Tuple<>(iB.getString("label"), new Data(iB.getInt("bitSize")));
		
		JSONObject op = json.getJSONObject("output");
		this.output = new Tuple<>(op.getString("label"), new Data(op.getInt("bitSize")));
	}
	
	@Override
	public Data getData(String outputLabel) {
		return output.getRight();
	}

	@Override
	public void setData(String inputLabel, Data data) {
		if(inputA.getLeft().equals(inputLabel))
			inputA.getRight().setData(data.getData());
		
		else if(inputB.getLeft().equals(inputLabel))
			inputB.getRight().setData(data.getData());
		
		else
			logger.log(System.Logger.Level.WARNING, label + " --> Unknown request to set data for " + inputLabel);
	}
}
