package sk.catheaven.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.core.Data;
import sk.catheaven.core.Tuple;
import sk.catheaven.model.cpu.components.CPUBase;

/**
 * Basic numbering object in the application. Has a mask, which prohibits overflows. Provides 
 * handy methods to format output into nice hex, octal or binary format. Input data can be 
 * any number, output data is always cut by logical AND operation with the mask. 
 * Mask is created on data initialization.
 * @author catlord
 */
public class DataImpl implements Data {
    private int mask = 0;
    private int data = 0;
    private int bitSize;
    private Tuple<Integer, Integer> range;
	
    private static Logger log = LogManager.getLogger();
 
    public DataImpl() { this(CPUBase.BIT_SIZE); }
	public DataImpl(int bitSize) { setBitSize(bitSize); }
 
	@Override
	public void setBitSize(int bitSize) {
		if (bitSize > CPUBase.BIT_SIZE)
			this.bitSize = CPUBase.BIT_SIZE;            // assign highest possible value
		else if (bitSize <= 0)
			this.bitSize = 1;                       // lowest possible value of one bit
		else
			this.bitSize = bitSize;                 // correct value
		
		createMask();
	}
	
	/**
	 * According to bit-size of this object data creates a mask.
	 */
	private void createMask() {
		this.mask = (-1 >>> (CPUBase.BIT_SIZE - bitSize));
	}
	
	@Override
	public Tuple<Integer, Integer> getRange() {
		return range;
	}
	
	// integer values get loaded by jackson as strings
	@Override
	public void setRange(Tuple<?, ?> range) {
		this.range = (Tuple<Integer, Integer>) range;
	}
	    
    /**
     * Sets data according to the input parameter. If a range is specified, data will get truncated.
	 * Example:
	 * 	range: 4-5 (shift first 4 to the left and then 5 to the right)
	 * 	data:   '1101 0101 0000 1111'
	 * 	result: 'xxxx 0101 0000 111x' ('x' represents 0, used for better visually differentiation)
	 *
	 * 	We need to be careful though. If our number has 10 bits, but we store it inside an int, which
	 * 	has 32 bits, then shift to the right could 'bring' more unnecessary numbers into the action.
	 * 	Explanation:
	 * 		bit size: 12 bits
	 * 		our int data storage in binary: ... 1111 0101 0101 0101 (shortened version of 32 bit number)
	 * 		range: 0-1		(so we want to cut only the last '1')
	 *
	 * 		BUT shifting right (data >>> 1) would look like this:
	 * 			data: 	1111 0101 0101 0101
	 * 		expected:		 0010 1010 1010
	 * actual result:	     1010 1010 1010x		(shift >>> 1)
	 *
	 * THEREFORE, we need to carefully erase the data with the mask.
     * @param data input data to set
     */
    @Override
	public void setData(int data){
        this.data = data;
	
		if (range != null) {
			this.data = (this.data << range.getLeft());
			this.data &= this.mask;						// erase the shifted part
			this.data = this.data >>> range.getRight();
		}
    }
    @Override
	public void setData(Data data) { setData(data.getData());}
    /**
     * Returns data appropriately adjusted by the mask.
     * @return Integer representation of data.
     */
    @Override
	public int getData(){
		return data & mask;
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
    @Override
	public int getMask(){
        return mask;
    }
	
	 /**
     * Returns bitSize. Used for debugging purposes.
     * @return mask
     */
	@Override
	public int getBitSize(){
		return bitSize;
	}
	
	public Data newInstance() {
		Data copy = new DataImpl(getBitSize());
		if (this.range != null)
			copy.setRange(this.range);
		copy.setData(this.getDataUnmasked());
		
		return copy;
	}
}
