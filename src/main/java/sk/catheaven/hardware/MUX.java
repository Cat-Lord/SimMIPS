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
 * Selector describes the component signal, that is selecting the output value.
 * If the selector is inactive (meaning its value is 0), the output is inputA.
 * If the value of the selector is 1, output is set to inputB.
 * @author catlord
 */
public class MUX extends BinaryComponent {
	private final Tuple<String, Data> selector;
	private static Logger logger;
	
	public MUX(String label, JSONObject json) {
		super(label, json);
		
		MUX.logger = System.getLogger(this.getClass().getName());
		
		JSONObject selectorJson = json.getJSONObject("selector");
		selector = new Tuple<>(selectorJson.getString("label"), new Data(selectorJson.getInt("bitSize")));
		
		logger.log(Logger.Level.DEBUG, label + " --> Selector: " + selector.getLeft() + ": " + selector.getRight() + "b");
	}

	@Override
	public void execute() {
		output.setData(
			(selector.getRight().getData() == 0) ? inputA.getRight().getData() : inputB.getRight().getData()
		);
	}
	
	@Override
	public boolean setInput(String selector, Data data) {
		boolean set = super.setInput(selector, data);
		if(selector.equals(this.selector.getLeft())){
			this.selector.getRight().setData(data.getData());
			set = true;
		}
		
		if( ! set){
			logger.log(System.Logger.Level.WARNING, label + " --> Unknown request to set data for `" + selector + "`"); 
			return false;
		}
		return true;
	}
}