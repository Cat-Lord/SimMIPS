/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils.argumentTypes;

import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class IntArgumentType extends ArgumentType {
	private final Data integer;
	
	public IntArgumentType(){
		super();
		integer = new Data(16);				// maximal amount of bits per int number
	}
	
	@Override
	public void parse(String arg) throws SyntaxException {
		try{
			integer.setData(Integer.parseInt(arg)); 
		}catch(NumberFormatException e){
			throw new SyntaxException("Illegal Number format of '" + arg + "'");
		}
	}
	
	@Override
	public int getData(String validArgument) {
		validArgument = validArgument.trim();
		
		try{
			parse(validArgument);
		} catch(SyntaxException e) {}
		
		return integer.getData();
	}
	
	@Override
	public String toString(){
		return "Int";
	}
}
