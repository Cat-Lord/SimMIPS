/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.lang.System.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Cutter;

/**
 * JSON description - the inputs in json are simple - one instruction as integer number.
 * Outputs are specified by intervals. Each interval is described by a number, which
 * represent by how many bits does the IF_ID latch should shift. 
 * Format goes like this: "outputLabel" : "shift_to_left-shift_to_right".
 * @author catlord
 */
public class LatchRegister extends Component {
	private static Logger logger;
	Map<String, Data> inputs;						// input labels -> data
	Map<String, ArrayList<String>> iTOo;			// input -> output (only labels)
	Map<String, Cutter> outputs;					// output labels -> output
	
	public LatchRegister(String label, JSONObject json) throws Exception {
		super(label);
		LatchRegister.logger = System.getLogger(this.getClass().getName());
		
		inputs = new HashMap<>();
		iTOo = new HashMap<>();
		outputs = new HashMap<>();
		
		setupIO(json);
	}

	/**
	 * First <b>clears</b> outputs and after that moves all information on inputs to the specific
	 * outputs. Each output knows, how to handle its information. After this move <b>all of the 
	 * inputs are cleared</b> as well.
	 */
	@Override
	public void execute() {
		// clearing outputs
		outputs.keySet().forEach((ol) -> {
			outputs.get(ol).setDataToCut(0);
		});
		
		inputs.keySet().forEach((ins) -> {
			Data insData = inputs.get(ins);
			
			iTOo.get(ins).forEach((ol) -> {
				outputs.get(ol).setDataToCut(insData);
			});
		});
		
		inputs.keySet().forEach((il) -> {
			inputs.get(il).setData(0);
		});
	}

	@Override
	public boolean setInput(String selector, Data data){
		if(inputs.get(selector) == null){
			logger.log(System.Logger.Level.WARNING, label + " --> Unknown request to set data for `" + selector + "`"); 
			return false;
		}
			
		inputs.get(selector).setData(data.getData());
		return true;			
	}
	
	@Override
	public Data getOutput(String selector){
		if(outputs.get(selector) == null){
			logger.log(Logger.Level.ERROR, "No such output: " + selector);
			return null;
		}
		return outputs.get(selector).getCutData().duplicate();
	}
	
	/**
	 * Since input can result in multiple outputs, we need to first get an input,
	 * and then load every output for this specific input. For example:
	 * An 15 bit input "iCode" has 3 outputs "A" "B" "C" and json could look like this:
	 * "in":  { "iCode": 20 },
	 * "out": { "iCode": { "A": "", "B" : "", "C": "" }
	 * @param json
	 * @throws JSONException 
	 */
	private void setupIO(JSONObject json) throws Exception, JSONException {
		JSONObject outJson = json.getJSONObject("out");		// all of the outputs
		JSONObject inJson  = json.getJSONObject("in");

		// for each input
		for(String in : inJson.keySet()){
			int inputBitSize = inJson.getInt(in);				// load its bit size
			inputs.put(in, new Data(inputBitSize));				// store this input
			
			logger.log(Logger.Level.DEBUG, in + ": bitSize " + inputBitSize);
			String debugOutput = "Output:\n";
			
			ArrayList<String> list = new ArrayList();
			
			// and store all outputs tied to this input
			JSONObject inputToOutputJson = outJson.getJSONObject(in);
			for(String inToOut : inputToOutputJson.keySet()){
				list.add(inToOut);			// remember mapping
				
				Cutter opt = new Cutter(inputBitSize, inputToOutputJson.getString(inToOut));
				outputs.put(inToOut, opt);
				
				debugOutput = debugOutput.concat("\t" + inToOut + ": bitSize " + opt.getCutData().getBitSize() + "\n");
			}
			
			iTOo.put(in, list);
			logger.log(Logger.Level.DEBUG, debugOutput);
		}
	}
}
