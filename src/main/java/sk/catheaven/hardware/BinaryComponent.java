/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import sk.catheaven.utils.Datapathable;
import java.util.logging.Logger;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.run.Connector;
import sk.catheaven.utils.Observable;
import sk.catheaven.utils.Subscriber;
import sk.catheaven.utils.Tuple;

/**
 *
 * @author catlord
 */
public abstract class BinaryComponent extends Component implements Datapathable, Observable {
	protected final Tuple<String, Data> inputA, inputB;
	protected final Data output;
	
	public BinaryComponent(String label, JSONObject json) {
		super(label, json);
		
		JSONObject iA = json.getJSONObject("inputA");
		this.inputA = new Tuple<>(iA.getString("label"), new Data(iA.getInt("bitSize")));
		
		JSONObject iB = json.getJSONObject("inputB");
		this.inputB = new Tuple<>(iB.getString("label"), new Data(iB.getInt("bitSize")));
		
		this.output = new Data(json.getInt("output"));
	}
	
	/**
	 * Allows investigating value of input.
	 * @param inputLabel
	 * @return 
	 */
	public Data getInput(String inputLabel){
		if(inputA.getLeft().equals(inputLabel))
			return inputA.getRight().duplicate();
		else if(inputB.getLeft().equals(inputLabel))
			inputB.getRight().duplicate();
		return null;
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
	
	@Override
	public String getStatus(){
		String s = "";
		s = s.concat(String.format(statusFormat, new Object[]{inputA.getLeft(), inputA.getRight().getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{inputB.getLeft(), inputB.getRight().getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{"Output", output.getHex()}));
		return s;
	}
	
	@Override
	public void reset(){
		inputA.getRight().setData(0);
		inputB.getRight().setData(0);
		output.setData(0);
	}
}
