/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.instructions.argumentTypes;

import sk.catheaven.model.Data;
import sk.catheaven.model.instructions.ArgumentType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represent label argument. Label can be almost anything, format is as follows:
 *  Any ASCII character first, can be followed by any number of characters and numbers.
 * Valid characters are _0123456789a-zA-Z
 * @author catlord
 */
public class LabelArgumentType extends ArgumentType {
    public LabelArgumentType() {
        pattern = Pattern.compile(LABEL_REGEX);
    }
    
    /**
     * Parses argument to detect possible label malformation.
     * @param argument Represent instruction argument
     */
    @Override
    public boolean isValidArgument(String argument) {
        return pattern.matcher(argument).matches();
    }
    
    @Override
    public Data getData(String argument) {
        return null;
    }
}
