/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.exceptions;

import java.util.ArrayList;
import java.util.List;
import sk.catheaven.utils.Tuple;

/**
 * Syntax Exception used for code assembling. Allows to define multiple errors
 * and can be thrown once instead of multiple exception throws.
 * @author catlord
 */
public class SyntaxException extends Exception {
	private List<Tuple<Integer, String>> errors;		// holds error message associated with a line if was found on
	
	/**
	 * Empty constructor defines the list of (possible) errors.
	 */
	public SyntaxException(){
		errors = new ArrayList();
	}
	
	/**
	 * Adding a parameter to constructor will create basic exception
	 * with a specified message.
	 * @param error Error message.
	 */
	public SyntaxException(String error){
		super(error);
	}
	
	/**
	 * Adds error message to list
	 * @param line Line number containing error.
	 * @param error Error message.
	 */
	public void addError(int line, String error){
		if(errors == null) return;
		
		errors.add(new Tuple(line, error));
	}
	
	/**
	 * Concatenates error list to already managed error list (if there is any).
	 * @param errors List of exceptions in the format this class defines.
	 */
	public void addAllErrors(List<Tuple<Integer, String>> errors){
		if(this.errors == null) return;
		
		errors.addAll(errors);
	}
	
	/**
	 * Returns the list of all exceptions (tuple of line and error message).
	 * @return 
	 */
	public List<Tuple<Integer, String>> getErrors(){
		return errors;
	}
}
