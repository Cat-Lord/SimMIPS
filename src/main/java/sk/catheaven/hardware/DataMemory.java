/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.logging.Logger;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Tuple;

/**
 * Data storage unit. InputA represents target address (to read from or write to)
 * and inputB represent actual data. Only one of the control signals (memRead or
 * memWrite) can be active at one time, but inactivity of those signals is allowed.
 * @author catlord
 */
public class DataMemory extends Component {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private final Data[] memory;
	private final Tuple<String, Data> memRead, memWrite;
	private final Tuple<String, Data> inputA, inputB;
	private final Data output;
	
	public DataMemory(String label, JSONObject json) {
		super(label, json);
		
		int bitSize = json.getInt("bitSize");
		
		JSONObject mr = json.getJSONObject("memRead");
		memRead = new Tuple<>(mr.getString("label"), new Data(mr.getInt("bitSize")));
		
		JSONObject mw = json.getJSONObject("memWrite");
		memWrite = new Tuple<>(mw.getString("label"), new Data(mw.getInt("bitSize")));
		
		inputA = new Tuple<>(json.getString("inputA"), new Data(bitSize));
		inputB = new Tuple<>(json.getString("inputB"), new Data(bitSize));
		output = new Data(bitSize);
		
		memory = new Data[json.getInt("capacity")];
		for(int i = 0; i < memory.length; i++)
			memory[i] = new Data(bitSize);
		
		logger.log(Level.INFO, "Created memory of " + memory.length + " memory blocks of size " + bitSize + "b");
	}

	/**
	 * Do only one at the time - either read or write to the memory. Checks for out-of-bounds request 
	 * to read/write and if detected, sets output to zero. Information about activity is logged.
	 */
	@Override
	public void execute() {
		if(memRead.getRight().getData() == 1  && memWrite.getRight().getData() == 1){
			logger.log(Level.SEVERE, "Inconsistent state, both control signals are active !");
			output.setData(0);
			return;
		}
		
		int index = Assembler.computeIndex(inputA.getRight());
		if(index < 0  ||  index >= memory.length){
			logger.log(Level.WARNING, "Index out of memory bounds. Calculated index {0} from input 0x{1} (memory limit is 0x{2}", new Object[]{index, inputA.getRight().getHex(), Assembler.computeAddress(memory.length).getHex()});
		}
		
		if(memRead.getRight().getData() == 1){
			output.setData(memory[index].getData());
			logger.log(Level.INFO, "Reading from memory address 0x{0}, got number 0x{1}", new Object[]{inputA.getRight().getHex(), memory[index].getHex()});
			notifySubs();
		}
		else if(memWrite.getRight().getData() == 1){
			memory[index].setData(inputB.getRight().getData());
			logger.log(Level.INFO, "Writing to memory address 0x{0} value of {1}", new Object[]{inputA.getRight().getHex(), inputB.getRight().getData()});
			notifySubs();
		}
		else
			logger.log(Level.INFO, "Memory inactive");
	}

	@Override
	public Data getOutput(String selector) {
		return output.duplicate();
	}

	@Override
	public boolean setInput(String selector, Data data) {
		if(selector.equals(inputA.getLeft())) inputA.getRight().setData(data.getData());
		else if(selector.equals(inputB.getLeft())) inputB.getRight().setData(data.getData());
		else if(selector.equals(memRead.getLeft())) memRead.getRight().setData(data.getData());
		else if(selector.equals(memWrite.getLeft())) memWrite.getRight().setData(data.getData());
		
		else{
			logger.log(Level.WARNING, "{0} --> Unknown request to set data for `{1}`", new Object[]{label, selector}); 
			return false;
		}
		return true;
	}
	
	public String getStatus(){
		String s = "";
		s = s.concat(String.format(statusFormat, new Object[]{inputA.getLeft(), inputA.getRight().getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{inputB.getLeft(), inputB.getRight().getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{memRead.getLeft(), memRead.getRight().getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{memWrite.getLeft(), memWrite.getRight().getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{"Output", output.getHex()}));
		return s;
	}

	@Override
	public Data getInput(String selector) {
		if(selector.equals(inputA.getLeft()))			return inputA.getRight().duplicate();
		else if(selector.equals(inputB.getLeft()))		return inputB.getRight().duplicate();
		else if(selector.equals(memRead.getLeft()))		return memRead.getRight().duplicate();
		else if(selector.equals(memWrite.getLeft()))	return memWrite.getRight().duplicate();
		return null;
	}
	
	public void clearMemory(){
		for(int i = 0; i < memory.length; i++)
			memory[i].setData(0);
	}

	@Override
	public void reset() {
		clearMemory();
		
		inputA.getRight().setData(0);
		inputB.getRight().setData(0);
		memRead.getRight().setData(0);
		memWrite.getRight().setData(0);
		output.setData(0);
	}
	
	public Data[] getMemory(){
		return memory;
	}
	
}
