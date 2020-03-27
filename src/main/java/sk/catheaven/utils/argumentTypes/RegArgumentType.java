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
 *
 * @author catlord
 */
public class RegArgumentType extends ArgumentType {
	private Data data;	

	public RegArgumentType(){
		super();
	}
	
	public void parse(String arg) throws Exception {
		pattern = Pattern.compile(REG_REGEX);
		Matcher matcher = pattern.matcher(arg);
		if( ! matcher.matches())
			throw new Exception("Invalid Register Specified");
	}
	
	public Data getData(){
		return null;
	}
	
	public String toString(){
		return "Reg";
	}
}
