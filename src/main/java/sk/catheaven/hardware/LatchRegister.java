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
import sk.catheaven.utils.Tuple;

/**
 * JSON description - the inputs in json are simple - one instruction as integer number.
 * Outputs are specified by intervals. Each interval is described by a number, which
 * represent by how many bits does the IF_ID latch should shift. 
 * Format goes like this: "outputLabel" : "shift_to_left-shift_to_right"
 * @author catlord
 */
public class LatchRegister extends Component {
	private static Logger logger;
	Map<String, Data> inputs;						// input labels -> data
	Map<String, ArrayList<String>> iTOo;			// input -> output (only labels)
	Map<String, Output> outputs;					// output labels -> output
	
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
			outputs.get(ol).setData(0);
		});
		
		inputs.keySet().forEach((ins) -> {
			Data insData = inputs.get(ins);
			
			iTOo.get(ins).forEach((ol) -> {
				outputs.get(ol).setData(insData);
			});
		});
		
		inputs.keySet().forEach((il) -> {
			inputs.get(il).setData(0);
		});
	}

	public void setData(String selector, Data data){
		if(inputs.get(selector) != null)
			inputs.get(selector).setData(data.getData());
		else
			logger.log(Logger.Level.ERROR, "No such input: " + selector);
	}
	
	public Data getData(String selector){
		if(outputs.get(selector) == null){
			logger.log(Logger.Level.ERROR, "No such output: " + selector);
			return null;
		}
		return outputs.get(selector).getData();
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
				
				Output opt = new Output(inputBitSize, inputToOutputJson.getString(inToOut));
				outputs.put(inToOut, opt);
				
				debugOutput = debugOutput.concat("\t" + inToOut + ": bitSize " + opt.getData().getBitSize() + "\n");
			}
			
			iTOo.put(in, list);
			logger.log(Logger.Level.DEBUG, debugOutput);
		}
	}
	
	/**
	 * Output inner class to nicely contain all the necessary 
	 * functionality to know for the output. Output can be <i>constant</i>,
	 * which means it will behave exactly like a <code>Data</code> variable.
	 * it can also be <i>ranged</i>, where it will know the range of data it 
	 * will "cut" from. For example range 6-12 means, it will bit-wise shift 
	 * left for 6 bits and right for 12, thus getting only part of original output.
	 *  
	*/
	public class Output {
		private final Data data;
		private Tuple<Integer, Integer> range;
		
		/**
		 * @param originalBitSize The original bitSize this output ties to. Serves to determine bit size of output. 
		 * @param spec Specification of output. Can be constant integer value or range.
		 */
		public Output(int originalBitSize, String spec) throws NumberFormatException {
			range = null;
			
			if(spec.contains("-"))
				this.data = parseRangedValue(originalBitSize, spec);
			else
				this.data = new Data(Integer.parseInt(spec));
		}

		private Data parseRangedValue(int originalBitSize, String spec) throws NumberFormatException {
			int from = Integer.parseInt(spec.substring(0, spec.indexOf("-")));
			int to = Integer.parseInt(spec.substring(spec.indexOf("-") + 1, spec.length()));
			range = new Tuple<>(from, to);
			return new Data(originalBitSize - to);		// original size - shift to right gives the actual data size
		}
		
		public void setData(Data origin){
			if(range == null)
				data.setData(origin.getData());
			else{
				System.out.println("Origin data: " + origin.getHex());
				System.out.println("shifting " + range.getLeft() + " left and " + range.getRight() + " right");
				data.setData(
					(origin.getData() << range.getLeft()) >>> range.getRight()
				);
			}
		}
		
		public void setData(int data){
			this.data.setData(data);
		}
		
		public Data getData(){
			return data;
		}
	}
}
