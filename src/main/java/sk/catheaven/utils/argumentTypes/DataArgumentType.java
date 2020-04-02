/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils.argumentTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
	private Data base;
	private Data offset;
	
	public DataArgumentType(){
		super();
	}
	
	public void parse(String arg) throws Exception {
		pattern = Pattern.compile(DATA_REGEX);
		Matcher matcher = pattern.matcher(arg.toLowerCase());
		if( ! matcher.matches() )
			throw new Exception("Invalid Data Format");
		
		/*
		// TODO: check if this works
		String argParts[] = arg.split("(");
		int offInt  = Integer.parseInt(argParts[0]);											// get offset
		int baseInt = Integer.parseInt(argParts[1].substring(0, argParts[1].length()-1));		// cut the last ')' character
		
		offset.setData(offInt);
		base.setData(baseInt);*/
	}
	
	public Data getData(){
		return null;
	}
	
	public String toString(){
		return "Data";
	}
}
