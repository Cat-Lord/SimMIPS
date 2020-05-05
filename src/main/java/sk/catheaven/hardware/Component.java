/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Tuple;

/**
 *
 * @author catlord
 */
public abstract class Component implements Datapathable {
	protected static String statusFormat = "%25s: %s\n";
	protected String label;
	
	private Tuple<Integer, Integer> compPosition;			// stores x/y coordinates
	private Tuple<Integer, Integer> compSize;				// stores width/height
	
	public Component(String label, JSONObject json){
		this.label = label;
		
		try{
			JSONObject gui = json.getJSONObject("gui");
			compPosition = new Tuple<>(gui.getInt("x"), gui.getInt("y"));
			compSize = new Tuple<>(gui.getInt("width"), gui.getInt("height"));
		} catch(Exception e) { System.out.println("!!! " + label + ": " + e.getMessage()); }
		
	}
	
	public String getLabel(){
		return label;
	}
	
	/**
	 * Returns values of every input/output.
	 * @return 
	 */
	public abstract String getStatus();
	
	public abstract Data getInput(String selector);
	
	public Tuple<Integer, Integer> getComponentPosition(){
		return compPosition;
	}
	
	public Tuple<Integer, Integer> getComponentSize(){
		return compSize;
	}
}
