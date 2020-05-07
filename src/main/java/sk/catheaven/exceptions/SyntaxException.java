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
 *
 * @author catlord
 */
public class SyntaxException extends Exception {
	private List<Tuple<Integer, String>> errors;
	
	public SyntaxException(){
		errors = new ArrayList();
	}
	
	public SyntaxException(String error){
		super(error);
	}
	
	public void addError(int line, String error){
		if(errors == null) return;
		
		errors.add(new Tuple(line, error));
	}
	
	public void addAllErrors(List<Tuple<Integer, String>> errors){
		if(this.errors == null) return;
		
		errors.addAll(errors);
	}
	
	public List<Tuple<Integer, String>> getErrors(){
		return errors;
	}
}
