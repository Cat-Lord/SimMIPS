/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.instructions.argumentTypes;

import sk.catheaven.model.Data;
import sk.catheaven.model.instructions.ArgumentType;

public class IntArgumentType extends ArgumentType {
    private final Data integer;
    
    public IntArgumentType() {
        integer = new Data(16);				// maximal amount of bits per int number // todo - why ?
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
