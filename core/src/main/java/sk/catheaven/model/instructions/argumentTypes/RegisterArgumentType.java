/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.instructions.argumentTypes;

import sk.catheaven.model.Data;
import sk.catheaven.model.instructions.ArgumentType;
import sk.catheaven.service.Assembler;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Represents register argument type. A register begins with letter R (lower or upper case)
 * and an index number. Example: r4, R124, r14, etc.
 * Index limitation depends on the size of register bank of CPU, but allows index overflows
 * (Indexes are stored in Data objects and are properly cut if needed).
 * @author catlord
 */
public class RegisterArgumentType extends ArgumentType {
    private final Data registerIndex;
    
    public RegisterArgumentType(){
        registerIndex = new Data(5); // todo - again, why this hard-coded value ?
    }
    
    @Override
    public boolean isValidArgument(String argument) {
        pattern = Pattern.compile(REGISTER_REGEX);
        return pattern.matcher(argument.toLowerCase(Locale.ROOT)).matches();
    }
    
    /**
     * From valid argument extracts number and returns it as integer value.
     * @param argument String representation of register, for example "r3" or "R72".
     * @return register index
     */
    @Override
    public Data getData(String argument){
        argument = argument.trim().toLowerCase();
        String regIndexString = argument.substring(
                                    argument.indexOf(Assembler.REGISTER_SYMBOL) + 1,
                                    argument.length()
                                );
        registerIndex.setData(Integer.parseInt(regIndexString));
        return registerIndex;
        
    }
}
