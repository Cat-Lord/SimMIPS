package sk.catheaven.model.instructions;

import sk.catheaven.core.Data;
import sk.catheaven.service.Assembler;

import java.util.regex.Pattern;

/**
 * Serves the purpose of determining if provided argument is valid and correctly
 * formatted. Apart from that also offers the extraction of data from supplied argument.
 * @author catlord
 */
public abstract class ArgumentType {
    protected static Pattern pattern;
    
    protected final static String LABEL_REGEX = "[a-zA-Z]\\w*";					            // regex for filtering correct labels
    protected final static String REGISTER_REGEX   = Assembler.REGISTER_SYMBOL + "\\d+";	// regex for filtering registers
    protected final static String DATA_REGEX  = "\\d+\\((" + REGISTER_REGEX + ")\\)";	    // regex for filtering data formats
    
    public abstract boolean isValidArgument(String argument);
    public abstract Data getData(String argument);

    public static String getRegisterRegex() {
        return REGISTER_REGEX;
    }

    public static String getLabelRegex() {
        return LABEL_REGEX;
    }
}
