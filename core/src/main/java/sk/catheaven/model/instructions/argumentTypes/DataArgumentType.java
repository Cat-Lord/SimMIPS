/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.instructions.argumentTypes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.Data;
import sk.catheaven.model.instructions.ArgumentType;

import java.util.Locale;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Data argument has syntax of 'offset(base)'. Offset is 16-bit
 * number and base is register. Final value (address) is calculated
 * by adding these values together.
 * Since this syntax varies from others significantly, it requires
 * separate treatment when using.
 * @author catlord
 */
public class DataArgumentType extends ArgumentType {
    private static Logger log = LogManager.getLogger();
    private static RegArgumentType regArg;		// inside of the braces there is register, so we need to know, how to get value from register. This way we just call the proper class to do it
    private static Data offset;
    
    /**
     * Describes which part of argument are we interested in. Requested argument part then gets stored.
     * In case the part is not known, we use a default value of unknown.
     */
    public enum Part {
        UNKNOWN,
        OFFSET,
        BASE;
    
        private String providedPart;
    
        public Part of(String argumentPart) {
            if (argumentPart.toUpperCase(Locale.ROOT).contains(Part.BASE.toString()))
                return create(Part.BASE, argumentPart);
        
            if (argumentPart.toUpperCase(Locale.ROOT).contains(Part.OFFSET.toString()))
                return create(Part.OFFSET, argumentPart);
        
            return create(Part.UNKNOWN, argumentPart);
        }
    
        private Part create(Part part, String origin) {
            part.setProvidedPart(origin);
            return part;
        }
    
        public String getProvidedPart() {
            return providedPart;
        }
    
        public void setProvidedPart(String providedPart) {
            this.providedPart = providedPart;
        }
    }
    
    public DataArgumentType(){
        regArg  = new RegArgumentType();
        offset = new Data(16);      // todo - why is this hard-coded
        pattern = Pattern.compile(DATA_REGEX);
    }
    
    @Override
    public boolean isValidArgument(String arg) {
        pattern = Pattern.compile(DATA_REGEX);
        return  pattern.matcher(arg.toLowerCase()).matches();
    }
    
    /**
     * From argument, which is data-valid cuts of part specified by <b>part</b>.
     * @param validArgument User-written argument, for example '0030(r6)' from "sw r1, 0030(r6)"
     * @param part Specifies part of data argument. Either base or offset.
     * @return
     */
    public Data getPart(String validArgument, Part part){
        switch (part) {
            case BASE -> {
                // cut off the register specified in brackets and call for
                String reg = validArgument.substring(
                        validArgument.indexOf("(") + 1,
                        validArgument.indexOf(")")
                );
                return regArg.getData(reg);
            }
            case OFFSET -> {
                offset.setData(Integer.parseInt(validArgument.substring(0, validArgument.indexOf("("))));
                return offset;
            }
            default -> log.error("Unknown argument part ! Provided: {}", part.getProvidedPart());
        }
        
        return new Data();
    }
}
