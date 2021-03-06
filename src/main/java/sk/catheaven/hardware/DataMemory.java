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
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Tuple;

/**
 * Data storage unit. InputA represents target address (to read from or write to)
 * and inputB represent actual data. Only one of the control signals (memRead or
 * memWrite) can be active at one time, but inactivity of those signals is allowed.
 * Memory is efficiently stored in a map in a address (number) and data (Data) combination.
 * When a request to an unset memory is made (which means memory at address is null),
 * zero is returned. Otherwise data duplicate at that memory address is returned.
 * Memory can be in one of three states: inactive, memory read or memory write.
 * State when memory read signal and memory write signal is active is forbidden.
 * @author catlord
 */
public class DataMemory extends Component {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private final Map<Integer, Data> memory;					// maps addresses represented as data to data values
	private final Data memoryBlock;							// to avoid overflows, this will be container, which will manage input/output from memory
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
	
		memory = new HashMap<>();
		memoryBlock = new Data(json.getInt("bitSize"));
		
		logger.log(Level.INFO, "Created memory blocks of size {0}b", new Object[]{bitSize});
	}

	/**
	 * Do only one at the time - either read or write to the memory. Checks for out-of-bounds request 
	 * to read/write and if detected, sets output to zero. Information about activity is logged.
	 * Read/write operation are performed on adjusted memory address references - since each block
	 * consists of fixed amount of bytes, shifts have to be calculated (memory is not byte-referenced).		// todo - check terminology
	 */
	@Override
	public void execute() {
		output.setData(0);

		if(memRead.getRight().getData() == 1  && memWrite.getRight().getData() == 1){
			logger.log(Level.SEVERE, "Inconsistent state, both control signals are active !");
			return;
		}
		
		int bitWidth = 2;		// maximal number of bits used to differentiate between a memory cell and a following cell
		
		Data address = inputA.getRight().duplicate();
		Data nextAddress = inputA.getRight().duplicate();
		
		Data dataShift = new Data(bitWidth);						 // contains the amount we need to shift data while having 4-byte alignment // todo -- - possible refactor, to allow not only 32 bit architecture
		dataShift.setData(address.getData());
		
		address.setData( ((address.getData() >>> bitWidth) << bitWidth) ); // remove last 2 bits // todo - possible refactor, to allow not only 32 bit architecture
		nextAddress.setData( address.getData() + (int) Math.pow(bitWidth,2));			 // todo - refactor

		System.out.println("InputA:     " + inputA.getRight().getHex());
		System.out.println("InputB:     " + inputB.getRight().getHex());
		System.out.println("Address:    " + address.getHex());
		System.out.println("Next addr:  " + nextAddress.getHex());
		System.out.println("Data shift: " + dataShift.getHex());
		
		if(memRead.getRight().getData() == 1){
			Data testBlock = memory.get(nextAddress.getData());		// first fetch data from following address
			
			output.setData(0);
			
			if(testBlock != null){
				// first read the following block and shift left (to make room for next number	
				memoryBlock.setData( (testBlock.getData() << ((int) Math.pow(bitWidth,2) - dataShift.getData())) );
				
				// and then get the next part of the result (could be zero)
				testBlock = memory.get(address.getData());
				if(testBlock != null)
					memoryBlock.setData( memoryBlock.getData() | (testBlock.getData() >>> dataShift.getData()) );
				
				output.setData(memoryBlock.getData());
			}
			logger.log(Level.INFO, "Reading from memory address 0x{0}, got number 0x{1}", new Object[]{address.getHex(), output.getHex()});

		}
		else if(memWrite.getRight().getData() == 1){
			// write first memory block
			memoryBlock.setData(inputB.getRight().getData() << dataShift.getData());			// first perform possible bit size adjustment of the input
			memory.put(address.getData(), memoryBlock.duplicate());

			// write next memory block (if any)
			if(dataShift.getData() != 0){
				memoryBlock.setData(inputB.getRight().getData() >>> ((int) Math.pow(bitWidth,2) - dataShift.getData()));			// first perform possible bit size adjustment of the input
				memory.put(nextAddress.getData(), memoryBlock.duplicate());
			}
			
			// reset memory block for next usage
			memoryBlock.setData(0);
			logger.log(Level.INFO, "Writing to memory address 0x{0} value of {1}", new Object[]{address.getHex(), inputB.getRight().getData()});
			notifySubs();
		}
		else{
			logger.log(Level.INFO, "Memory inactive");		
			return;											// dont notify subs
		}
		
		notifySubs();
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

	@Override
	public Data getInput(String selector) {
		if(selector.equals(inputA.getLeft()))			return inputA.getRight().duplicate();
		else if(selector.equals(inputB.getLeft()))		return inputB.getRight().duplicate();
		else if(selector.equals(memRead.getLeft()))		return memRead.getRight().duplicate();
		else if(selector.equals(memWrite.getLeft()))	return memWrite.getRight().duplicate();
		return null;
	}
	
	/**
	 * Removes every known value.
	 */
	public void clearMemory(){
		memory.clear();
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
	
	/**
	 * Returns memory block value by a set address. If such block doesn't exist,
	 * returns empty data.
	 * @param address Address of desired block.
	 * @return Data object with value of desired block.
	 */
	public Data getMemBlock(Data address){
		if(memory.get(address.getData()) == null)
			return memoryBlock.duplicate();		// memory block always has value of 0 and corresponds correctly to memory block bit size
		return memory.get(address.getData()).duplicate();
	}
	
	public Map<Integer, Data> getMemory(){
		return memory;
	}
	
		
	@Override
	public String getStatus(){
		String s = "";
		s = s.concat(String.format(statusFormat, new Object[]{inputA.getLeft(), inputA.getRight().getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{inputB.getLeft(), inputB.getRight().getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{memRead.getLeft(), memRead.getRight().getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{memWrite.getLeft(), memWrite.getRight().getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{"Output", output.getHex()}));
		return s;
	}
}
