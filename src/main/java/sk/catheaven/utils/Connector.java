/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

import java.util.Iterator;
import javafx.scene.shape.Polyline;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.hardware.Component;
		
/**
 * Connecting two components, this class represent a simple way of
 * encapsulating these relationships. 
 * @author catlord
 */
public class Connector implements Subscriber {
	private Polyline line;
	private final Component sourceComponent;
	private final String selector;
	private String data;						// data given by component in some preferred format

	/**
	 * Input json array represents each bending point in a line.Appending
	 * point after point give one continuous line from source component 
	 * using specific selector (just to be able to request data on this 
	 * wire (Connector).
	 * @param sourceComponent
	 * @param nodes 
	 */
	public Connector(Component sourceComponent, String selector, JSONArray nodes) throws JSONException {
		this.sourceComponent = sourceComponent;
		this.selector = selector;
		this.line = createPolyline(nodes);
		sourceComponent.registerSub(this);
	}
	
	/**
	 * Observer pattern implementation of update. This will be called
	 * when executing <code>notifySubs()</code>. The format of data
	 * is string to get the required format and not filter, which one
	 * is required (for example, decimal is required, so we get 
	 * decimal string and not raw Data object).
	 * TODO - needs implementation (possible feature)
	 */
	@Override
	public void updateSub(){
		data = "" + sourceComponent.getOutput(selector).getData();
	}
	
	public Polyline getLine(){
		return line;
	}

	/**
	 * From json loads set of points and constructs polyline.
	 * @param json
	 * @return
	 * @throws JSONException 
	 */
	private Polyline createPolyline(JSONArray json) throws JSONException {
		Iterator<Object> jitter = json.iterator();
		double[] nodes = new double[json.length() * 2];		// 2 coordinates for each json object
		int index = 0;
		
		while(jitter.hasNext()){
			JSONObject jobj = (JSONObject) jitter.next();
			int x = jobj.getInt("x");
			int y = jobj.getInt("y");
			
			nodes[index++] = x;
			nodes[index++] = y;
		}
		Polyline nline = new Polyline(nodes);
		nline.setStrokeWidth(2.0d);		// TODO test best size
		return nline;
	}
	
}
