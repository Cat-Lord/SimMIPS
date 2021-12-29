package sk.catheaven.model.instructions.argumentTypes;

import sk.catheaven.core.Data;
import sk.catheaven.model.instructions.ArgumentTypeImpl;

import java.util.regex.Pattern;

/**
 * Represent label argument. Label can be almost anything, format is as follows:
 *  Any ASCII character first, can be followed by any number of characters and numbers.
 * Valid characters are _0123456789a-zA-Z
 * @author catlord
 */
public class LabelArgumentType extends ArgumentTypeImpl {
    
    /**
     * Parses argument to detect possible label malformation.
     * @param argument Represent instruction argument
     */
    @Override
    public boolean isValidArgument(String argument) {
        return isValidLabel(argument);
    }
    
    public static boolean isValidLabel(String label) {
        pattern = Pattern.compile(LABEL_REGEX);
        return pattern.matcher(label).matches();
    }
    
    @Override
    public Data getData(String argument) {
        return null;
    }
}
