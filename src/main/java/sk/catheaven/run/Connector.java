/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.util.Iterator;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.hardware.Component;
import sk.catheaven.utils.Subscriber;
		
/**
 * Connecting two components, this class represent a simple way of
 * encapsulating these relationships. 
 * @author catlord
 */
public class Connector implements Subscriber {
	private static final String DEFAULT_COLOR = "#b8cccc";
	
	private Label popoverContent;
	private final Polyline line;
	private Polyline clickLine;		// this is the copy of the original line, but thicker, to allow easier clicking
	
	private final Component sourceComponent;
	private final String selector;
	private PopOver popOver;
	
	/**
	 * Input json array represents each bending point in a line.Appending
	 * point after point give one continuous line from source component 
	 * using specific selector (just to be able to request data on this 
	 * wire (Connector).
	 * @param sourceComponent
	 * @param selector
	 * @param nodes 
	 * @param type 
	 */
	public Connector(Component sourceComponent, String selector, JSONArray nodes, String type) throws JSONException {
		this.sourceComponent = sourceComponent;
		this.selector = selector;
		this.line = createPolyline(nodes, type);
		
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
		if(popOver == null  ||  popOver.getContentNode() == null){
			return;		// TODO - dont do anything, used for testing, where the gui wasnt yet implemented
		}
		
		((Label) (popOver.getContentNode())).setText("" + sourceComponent.getOutput(selector).getData());
	}
	
	public Polyline getLine(){
		return line;
	}
	
	public Polyline getClickLine(){
		return clickLine;
	}
	
	public Component getSourceComponent(){
		return sourceComponent;
	}
	
	public String getSelector(){
		return selector;
	}
	
	/**
	 * Sets specified color. If there is a need to use default color,
	 * it is possible to pass in null parameter for example.
	 * @param color 
	 */
	public void setColor(String color){
		try {
			line.setStroke(Paint.valueOf(color));
		} catch(Exception e) { line.setStroke(Paint.valueOf(DEFAULT_COLOR)); }
	}
	
	/**
	 * From json loads set of points and constructs polyline.
	 * @param json
	 * @return
	 * @throws JSONException 
	 */
	private Polyline createPolyline(JSONArray json, String type) throws JSONException {
		Iterator<Object> jitter = json.iterator();
		double[] nodes = new double[json.length() * 2];		// 2 coordinates for each json object
		int index = 0;
		
		// for all the points create a temporary "node" of its coordinates
		while(jitter.hasNext()){
			JSONObject jobj = (JSONObject) jitter.next();
			int x = jobj.getInt("x");
			int y = jobj.getInt("y");
			
			nodes[index++] = x;
			nodes[index++] = y;
		}
		
		Polyline nline = new Polyline(nodes);
		switch(type){
			case "normal": nline.setStrokeWidth(4);	break;
			case "thin": nline.setStrokeWidth(1);	break;
			case "thick": nline.setStrokeWidth(8);	break;
			default: nline.setStrokeWidth(4); break;
		}
		
		this.clickLine = new Polyline(nodes);
		this.clickLine.setStrokeWidth((nline.getStrokeWidth() +2) * 2);
		this.clickLine.setOpacity(0);
		
		return nline;
	}
	
	public void prepareSub(){
		this.popoverContent = new Label("");
		
		this.popOver = new PopOver();
		popOver.arrowSizeProperty().bind(new SimpleDoubleProperty(0));
		popOver.setContentNode(popoverContent);
        popOver.setDetachable(true);
        popOver.setDetached(true);
        popOver.setAnimated(false);
        popOver.cornerRadiusProperty().bind(new SimpleDoubleProperty(1));
        popOver.headerAlwaysVisibleProperty().bind(new SimpleBooleanProperty(false));
        popOver.closeButtonEnabledProperty().bind(new SimpleBooleanProperty(true));
		popOver.setTitle("");
		
		this.clickLine.setOnMouseClicked((MouseEvent evt) -> {
			if(popOver.isShowing())
				popOver.hide(Duration.ZERO);
			else{
				popOver.show(line, evt.getScreenX(), evt.getScreenY());
			}
		});
	}
	
	/**
	 * Hides popover.
	 */
	@Override
	public void clear(){
		if(popOver != null  &&  popOver.isShowing())
			popOver.hide(Duration.ZERO);
	}
}
