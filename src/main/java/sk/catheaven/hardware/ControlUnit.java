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
import sk.catheaven.instructionEssentials.DataContainer;

/**
 *
 * @author catlord
 */
public class ControlUnit extends Component {
	private static Logger logger;
	private final DataContainer[] controlSignals;			// all the known signals and their data
	private final Map<Integer, Integer[]> signalValues;		// mapping of opcodes to final signal values
	private final Map<Integer, Integer> funcToOp;			// mapping of "func" field to alu operations
	
	/**
	 * @param label Label of this control unit.
	 * @param json ControlUnit json object with all necessary information required.
	 * @throws JSONException
	 * @throws Exception 
	 */
	public ControlUnit(String label, JSONObject json) throws JSONException, Exception {
		super(label);
		
		controlSignals = assignControlSignals(json.getJSONObject("controlCodes").getJSONObject("codesDescription"));
		signalValues = assignControlValues(json.getJSONObject("controlCodes").getJSONObject("opcodeToControl"), controlSignals.length);
		funcToOp = assignFuncToOp(json.getJSONArray("funcToOperation"));
		
		if(controlSignals == null || signalValues == null || funcToOp == null)
			throw new Exception("Undefined objects in CU (" + controlSignals + ", " + signalValues + ", " + funcToOp + ")");
		
		ControlUnit.logger = System.getLogger(this.getClass().getName());
	}
	
	/**
	 * From a given json file gets all signals with their respective labels. Each signal
	 * is defined by a label and bit size. This allows easy creation of signals as labeled
	 * data (thus the DataContainer class).
	 * @param signalsJson Json object containing all signals specified as <code>"signalLabel" : { "bitSize": X }</code> where X is a valid number.
	 * @return Array of labeled data as DataContainer[].
	 */
	private DataContainer[] assignControlSignals(JSONObject signalsJson){
		DataContainer[] signals = new DataContainer[signalsJson.length()];
		
		int i = 0;
		for(String label: signalsJson.keySet()){
			JSONObject o = signalsJson.getJSONObject(label);
			signals[i++] = new DataContainer(label, o.getInt("bitSize"));
		}
		
		// TODO output only for logger
		System.out.println("Control signals:");
		for(i = 0; i < signals.length; i++)
			System.out.println("\tSignal `" + signals[i].getLabel() + "` has bit size of " + signals[i].getData().getBitSize() + "b");
		
		return signals;
	}
	
	/**
	 * When the control signals are known (by label and bit size), this method creates maps
	 * opcodes to signal values. For example opcode 3 could map to "AluSrc" = 1, "RegWrite" = 1, etc.
	 * @param codesJson Json object containing all the necessary mapping (string label and array, for example
	 * <code>"3": [1,1,0]</code>.
	 * @param limit Only and exactly this amount of numbers is allowed.
	 * @return 
	 */
	private Map<Integer, Integer[]> assignControlValues(JSONObject codesJson, int limit) throws Exception {
		Map<Integer, Integer[]> map = new HashMap<>();
		
		for(String opcodeString: codesJson.keySet()){
			JSONArray jsonSignalValues = codesJson.getJSONArray(opcodeString);
			int arrLen = jsonSignalValues.length();
			
			if(arrLen != limit)
				throw new Exception("Number of values (" + arrLen + ") doesn't fit expected count of " + limit + " elements");
				
			
			// opcode will be represented as integer number for easy manipulation
			int opcode = 0; 
			try{
				opcode = Integer.parseInt(opcodeString);
			} catch(NumberFormatException e) { 
				logger.log(Logger.Level.WARNING, e.getMessage()); 
				throw new Exception("Opcode `" + opcodeString + "` is not a number !");
			}
			
			if(map.get(opcode) != null)
				throw new Exception("Opcode `" + opcodeString + "` has multiple control signal values assigned !");
			
			
			Integer values[] = new Integer[arrLen];		// array of signal values
			
			// remember all the values for this specifigc opcode
			int i = 0;
			Iterator<Object> it = jsonSignalValues.iterator();
			while(it.hasNext())
				values[i++] = (Integer)it.next();
			
			map.put(opcode, values);
		}
		
		
		// TODO output only for logger
		System.out.println("Control signal values:");
		for(Integer it : map.keySet()){
			Integer arr[] = map.get(it);
			System.out.print("\t" + it + ": ");
			
			for(int j = 0; j < arr.length; j++)
				System.out.print(arr[j] + " ");
			System.out.print("\n");
		}
		
		return map;
	}
	
	/**
	 * Creates a map, which binds func field from instruction to operation.
	 * @param funcs Array of func values and their respective operations.
	 * @return 
	 */
	private Map<Integer, Integer> assignFuncToOp(JSONArray funcs){
		Map<Integer, Integer> map = new HashMap<>();
		
		Iterator<Object> i = funcs.iterator();
		while(i.hasNext()){
			JSONObject o = (JSONObject) i.next();
			map.put(o.getInt("func"), o.getInt("operation"));
		}
		
		System.out.println("\nFunc to operation map:");
		for (int func : map.keySet()) {
			System.out.println("\t" + func + " --> " + map.get(func));
		}
		
		return map;
	}
	
	/**
	 * Returns array of values for a given opcode.
	 * @param opcode From an opcode get all the values as int array.
	 * @return 
	 */
	public int[] getControlSignals(int opcode){
		Integer op = (Integer)opcode;
		Integer values[] = signalValues.get(op);
		
		if(values == null){
			logger.log(Logger.Level.WARNING, "Asking for unknown values of control signals (opcode " + opcode + ") !");
			return new int[0];
		}
		
		int ret[] = new int[values.length];
		for(int i = 0; i < values.length; i++)
			ret[i] = values[i];
		
		return ret;
	}
	
}
