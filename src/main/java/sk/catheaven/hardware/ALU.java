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
import sk.catheaven.utils.Tuple;

/**
 *
 * @author catlord
 */
public class ALU extends BinaryComponent {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private final Map<Integer, String> operations;
	private final Tuple<String, Data> aluOp;
	private final Tuple<String, Data> zeroResult;
	
	public ALU(String label, JSONObject json) throws JSONException {
		super(label, json);
		
		this.operations = assignOperations(json.getJSONArray("operations"));
		
		JSONObject aop = json.getJSONObject("aluOp");
		this.aluOp  = new Tuple<>(aop.getString("label"), new Data(aop.getInt("bitSize")));
		
		JSONObject zrs = json.getJSONObject("zeroResult");
		this.zeroResult = new Tuple<>(zrs.getString("label"), new Data(zrs.getInt("bitSize")));
	}
	
	/**
	 * Creates map, which binds code value to specific operation, for example 1 could be <i>add</i>.
	 * @param operations Array of code values and respective operations.
	 * @return 
	 */
	private Map<Integer, String> assignOperations(JSONArray operations){
		Map<Integer, String> map = new HashMap<>();
		
		Iterator<Object> i = operations.iterator();
		while(i.hasNext()){
			JSONObject o = (JSONObject) i.next();
			map.put(o.getInt("code"), o.getString("operation"));
		}
		
		// log info into logger
		String debugString  = "Map:\n";
		for (int opCode : map.keySet()) {
			debugString = debugString.concat(opCode + " --> " + map.get(opCode) + "\n");
		}
		logger.log(Level.INFO, debugString);
		
		return map;
	}

	@Override
	public void execute() {
		output.setData(0);
		zeroResult.getRight().setData(0);
		
		String operation = operations.get(aluOp.getRight().getData());
		
		if(operation == null){
			logger.log(Level.WARNING, "Unknown operation code {0}", aluOp.getRight().getData());
			return;
		}
		
		switch(operation){
			case "add": output.setData(inputA.getRight().getData() + inputB.getRight().getData() ); break;
			case "sub": output.setData(inputA.getRight().getData() - inputB.getRight().getData() ); break;
			case "and": output.setData(inputA.getRight().getData() & inputB.getRight().getData() ); break;
			case  "or": output.setData(inputA.getRight().getData() | inputB.getRight().getData() ); break;
			case "nor": output.setData( ~(inputA.getRight().getData() |  inputB.getRight().getData()) ); break;
			case "xor": output.setData(inputA.getRight().getData() ^ inputB.getRight().getData() ); break;
			case "mul": output.setData(inputA.getRight().getData() * inputB.getRight().getData() ); break;
			case "mulu": output.setData( (int) ((long) inputA.getRight().getData() * (long) inputB.getRight().getData()) ); break;					// TODO - Check correctness.
			case "div": {
				if(inputB.getRight().getData() != 0) output.setData(inputA.getRight().getData() / inputB.getRight().getData() );
				break;
			}
			case "divu": {																				// TODO Check correctness !
				if(inputB.getRight().getData() != 0) output.setData( (int) ((long) inputA.getRight().getData() / (long) inputB.getRight().getData()) );
				break;
			}
			case "bneq": { 
				output.setData(inputA.getRight().getData() - inputB.getRight().getData() );		// bneq is actually sub
				
				// if the output is NOT zero, we've got different numbers
				if(output.getData() != 0)
					zeroResult.getRight().setData(1);
				break;			
			} 
			case "sllv": output.setData(inputA.getRight().getData()  << inputB.getRight().getData() ); break;
			case "srlv": output.setData(inputA.getRight().getData() >>> inputB.getRight().getData() ); break;
			case "lui":{
				output.setData(
					inputA.getRight().getData() << (inputA.getRight().getBitSize() / 2)
				);
				break;
			}
			
			default: logger.log(Level.WARNING, "Unknown operation `{0}`", operation); break;
		}
		
		//DONT FORGET TO SET ZERO-RESULT OUTPUT
		if( ! operation.equals("bneq")  &&  output.getData() == 0)
			zeroResult.getRight().setData(1);
		
		inputA.getRight().setData(0);
		inputB.getRight().setData(0);
	}

	@Override
	public Data getOutput(String selector) {
		if(selector.equals(zeroResult.getLeft())) {
			logger.log(Level.INFO, "Returning zero result signal value (selector `{0}`)", selector);
			return zeroResult.getRight();
		}
		return output.duplicate();
	}

	@Override
	public boolean setInput(String selector, Data data) {
		boolean set = super.setInput(selector, data);
		
		if(selector.equals(aluOp.getLeft())){
			set = true;
			aluOp.getRight().setData(data.getData());
		}
		
		if( ! set){
			logger.log(Level.WARNING, "{0} --> Unknown request to set data for `{1}`", new Object[]{label, selector}); 
			return false;
		}
		return true;
	}
}