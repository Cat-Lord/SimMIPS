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
public class ALU extends Component {
	private static Logger logger;
	
	private final Map<Integer, String> operations;
	private final Tuple<String, Data> aluOp;
	private final Tuple<String, Data> inputA, inputB, output;
	private final Tuple<String, Data> zeroResult;
	
	public ALU(String label, JSONObject json) throws JSONException {
		super(label);
		
		ALU.logger = System.getLogger(this.getClass().getName());
		
		this.operations = assignOperations(json.getJSONArray("operations"));
		
		JSONObject iA = json.getJSONObject("inputA");
		this.inputA = new Tuple<>(iA.getString("label"), new Data(iA.getInt("bitSize")));
		
		JSONObject iB = json.getJSONObject("inputB");
		this.inputB = new Tuple<>(iB.getString("label"), new Data(iB.getInt("bitSize")));
		
		JSONObject op = json.getJSONObject("output");
		this.output = new Tuple<>(op.getString("label"), new Data(op.getInt("bitSize")));
		
		JSONObject aop = json.getJSONObject("aluOp");
		this.aluOp  = new Tuple<>(aop.getString("label"), new Data(aop.getInt("bitSize")));
		
		JSONObject zrs = json.getJSONObject("zeroResult");
		this.zeroResult = new Tuple<>(aop.getString("label"), new Data(aop.getInt("bitSize")));
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
		String debugString  = "Map:";
		for (int opCode : map.keySet()) {
			debugString = debugString.concat(opCode + " --> " + map.get(opCode));
		}
		logger.log(Logger.Level.DEBUG, debugString);
		
		return map;
	}

	@Override
	public void execute() {
		output.getRight().setData(0);
		zeroResult.getRight().setData(0);
		
		String operation = operations.get(aluOp.getRight().getData());
		
		if(operation == null){
			logger.log(Logger.Level.WARNING, "Unknown operation code " + aluOp.getRight().getData());
			return;
		}
		
		switch(operation){
			case "add": output.getRight().setData(inputA.getRight().getData() + inputB.getRight().getData() ); break;
			case "sub": output.getRight().setData(inputA.getRight().getData() - inputB.getRight().getData() ); break;
			case "and": output.getRight().setData(inputA.getRight().getData() & inputB.getRight().getData() ); break;
			case  "or": output.getRight().setData(inputA.getRight().getData() | inputB.getRight().getData() ); break;
			case "nor": output.getRight().setData( ~(inputA.getRight().getData() |  inputB.getRight().getData()) ); break;
			case "xor": output.getRight().setData(inputA.getRight().getData() ^ inputB.getRight().getData() ); break;
			case "mul": output.getRight().setData(inputA.getRight().getData() * inputB.getRight().getData() ); break;
			case "mulu": output.getRight().setData(inputA.getRight().getData() * inputB.getRight().getData() ); break;					// TODO - maybe just create big data containers to avoid overflow
			case "div": {
				if(inputB.getRight().getData() != 0) output.getRight().setData(inputA.getRight().getData() / inputB.getRight().getData() );
				break;
			}
			case "divu": {																				// TODO
				if(inputB.getRight().getData() != 0) output.getRight().setData(inputA.getRight().getData() / inputB.getRight().getData() );
				break;
			}
			case "bneq": { 
				output.getRight().setData(inputA.getRight().getData() - inputB.getRight().getData() );		// bneq is actually sub
				
				// if the output is NOT zero, we've got different numbers
				if(output.getRight().getData() != 0)
					zeroResult.getRight().setData(1);
				break;			
			} 
			case "sllv": output.getRight().setData(inputA.getRight().getData()  << inputB.getRight().getData() ); break;
			case "srlv": output.getRight().setData(inputA.getRight().getData() >>> inputB.getRight().getData() ); break;
			case "lui":{
				output.getRight().setData(
					inputA.getRight().getData() << (inputA.getRight().getBitSize() / 2)
				);
				break;
			}
			
			default: logger.log(Logger.Level.WARNING, "Unknown operation `" + operation + "`"); break;
		}
		
		//DONT FORGET TO SET ZERO-RESULT OUTPUT
		if( ! operation.equals("bneq")  &&  output.getRight().getData() == 0)
			zeroResult.getRight().setData(1);
		
		inputA.getRight().setData(0);
		inputB.getRight().setData(0);
	}

	@Override
	public Data getData(String selector) {
		return output.getRight().duplicate();
	}

	@Override
	public void setData(String selector, Data data) {
		switch(selector){
			case "inputA": inputA.getRight().setData(data.getData()); break;
			case "inputB": inputB.getRight().setData(data.getData()); break;
			
			default: logger.log(Logger.Level.WARNING, "Unknown input `" + selector + "`"); break;
		}
	}
}
