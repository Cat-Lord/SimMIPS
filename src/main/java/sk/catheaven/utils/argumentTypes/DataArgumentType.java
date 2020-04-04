/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils.argumentTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.instructionEssentials.Data;
import static sk.catheaven.utils.argumentTypes.ArgumentType.pattern;

/**
 * Data argument has syntax of offset(base). Offset is 16-bit
 * number and base is register. Final value (address) is calculated
 * by adding these values together.
 * Since this syntax varies from others significantly, it requires
 * separate treatment when using.
 * @author catlord
 */
public class DataArgumentType extends ArgumentType {
	private static RegArgumentType regArg;		// inside of the braces there is register, so we need to know, how to get value from register. This way we just call the proper class to do it
	private static Data offset;
	
	public DataArgumentType(){
		super();
		DataArgumentType.logger = System.getLogger(this.getClass().getName());
		regArg  = new RegArgumentType();
		offset = new Data(16);
	}
	
	@Override
	public void parse(String arg) throws SyntaxException {
		pattern = Pattern.compile(DATA_REGEX);
		
		Matcher matcher = pattern.matcher(arg.toLowerCase());
		if( ! matcher.matches() )
			throw new SyntaxException("Invalid Data Format");
		
	}
	
	/**
	 * From argument, which is data-valid cuts of part specified by <b>part</b>.
	 * @param validArgument User-written argument, for example "sw r1, 0030(r6)"
	 * @param part Specifies part of data argument. Either base or offset. 
	 * @return 
	 */
	public int getPart(String validArgument, String part){
		switch(part){
			case ".offset":
				offset.setData(Integer.parseInt(validArgument.substring(0, validArgument.indexOf("("))));
				return offset.getData();
				
			case ".base":
				// cut off the register specified in brackets and call for 
				String reg = validArgument.substring(validArgument.indexOf("(") + 1,
													 validArgument.indexOf(")"));
				
				return regArg.getData(reg);
				
			default: 
				logger.log(System.Logger.Level.WARNING, "getPart(): Returning default value, part '" + part + "' was not recognized" );
				break;
		}
		
		return 0;
	}
	
	public String toString(){
		return "Data";
	}
}
