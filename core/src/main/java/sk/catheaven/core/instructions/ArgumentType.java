package sk.catheaven.core.instructions;

import sk.catheaven.core.Data;

public abstract class ArgumentType {
    protected final static String LABEL_REGEX    = "[a-zA-Z]\\w*";					            // regex for filtering correct labels
    protected final static String REGISTER_REGEX = "r\\d+";                                 	// regex for filtering registers
    protected final static String DATA_REGEX     = "\\d+\\((" + REGISTER_REGEX + ")\\)";	    // regex for filtering data formats

    public abstract boolean isValidArgument(String argument);
    public abstract Data getData(String argument);

    public static String getRegexFor(String type) {
        return
            switch (type.toLowerCase()) {
                case "label" -> LABEL_REGEX;
                case "data" -> DATA_REGEX;
                case "register" -> REGISTER_REGEX;
                default -> "";
            };
    }
}
