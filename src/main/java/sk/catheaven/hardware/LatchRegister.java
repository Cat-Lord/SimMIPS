/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Cutter;
import sk.catheaven.utils.Tuple;

/**
 * JSON description - the inputs in json are simple - one instruction as integer number.
 * Outputs are specified by intervals. Each interval is described by a number, which
 * represent by how many bits does the IF_ID latch should shift. 
 * Format goes like this: "outputLabel" : "shift_to_left-shift_to_right".
 * It is allowed for a latch register to have an input signal, that defines its output
 * validity. It behaves similarly to const MUX - if that signal is 0, output is .
 * traditional but if that signal is 1 (active), output is 0. This is a simple way to  
 * handle data hazard.
 * @author catlord
 */
public class LatchRegister extends Component {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	Map<String, Data> inputs;						// input labels -> data
	Map<String, ArrayList<String>> iTOo;			// input -> output (only labels)
	Map<String, Cutter> outputs;					// output labels -> output
	private final Tuple<String, Data> bubble;
	
	public LatchRegister(String label, JSONObject json) throws Exception {
		super(label);
		
		
		inputs = new HashMap<>();
		iTOo = new HashMap<>();
		outputs = new HashMap<>();
		
		setupIO(json);
		
		JSONObject so = (JSONObject) json.opt("bubble");
		if(so != null)
			bubble = new Tuple<>(so.getString("label"), new Data(so.getInt("bitSize")));
		else
			bubble = new Tuple<>("EMPTY_BUBBLE_SIGNAL", new Data(1));		// empty data, with the value of 0 always
	}

	/**
	 * First <b>clears</b> outputs and after that moves all information on inputs to the specific
	 * outputs. Each output knows, how to handle its information. After this move <b>all of the 
	 * inputs are cleared</b> as well.
	 */
	@Override
	public void execute() {
		// if we skip, outputs are already cleared, so don't update them
		if(bubble.getRight().getData() == 0)
			inputs.keySet().forEach((ins) -> {
				Data insData = inputs.get(ins);

				iTOo.get(ins).forEach((ol) -> {
					outputs.get(ol).setDataToCut(insData);
				});
			});
	}

	@Override
	public boolean setInput(String selector, Data data){
		if(inputs.get(selector) != null){
			inputs.get(selector).setData(data.getData());
			return true;
		}
		else if(selector.equals(bubble.getLeft())){
			bubble.getRight().setData(data.getData());
			return true;
		}
		logger.log(Level.WARNING, label + " --> Unknown request to set data for `" + selector + "`"); 
		return false;			
	}
	
	/**
	 * Returns output value specified by the selector. If there is no output with 
	 * the label specified by selector, returns empty data.
	 * @param selector
	 * @return 
	 */
	@Override
	public Data getOutput(String selector){
		if(outputs.get(selector) == null){
			logger.log(Level.WARNING,  "No such output: " + selector);
			return new Data();
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
			
			logger.log(Level.INFO, in + ": bitSize " + inputBitSize);
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
			logger.log(Level.INFO, debugOutput);
		}
	}
	
	public String getStatus(){
		String s = "Inputs:\n";
		for(String ss : inputs.keySet())
			s = s.concat(String.format(statusFormat, new Object[]{ss, inputs.get(ss).getHex()}));
		
		s = s.concat("\nOutputs:\n");
		for(String ss : outputs.keySet())
			s = s.concat(String.format(statusFormat, new Object[]{ss, outputs.get(ss).getCutData().getHex()}));
		
		s = s.concat(String.format(statusFormat, new Object[]{bubble.getLeft(), bubble.getRight().getHex()}));
		return s;
	}

	@Override
	public Data getInput(String selector) {
		if(inputs.get(selector) == null)
			return null;
		return inputs.get(selector).duplicate();
	}
}
