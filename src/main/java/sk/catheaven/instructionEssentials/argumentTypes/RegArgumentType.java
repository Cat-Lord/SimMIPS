/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionEssentials.argumentTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.instructionEssentials.Data;
import static sk.catheaven.instructionEssentials.argumentTypes.ArgumentType.pattern;

/**
 * Represents register argument type. A register begins with letter R (lower or upper case)
 * and an index number. Example: r4, R124, r14, etc.
 * Index limitation depends on the size of register bank of CPU, but allows index overflows
 * (Indexes are stored in Data objects and are properly cut if needed).
 * @author catlord
 */
public class RegArgumentType extends ArgumentType {
	private final Data regIndex;	

	public RegArgumentType(){
		super();
		regIndex = new Data(5);
	}
	
	@Override
	public void parse(String arg) throws SyntaxException {
		pattern = Pattern.compile(REG_REGEX);
		Matcher matcher = pattern.matcher(arg.toLowerCase());
		if( ! matcher.matches() )
			throw new SyntaxException("Invalid Register `" + arg + "` specified");
	}
	
	/**
	 * From valid argument extracts number and returns it as integer value.
	 * @param validArgument String representation of register, for example "r3" or "R72".
	 * @return 
	 */
	@Override
	public int getData(String validArgument){
		validArgument = validArgument.trim().toLowerCase();
		String regIndexString = validArgument.substring(validArgument.indexOf(REG_SYMBOL)+1, validArgument.length());
		regIndex.setData(Integer.parseInt(regIndexString));
		return regIndex.getData();
		
	}
	
	@Override
	public String toString(){
		return "Reg";
	}
}
