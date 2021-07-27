package sk.catheaven.utils;

import sk.catheaven.model.Data;
import sk.catheaven.model.components.CPU;

import java.util.Locale;

/**
 * From a given data returns a proper string representation in a nice human readable format.
 */
public class DataFormatter {
    /**
     * Returns binary representation of data in nicer formatting style.
     * @return Binary representation of data
     */
    public static String getBinary(Data data){
        String dataString = Integer.toBinaryString(data.getData());
        StringBuilder output = new StringBuilder();
        int index = 1;
        
        // add necessary number of one's
        for(int i = 0; i < data.getBitSize() - dataString.length(); i++, index++){
            output.append("0");
            if((data.getBitSize() - index) % 4 == 0)
                output.append(" ");
        }
        
        // append zero's
        for(int i = 0; i < dataString.length(); i++, index++){
            if(dataString.charAt(i) == '0')						// avoids adding one for binary string "0"
                output.append("0");
            else
                output.append("1");
            
            if((data.getBitSize() - index) % 4 == 0  &&  i < (dataString.length() -1))
                output.append(" ");
        }
        
        return output.toString();
    }
    
    /**
     * Converts data to hex representation aligned to 8 places.
     * @return Hexadecimal representation of data.
     */
    public static String getHex(Data data){
        String dataString = Integer.toHexString(data.getData());
        String output = "";
        
        for(int i = 0; i < CPU.BIT_SIZE /4 - dataString.length(); i++)
            output += "0";
        output += dataString;
        
        return output.toUpperCase(Locale.ROOT);
    }
    
    /**
     * Converts data to octal representation aligned to 8 places.
     * @return Hexadecimal representation of data.
     */
    public static String getOct(Data data){
        String dataString = Integer.toOctalString(data.getData());
        String output = "";
        
        for(int i = 0; i < (CPU.BIT_SIZE /3 + 1) - dataString.length(); i++)
            output += "0";
        output += dataString;
        
        return output;
    }
}
