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
import sk.catheaven.utils.Tuple;

/**
 *
 * @author catlord
 */
public class ALU extends BinaryComponent {
	private static Logger logger;
	
	private final Map<Integer, String> operations;
	private final Tuple<String, Data> aluOp;
	private final Tuple<String, Data> zeroResult;
	
	public ALU(String label, JSONObject json) throws JSONException {
		super(label, json);
		
		ALU.logger = System.getLogger(this.getClass().getName());
		
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
		logger.log(Logger.Level.DEBUG, debugString);
		
		return map;
	}

	@Override
	public void execute() {
		output.setData(0);
		zeroResult.getRight().setData(0);
		
		String operation = operations.get(aluOp.getRight().getData());
		
		if(operation == null){
			logger.log(Logger.Level.WARNING, "Unknown operation code " + aluOp.getRight().getData());
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
			case "mulu": output.setData(inputA.getRight().getData() * inputB.getRight().getData() ); break;					// TODO - maybe just create big data containers to avoid overflow
			case "div": {
				if(inputB.getRight().getData() != 0) output.setData(inputA.getRight().getData() / inputB.getRight().getData() );
				break;
			}
			case "divu": {																				// TODO
				if(inputB.getRight().getData() != 0) output.setData(inputA.getRight().getData() / inputB.getRight().getData() );
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
			
			default: logger.log(Logger.Level.WARNING, "Unknown operation `" + operation + "`"); break;
		}
		
		//DONT FORGET TO SET ZERO-RESULT OUTPUT
		if( ! operation.equals("bneq")  &&  output.getData() == 0)
			zeroResult.getRight().setData(1);
		
		inputA.getRight().setData(0);
		inputB.getRight().setData(0);
	}

	@Override
	public Data getOutput(String selector) {
		if(selector.equals(zeroResult.getLeft())) 
			return zeroResult.getRight();
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
			logger.log(System.Logger.Level.WARNING, label + " --> Unknown request to set data for `" + selector + "`"); 
			return false;
		}
		return true;
	}
}
