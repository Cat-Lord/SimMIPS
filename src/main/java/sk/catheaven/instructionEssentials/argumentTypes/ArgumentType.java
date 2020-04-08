/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionEssentials.argumentTypes;

import java.lang.System.Logger;
import java.util.regex.Pattern;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.instructionEssentials.Data;

/**
 * Defines methods needed to parse user input. All classes
 * derived from this class will define their own ways of parsing
 * input.
 * All arguments will be later translated into numbers, since instruction
 * is a number.
 * @author catlord
 */
public abstract class ArgumentType {
	protected static Logger logger;
	protected static Pattern pattern;
	protected final static char REG_SYMBOL = 'r';		// defines character that represent symbol for register
	
	protected final static String LABEL_REGEX = "[a-zA-Z]\\w*";					// regex for filltering correct labels
	protected final static String REG_REGEX   = REG_SYMBOL + "\\d+";				// regex for filltering registers
	protected final static String DATA_REGEX  = "\\d+\\((" + REG_REGEX + ")\\)";	// regex for filltering data formats
	
	protected ArgumentType(){
		
	}
	
	public void parse(String arg) throws SyntaxException {
		// parse given argument
		throw new SyntaxException("Trying to parse within abstract ArgumentType class !");
	}
	
	public int getData(String argument){
		return 0;
	}
}
