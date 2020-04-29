/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.lang.System.Logger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Cutter;

/**
 * Forks the input value into exactly multiple output values. It is expected
 * to fork a single input value into two output values, but not required/constrained.
 * Input json file is expected to define output array of values. If this array 
 * contains only a single value, it behaves like an exact copy of the input value. 
 * Meaning requesting the value with <code>getOutput("value")</code> would just copy 
 * result of only one value and duplicate it.
 * Specifying multiple input values will result in multiple output values on 
 * each separate request.
 * @author catlord
 */
public class Fork extends Component {
	private static Logger logger;
	
	private final Data input;
	private final Map<String, Cutter> outputs;
	
	public Fork(String label, JSONObject json) throws Exception, JSONException {
		super(label);
		
		Fork.logger = System.getLogger(this.getClass().getName());
		
		input = new Data(json.getInt("in"));
		outputs = new HashMap<>();
		
		// array of json objects, because sometimes we fork output and name it the same (and json doesn't allow multiple labels of the same string)
		JSONArray ots = json.getJSONArray("out");
		
		// iterate over all json objects
		Iterator<Object> jitter = ots.iterator();
		while(jitter.hasNext()){
			JSONObject outJO = (JSONObject) jitter.next();
			
			String oLabel = outJO.getString("label");
			String bitSizeRange = outJO.getString("bitSize");
			
			Cutter ctr = new Cutter(input.getBitSize(), bitSizeRange);
			outputs.put(oLabel, ctr);
			logger.log(Logger.Level.DEBUG, "Output " + oLabel);
		}
	}

	/**
	 * Pass input to all of the outputs and let the outputs handle the values.
	 */
	@Override
	public void execute() {
		String debugOutput = "Cutting input: " + input.getHex() + "\n";
		
		for(String oLabel : outputs.keySet()){
			Cutter ctr = outputs.get(oLabel);
			ctr.setDataToCut(0);					// reset data just in case
			ctr.setDataToCut(input);				// set data to cut
			debugOutput = debugOutput.concat("\t" + ctr.getCutData().getData() + "\n");
		}
		
		logger.log(Logger.Level.DEBUG, debugOutput);
		input.setData(0);
	}

	@Override
	public Data getOutput(String selector) {
		Cutter ctr = outputs.get(selector);
		
		if(ctr == null){
			logger.log(Logger.Level.WARNING, "Unknown output for selector `" + selector + " ! Returning empty data");
			return new Data();
		}
		return ctr.getCutData();
	}

	@Override
	public boolean setInput(String selector, Data data) {
		input.setData(data.getData());
		return true;
	}
	
}