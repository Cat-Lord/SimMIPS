package sk.catheaven.utils;

import sk.catheaven.core.Data;

import java.util.Locale;

/**
 * From a given data returns a proper string representation in a nice human readable format.
 */
public class DataFormatter {
    
    private static final int spacingFrequency = 4;
    private static final int hexDigitLimit = 8;
    private static final int octDigitLimit = 10;
    
    /**
     * Returns binary representation of data in nicer formatting style.
     * @return Binary representation of data
     */
    public static String getBinary(Data data){
        return getBinary(data.getData(), data.getBitSize());
    }
    
    public static String getBinary(Data data, int bitSize) {
        return getBinary(data.getData(), bitSize);
    }
    
    /**
     * String representation of number in binary. Uses spacing to better visualize the number.
     * @param num The number to convert to binary string
     * @param maxNumberOfDigits Number of digits of number we want to see (not including white spaces).
     * @return Binary number as string with correct formatting.
     */
    public static String getBinary(long num, int maxNumberOfDigits) {
        String numberAsString = Long.toBinaryString(num);
        StringBuilder output = new StringBuilder(numberAsString);
        output.reverse();
        appendZero(output, Math.max(spacingFrequency, maxNumberOfDigits));
        addWhiteSpaces(output);
        
        StringBuilder result = new StringBuilder(output.substring(0, getLengthIncludingSpaces(maxNumberOfDigits)));
        result.reverse();
        return result.toString();
    }
    
    /**
     * Converts data to hex representation aligned to 8 places.
     * @return Hexadecimal representation of data.
     */
    public static String getHex(Data data){
        return getHex(data.getData(), hexDigitLimit);
    }
    
    public static String getHex(Data data, int bitSize) {
        return getHex(data.getData(), bitSize);
    }
    
    public static String getHex(long num, int maxNumberOfDigits) {
        String dataString = Long.toHexString(num);
        StringBuilder outputFormatter = new StringBuilder(dataString);
        
        outputFormatter.reverse();
        appendZero(outputFormatter, maxNumberOfDigits);
        StringBuilder outputBuilder = new StringBuilder(outputFormatter.substring(0, maxNumberOfDigits));
        outputBuilder.reverse();
        
        return outputBuilder.toString().toUpperCase(Locale.ROOT);
    }
    
    /**
     * Converts data to octal representation aligned to 8 places.
     * @return Hexadecimal representation of data.
     */
    public static String getOct(Data data){
        return getOct(data.getData(), octDigitLimit);
    }
    
    public static String getOct(Data data, int bitSize) {
        return getOct(data.getData(), bitSize);
    }
    
    public static String getOct(long num, int maxNumberOfDigits) {
        String dataString = Long.toOctalString(num);
        StringBuilder outputFormatter = new StringBuilder(dataString);
    
        outputFormatter.reverse();
        appendZero(outputFormatter, maxNumberOfDigits);
        StringBuilder outputBuilder = new StringBuilder(outputFormatter.substring(0, maxNumberOfDigits));
        outputBuilder.reverse();
    
        return outputBuilder.toString().toUpperCase(Locale.ROOT);
    }
    
    /**
     * Append character zero up to maxNumberOfDigits.
     * @param output Result to append to.
     * @param maxNumberOfDigits Number of bits we want to display.
     */
    private static void appendZero(StringBuilder output, int maxNumberOfDigits) {
        int index = output.length();
    
        while (index < maxNumberOfDigits) {
            output.append("0");
            index++;
        }
    }
    
    private static void addWhiteSpaces(StringBuilder output) {
        int index = spacingFrequency;
        while (index < output.length()) {
            output.insert(index, " ");
            index += spacingFrequency + 1;
        }
    }
    
    /**
     * Calculates number of characters a string including white spaces would have. Uses <code>spacing frequency</code>
     * to calculate the result. Always returns at least bitSize.
     * @param bitSize number of bits of initial number
     * @return length of number as string with white spaces
     */
    private static int getLengthIncludingSpaces(int bitSize) {
        int limit = bitSize / spacingFrequency;
        if (limit % spacingFrequency == 0)
            limit -= 1;
        return Math.max(bitSize, bitSize + limit);
    }
}
