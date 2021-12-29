package sk.catheaven.model.instructions;

import sk.catheaven.core.instructions.ArgumentType;

import java.util.regex.Pattern;

/**
 * Serves the purpose of determining if provided argument is valid and correctly
 * formatted. Apart from that also offers the extraction of data from supplied argument.
 * @author catlord
 */
public abstract class ArgumentTypeImpl extends ArgumentType {
    protected static Pattern pattern;

    public static String getRegisterRegex() {
        return REGISTER_REGEX;
    }

    public static String getLabelRegex() {
        return LABEL_REGEX;
    }
}
