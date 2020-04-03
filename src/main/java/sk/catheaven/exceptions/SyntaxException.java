/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.exceptions;

/**
 *
 * @author catlord
 */
public class SyntaxException extends Exception {
	public SyntaxException(String error){
		super(error);
	}
}
