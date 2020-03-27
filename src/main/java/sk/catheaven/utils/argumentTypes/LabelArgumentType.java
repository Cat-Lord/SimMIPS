/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils.argumentTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sk.catheaven.instructionEssentials.Data;

/**
 * Represent label argument. Label can be almost anything, format is as follows:
 * any ASCII character first, can be followed by any number of characters and numbers.
 * Valid characters are _0123456789a-zA-Z
 * @author catlord
 */
public class LabelArgumentType extends ArgumentType {
	public LabelArgumentType(){
		super();
	}
	
	/**
	 * Parses argument to detect possible label malformation.
	 * @param arg Represent instruction argument
	 * @throws Exception
	 */
	@Override
	public void parse(String arg) throws Exception {
		pattern = Pattern.compile(LABEL_REGEX);
		Matcher matcher = pattern.matcher(arg);
		if( ! matcher.matches())
			throw new Exception("Invalid Label Syntax");
	}
	
	public Data getData(){
		return null;
	}
	
	public String toString(){
		return "Label";
	}
}
