/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Tuple;

/**
 * Selector describes the component signal, that is selecting the output value.
 * If the selector is inactive (meaning its value is 0), the output is inputA.
 * If the value of the selector is 1, output is set to inputB.
 * @author catlord
 */
public class MUX extends BinaryComponent {
	private final Tuple<String, Data> selector;
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public MUX(String label, JSONObject json) {
		super(label, json);
		
		JSONObject selectorJson = json.getJSONObject("selector");
		selector = new Tuple<>(selectorJson.getString("label"), new Data(selectorJson.getInt("bitSize")));
		
		logger.log(Level.INFO, "{0} --> Selector: {1}: {2}b", new Object[]{label, selector.getLeft(), selector.getRight().getHex()});
	}

	@Override
	public void execute() {
		output.setData(
			(selector.getRight().getData() == 0) ? inputA.getRight().getData() : inputB.getRight().getData()
		);
		
		notifySubs();
	}
	
	@Override
	public Data getInput(String selector){
		Data d = super.getInput(selector);
		if(d != null) return d;
		
		if(selector.equals(this.selector.getLeft())) return this.selector.getRight().duplicate();
		return null;
	}
	
	@Override
	public boolean setInput(String selector, Data data) {
		boolean set = super.setInput(selector, data);
		if(selector.equals(this.selector.getLeft())){
			this.selector.getRight().setData(data.getData());
			set = true;
		}
		
		if( ! set){
			logger.log(Level.WARNING, "{0} --> Unknown request to set data for `{1}`", new Object[]{label, selector}); 
			return false;
		}
		return true;
	}
	
	@Override
	public String getStatus(){
		String s = super.getStatus();
		s = s.concat(String.format(statusFormat, new Object[]{selector.getLeft(), selector.getRight().getHex()}));
		return s;
	}

	@Override
	public void reset() {
		super.reset();
		selector.getRight().setData(0);
	}
}