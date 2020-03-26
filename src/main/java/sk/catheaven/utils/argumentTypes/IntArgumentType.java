/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils.argumentTypes;

import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class IntArgumentType extends ArgumentType {
	public IntArgumentType(){
		super();
		data = new Data(16);				// maximal amount of bits per int number
	}
	
	public void parse(String arg){
		try{
			data.setData(Integer.parseInt(arg)); 
		}catch(NumberFormatException e){
			throw new NumberFormatException();
		}
	}
	
	public Data getData(){
		return data;
	}
}
