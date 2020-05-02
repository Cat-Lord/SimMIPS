/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author catlord
 */
public abstract class Component implements Datapathable {
	protected static String statusFormat = "%25s: %s\n";
	protected String label;
	
	public Component(String label){
		this.label = label;
	}
	
	public String getLabel(){
		return label;
	}
	
	/**
	 * Returns values of every input/output.
	 * @return 
	 */
	public abstract String getStatus();
}
