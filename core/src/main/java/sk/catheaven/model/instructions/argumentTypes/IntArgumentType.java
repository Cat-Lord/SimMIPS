package sk.catheaven.model.instructions.argumentTypes;

import sk.catheaven.core.Data;
import sk.catheaven.model.DataImpl;
import sk.catheaven.model.instructions.ArgumentTypeImpl;

/**
 * Represent a number as argument type. Number is an integer.
 * @author catlord
 */
public class IntArgumentType extends ArgumentTypeImpl {
    private final Data integer;
    
    public IntArgumentType() {
        integer = new DataImpl(16);				// maximal amount of bits per int number // todo - why ?
    }
    
    @Override
    public boolean isValidArgument(String argument) {
        try {
            argument = argument.trim();
            integer.setData(Integer.parseInt(argument));
        } catch(NumberFormatException e){
            return false;
        }
        return true;
    }
    
    @Override
    public Data getData(String argument) {
        if (isValidArgument(argument))
            return integer;
        return null;
    }
    
    
}
