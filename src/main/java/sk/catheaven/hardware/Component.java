/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.ArrayList;
import java.util.List;
import sk.catheaven.utils.Datapathable;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Observable;
import sk.catheaven.utils.Subscriber;
import sk.catheaven.utils.Tuple;

/**
 *
 * @author catlord
 */
public abstract class Component implements Datapathable, Observable {
	private final static String DEFAULT_COLOUR = "#98999a";
	protected static String statusFormat = "%25s: %13s\n";
	protected String label;
	private String componentType;
	
	protected List<Subscriber> subsribers;
	
	private Tuple<Integer, Integer> compPosition;			// stores x/y coordinates
	private Tuple<Integer, Integer> compSize;				// stores width/height
	private String colour;
	
	public Component(String label, JSONObject json){
		this.label = label;
		
		subsribers = new ArrayList<>();
		
		try{
			componentType = json.getString("type");
			JSONObject gui = json.getJSONObject("gui");
			compPosition = new Tuple<>(gui.getInt("x"), gui.getInt("y"));
			compSize = new Tuple<>(gui.getInt("width"), gui.getInt("height"));
			colour = gui.optString("colour");
		} catch(Exception e) { System.out.println("!!! " + label + ": " + e.getMessage()); }
		
		if(colour.isEmpty()) 
			colour = DEFAULT_COLOUR;
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
	
	public abstract void reset();	
	
	public Tuple<Integer, Integer> getComponentPosition(){
		return compPosition;
	}
	
	public Tuple<Integer, Integer> getComponentSize(){
		return compSize;
	}
	
	public String getColour(){
		return colour;
	}
	
	public String getComponentType(){
		return componentType;
	}
	
	@Override
	public void registerSub(Subscriber sub){
		subsribers.add(sub);
	}
	
	@Override
	public void unregisterSub(Subscriber sub){
		subsribers.remove(sub);
	}
	
	@Override
	public void notifySubs(){
		for(Subscriber sub : subsribers)
			sub.updateSub();
	}
}
