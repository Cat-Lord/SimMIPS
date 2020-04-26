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
		
	public MUX(String label, JSONObject json) {
		super(label, json);
		
		JSONObject selectorJson = json.getJSONObject("selector");
		selector = new Tuple<>(selectorJson.getString("label"), new Data(selectorJson.getInt("selector")));
		
		logger.log(Logger.Level.DEBUG, label + " --> Selector: " + selector.getLeft() + ": " + selector.getRight() + "b");
	}

	@Override
	public void execute() {
		output.getRight().setData(
			(selector.getRight().getData() == 0) ? inputA.getRight().getData() : inputB.getRight().getData()
		);
	}
	
	@Override
	public void setData(String inputLabel, Data data) {
		switch(inputLabel){
			case "inputA": inputA.getRight().setData(data.getData()); break;
			case "inputB": inputB.getRight().setData(data.getData()); break;
			
			default: { 
				if(inputLabel.equals(selector.getLeft()))
					selector.getRight().setData(data.getData());
				else
					logger.log(System.Logger.Level.WARNING, label + " --> Unknown request to set data for " + inputLabel); 
				break; 
			}
		}
	}
}