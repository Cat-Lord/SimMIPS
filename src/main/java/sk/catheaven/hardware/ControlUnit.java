/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Cutter;

/**
 *
 * @author catlord
 */
public class ControlUnit extends Component {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private String[] signalLabels;		// labels of signals
	private Map<String, Data> signals;
	private Map<Integer, Integer[]> opcodeToSignals;
	private final Map<Integer, Integer> funcToOp;			// mapping of "func" field to alu operations
	
	private final Data input;
	private Data output;
	private final Cutter opcodeParser, funcParser;			// so we don't need to worry about how to get these values, the way is in json
	private final String funcDependant;						// denotes, which signal changes, when we need to parse func field, see setOutput()
	
	/**
	 * From a given json file gets all signals with their respective labels.Each signal
	 * is defined by a label and bit size.This allows easy creation of signals as labeled
	 * data (thus the DataContainer class).
	 * @param label
	 * @param json
	 */
	public ControlUnit(String label, JSONObject json) throws Exception, JSONException {
		super(label, json);
		
		setupSignals(json.getJSONObject("controlCodes"));
		
		String debugOutput = "Signal labels:\n";
		for(int i = 0; i < signalLabels.length; i++)
			debugOutput = debugOutput.concat(String.format("\t%2d: %8s ==> %db data\n", i, signalLabels[i], signals.get(signalLabels[i]).getBitSize()));
		
		System.out.println("\nOpcodes to Signals:");
		for(Integer opcode : opcodeToSignals.keySet()){
			debugOutput = debugOutput.concat(String.format("\t%2d: ", opcode));
			
			Integer[] ints = opcodeToSignals.get(opcode);
			for(int j = 0; j < ints.length; j++)
				debugOutput = debugOutput.concat(String.format("%2d ", ints[j]));
			debugOutput = debugOutput.concat("\n");
		}
		
		logger.log(Level.INFO, debugOutput);
		funcToOp = loadFuncToOp(json.getJSONArray("funcToOperation"));
		
		debugOutput = "Func to operation map:\n";
		for (int func : funcToOp.keySet())
			debugOutput = debugOutput.concat("\t" + func + " --> " + funcToOp.get(func) + "\n");
		logger.log(Level.INFO, debugOutput);
		
		this.funcDependant = json.getString("funcDependant");
		this.input  = new Data(json.getInt("in"));
		this.opcodeParser = new Cutter(Data.MAX_BIT_SIZE, json.getString("opCodeCut"));
		this.funcParser   = new Cutter(Data.MAX_BIT_SIZE, json.getString("funcCut"));
	}
	
	/**
	 * From json file loads all the labels and all respective arrays of values.
	 * @param json
	 * @throws JSONException
	 * @throws NumberFormatException 
	 */
	private void setupSignals(JSONObject json) throws JSONException, NumberFormatException {
		JSONArray labelsJson = json.getJSONArray("codesDescription");
		
		signalLabels = new String[labelsJson.length()];
		signals = new HashMap<>();
		int totalBitSize = 0;
		
		for(int j = 0; j < labelsJson.length(); j++){
			JSONObject currentSignal = (JSONObject) labelsJson.getJSONObject(j);
			String slabel = currentSignal.getString("label");
			signalLabels[j] = slabel;
			
			int bitSize = currentSignal.getInt("bitSize");
			totalBitSize += bitSize;
			
			signals.put(slabel, new Data(bitSize));
		}
		
		this.output = new Data(totalBitSize);
		
		opcodeToSignals = new HashMap<>();
		JSONObject opsTosigs = json.getJSONObject("opcodeToControl");
		
		for(String opcode : opsTosigs.keySet()) {
			JSONArray vals = opsTosigs.getJSONArray(opcode);
			Integer[] sigs = new Integer[signalLabels.length];				// store the valueshere		
			
			for(int j = 0; j < vals.length(); j++)
				sigs[j] = vals.getInt(j);
			
			opcodeToSignals.put(Integer.parseInt(opcode), sigs);
		}
	}

	/**
	 * Creates a map, which binds func field from instruction to operation.
	 * @param json Array of func values and their respective operations.
	 * @return 
	 */
	private Map<Integer, Integer> loadFuncToOp(JSONArray json) {
		Map<Integer, Integer> map = new HashMap<>();
		
		Iterator<Object> i = json.iterator();
		while(i.hasNext()){
			JSONObject o = (JSONObject) i.next();
			map.put(o.getInt("func"), o.getInt("operation"));
		}
		
		return map;
	}

	/**
	 * Get the input, 'parse' it and put the necessary output for this input.
	 * The only extra work is done when input opcode is 0 - then we need to
	 * put output ALU control signal according to func value of instruction.
	 */
	@Override
	public void execute() {
		opcodeParser.setDataToCut(input);
		funcParser.setDataToCut(input);

		int opCode = (int) opcodeParser.getCutData().getData();
		setOutput(opCode, (int) funcParser.getCutData().getData());		
		
		notifySubs();
	}
	
	/**
	 * Get mapping from opcode and set output accordingly. FuncField is provided,
	 * but is used only if the opcode is equal to zero. If opcode equals to zero,
	 * a signal pointed to by label <i>funcDependant</i> will be changed by the 
	 * value defined in <code>funcToOp</code> map.
	 * After the changes to signals, <code>setOutputValue()</code>.
	 * @param opcode Opcode of instruction.
	 */
	private void setOutput(int opcode, int funcField){
		logger.log(Level.INFO, "Setting output by opcode of " + opcode);
		
		Integer[] signalValues = opcodeToSignals.get(opcode);
		
		// check existence
		if(signalValues == null){
			logger.log(Level.WARNING,  "There is no output signal for opcode " + opcode);
			return;
		}
		
		// shift value of output bitwise left and 'append new value'
		for(int i = 0; i < signalLabels.length; i++){
			signals.get(signalLabels[i]).setData(signalValues[i]);
		}
		
		// if input IS func dependant, change the field, which depends on this change
		if(opcode == 0){
			signals.get(funcDependant).setData(funcToOp.get(funcField));
		}
		
		setOutputValue();
	}
	
	/**
	 * Reset output value to zero and bit-wise shift while updating its value
	 * with all the fields known in <code>signalLabels</code> array.
	 */
	private void setOutputValue(){
		// sanity set, prepare output 'default' value
		output.setData(0);

		// shift value of output bitwise left and 'append new value'
		for(int i = 0; i < signalLabels.length; i++){
			Data currentData = signals.get(signalLabels[i]);
			output.setData(
				( (output.getData() << currentData.getBitSize()) | currentData.getData() )
			);
		}
	}

	@Override
	public Data getOutput(String selector) {
		return output.duplicate();
	}

	@Override
	public boolean setInput(String selector, Data data) {
		input.setData(data.getData());
		return true;
	}
	
	public String getStatus(){
		String s = "";
		s = s.concat(String.format(statusFormat, new Object[]{"Input", input.getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{"Output (binary)", output.getBinary()}));
		return s;
	}

	@Override
	public Data getInput(String selector) {
		return input.duplicate();
	}

	@Override
	public void reset() {
		input.setData(0);
		output.setData(0);
	}
}