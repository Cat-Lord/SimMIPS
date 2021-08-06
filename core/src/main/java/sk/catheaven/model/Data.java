/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.cpu.components.CPU;

/**
 * Basic numbering object in the application. Has a mask, which prohibits overflows. Provides 
 * handy methods to format output into nice hex, octal or binary format. Input data can be 
 * any number, output data is always cut by logical AND operation with the mask. 
 * Mask is created on data initialization.
 * @author catlord
 */
public class Data {
    private int mask = 0;
    private int data = 0;
    private int bitSize;
    private Tuple<Integer, Integer> range;
	
    private static Logger log = LogManager.getLogger();
 
    public Data() { this(CPU.BIT_SIZE); }
	public Data(int bitSize) { setBitSize(bitSize); }
 
	public void setBitSize(int bitSize) {
		if (bitSize > CPU.getBitSize())
			this.bitSize = CPU.getBitSize();            // assign highest possible value
		else if (bitSize <= 0)
			this.bitSize = 1;                       // lowest possible value of one bit
		else
			this.bitSize = bitSize;                 // correct value
		
		createMask();
	}
	
	/**
	 * According to bit-size of this objects data creates a mask.
	 */
	public void createMask() {
		this.mask = (-1 >>> (CPU.getBitSize() - bitSize));
	}
	
	public Tuple<Integer, Integer> getRange() {
		return range;
	}
	
	public void setRange(Tuple<Integer, Integer> range) {
		this.range = range;
	}
	    
    /**
     * Sets data according to the input parameter.
     * @param data input data to set 
     */
    public void setData(int data){
        this.data = data;
    }
    public void setData(Data data) { setData(data.getData());}
    /**
     * Returns data appropriately adjusted by the mask. If range is set,
	 * data will get truncated accordingly.
     * @return Integer representation of data.
     */
    public int getData(){
		int returnData = (data & mask);
	
		/*
		 * If a range is specified, data will get truncated. Example:
		 * range: 4-5 (shift first 4 to the left and then 5 to the right)
		 * data:   '1101 0101 0000 1111'
		 * result: 'xxxx 0101 0000 111x' ('x' represents 0, used for better visually differentiation)
		 */
    	if (range != null)
			returnData = (returnData << range.getLeft()) >>> range.getRight();
    	
        return returnData;
    }
    
	/**
	 * Returns data without mask adjustment.
	 * @return data
	 */
	public int getDataUnmasked(){
		return data;
	}
	
    /**
     * Returns mask. Used for debugging purposes.
     * @return mask
     */
    public int getMask(){
        return mask;
    }
	
	 /**
     * Returns bitSize. Used for debugging purposes.
     * @return mask
     */
	public int getBitSize(){
		return bitSize;
	}
	
	public Data newInstance() {
		return new Data(getBitSize());
	}
}
