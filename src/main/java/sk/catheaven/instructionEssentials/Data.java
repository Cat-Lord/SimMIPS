/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionEssentials;

/**
 *
 * @author catlord
 */
public class Data {
    private final int MAX_BIT_SIZE = 32;
    private int mask;
    private int data;
    private int bitSize;
    
    public Data(){
        this(32);
    }
    
    public Data(int bitSize){
        // need to be careful when checking constraints
        if(bitSize > MAX_BIT_SIZE)
            this.bitSize = MAX_BIT_SIZE;            // assing highest possible value
        else if(bitSize <= 0)
            this.bitSize = 1;                       // lowest possible value of one bit
        else
            this.bitSize = bitSize;                 // correct value
        
        createMask();
    }
   
    /**
     * According to bit-size of this objects data creates a mask.
     */ 
    private void createMask(){
        mask = (-1 >>> (MAX_BIT_SIZE - bitSize));
    }
    
    /**
     * Sets data according to the input parameter.
     * @param data input data to set 
     */
    public void setData(int data){
        this.data = data;
    }
    
    /**
     * Returns data appropriately adjusted by the mask.
     * @return data
     */
    public int getData(){
        return (data & mask);
    }
    
    /**
     * Returns mask. Used for debugging purposes.
     * @return mask
     */
    public int getMask(){
        return mask;
    }

    
    /**
     * Returns binary representation of data in nicer formatting style.
     * @return Binary representation of data
     */
    public String getBinary(){
        String dataString = Integer.toBinaryString(getData());
        String output = "";
        int index = 1;
		
        // add necessary number of one's
        for(int i = 0; i < bitSize - dataString.length(); i++, index++){
			output += "0";
            if((bitSize - index) % 4 == 0)
                output += " ";
        }
        
        // append zero's
        for(int i = 0; i < dataString.length(); i++, index++){
			if(dataString.charAt(i) == '0')						// avoids adding one for binary string "0"
				output += "0";
			else
				output += "1";
            
            if((bitSize - index) % 4 == 0  &&  i < (dataString.length() -1))
                output += " ";
        }
        
        return output;
    }
}
