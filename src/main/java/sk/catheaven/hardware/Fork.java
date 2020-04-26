/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.lang.System.Logger;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Cutter;

/**
 *
 * @author catlord
 */
public class Fork extends Component {
	private static Logger logger;
	
	private final Data input;
	private final Map<String, Cutter> outputs;
	
	public Fork(String label, JSONObject json) throws JSONException {
		super(label);
		
		Fork.logger = System.getLogger(this.getClass().getName());
		
		input = new Data(json.getInt("in"));
		outputs = new HashMap<>();
		
		JSONObject ots = json.getJSONObject("out");
		
		ots.keySet().forEach((oLabel) -> {
			Cutter ctr = new Cutter(input.getBitSize(), ots.getString(oLabel));
			outputs.put(oLabel, ctr);
			logger.log(Logger.Level.DEBUG, "Output " + oLabel);
		});
	}

	/**
	 * Pass input to all of the outputs and let the outputs handle the values.
	 */
	@Override
	public void execute() {
		String debugOutput = "Cutting input: " + input.getHex();
		
		for(String oLabel : outputs.keySet()){
			Cutter ctr = outputs.get(oLabel);
			ctr.setDataToCut(0);					// reset data just in case
			ctr.setDataToCut(input);				// set data to cut
			debugOutput = debugOutput.concat("\t" + ctr.getCutData().getData());
		}
		
		logger.log(Logger.Level.DEBUG, debugOutput);
		input.setData(0);
	}

	@Override
	public Data getData(String selector) {
		Cutter ctr = outputs.get(selector);
		
		if(ctr == null){
			logger.log(Logger.Level.WARNING, "Unknown selector for output `" + selector + " ! Returning empty data");
			return new Data();
		}
		return ctr.getCutData();
	}

	@Override
	public void setData(String selector, Data data) {
		input.setData(data.getData());
	}
	
}