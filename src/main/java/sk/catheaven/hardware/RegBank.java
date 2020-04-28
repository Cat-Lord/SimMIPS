/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.lang.System.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Tuple;

/**
 * Reg bank, which provides temporary storage for CPU calculations. Has multiple inputs and 
 * two outputs, as well as regWrite signal.
 * @author catlord
 */
public class RegBank extends Component {
	private static Logger logger;
	
	private final Data[] registers;		// storage
	
	private final Tuple<String, Data> inputA, inputB;						// input data
	private final Tuple<String, Data> destReg, destRegData, regWrite;		// data for writing into register bank
	private final Tuple<String, Data> outputA, outputB;							// two outputs
	
	public RegBank(String label, JSONObject json) throws JSONException {
		super(label);
		
		RegBank.logger = System.getLogger(this.getClass().getName());
		
		int bitSize = json.getInt("dataBitSize");										// bitSize of data in this component
		registers = loadRegs(json.getInt("regCount"), bitSize);
		destRegData = new Tuple<>(json.getString("destRegData"), new Data(bitSize));	// and of course bit size of input data must be the same 'bitsize'
		outputA = new Tuple<>(json.getString("outputA"), new Data(bitSize));
		outputB = new Tuple<>(json.getString("outputB"), new Data(bitSize));
		
		// get labels of all inputs
		JSONObject iA = json.getJSONObject("inputA");
		inputA = new Tuple<>(iA.getString("label"), new Data(iA.getInt("bitSize")));
		
		JSONObject iB = json.getJSONObject("inputB");
		inputB = new Tuple<>(iB.getString("label"), new Data(iB.getInt("bitSize")));
		
		JSONObject dr = json.getJSONObject("destReg");
		destReg = new Tuple<>(dr.getString("label"), new Data(dr.getInt("bitSize")));
		
		JSONObject rwSignal = json.getJSONObject("selector");
		regWrite = new Tuple<>(rwSignal.getString("label"), new Data(rwSignal.getInt("bitSize")));
	}

	@Override
	public void execute() {
		// if write is enabled, wrtie to destination (but only if its not the 0th register, which is fixed
		if(regWrite.getRight().getData() == 1  &&  destReg.getRight().getData() != 0)
			registers[destReg.getRight().getData()].setData(
					destRegData.getRight().getData()
			);
		
		// pass register values pointed to by input to the output
		outputA.getRight().setData(registers[inputA.getRight().getData()].getData());
		outputB.getRight().setData(registers[inputB.getRight().getData()].getData());
	}

	/**
	 * Returns duplicate of data requested by string selector. If the selector points to
	 * unknown data, the resulting output is 0.
	 * @param selector String describing label of requested data.
	 * @return 
	 */
	@Override
	public Data getOutput(String selector) {
		if(selector.equals(outputA.getLeft())) return outputA.getRight().duplicate();
		if(selector.equals(outputB.getLeft())) return outputB.getRight().duplicate();
		
		logger.log(Logger.Level.WARNING, "Trying to get unknown output `" + selector + "`, returning 0");
		
		return new Data(inputA.getRight().getBitSize());
	}

	/**
	 * Set data labeled with selector string.
	 * @param selector Data to overwrite.
	 * @param data Provided data.
	 * @return True if value of input changed.
	 */
	@Override
	public boolean setInput(String selector, Data data) {
		if     (selector.equals(regWrite.getLeft())) regWrite.getRight().setData(data.getData());
		else if(selector.equals(inputA.getLeft())) inputA.getRight().setData(data.getData());
		else if(selector.equals(inputB.getLeft())) inputB.getRight().setData(data.getData());
		else if(selector.equals(destReg.getLeft())) destReg.getRight().setData(data.getData());
		else if(selector.equals(destRegData.getLeft())) destRegData.getRight().setData(data.getData());
		else{
			logger.log(System.Logger.Level.WARNING, label + " --> Unknown request to set data for `" + selector + "`"); 
			return false;
		}
		return true;
	}
	
	private Data[] loadRegs(int count, int bitSize) {
		Data[] regArray = new Data[count];
		for(int i = 0; i < count; i++)
			regArray[i] = new Data(bitSize);
		
		logger.log(Logger.Level.DEBUG, "Created array of " + count + " data elements with bit size of " + bitSize + "b");
		
		return regArray;
	}
}
