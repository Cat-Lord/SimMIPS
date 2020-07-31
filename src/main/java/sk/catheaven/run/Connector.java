/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
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
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Subscriber;
import sk.catheaven.utils.Tuple;
		
/**
 * Connecting two components, this class represent a simple way of
 * encapsulating these relationships. 
 * @author catlord
 */
public class Connector implements Subscriber {
	private static final String DEFAULT_COLOR = "#b8cccc";
	
	private final List<Tuple<String, Data>> dataInformation;
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
	public Connector(Component sourceComponent, String selector, JSONArray nodes, JSONArray dataInfo, String type) throws JSONException {
		this.sourceComponent = sourceComponent;
		this.selector = selector;
		this.line = createPolyline(nodes, type);
		
		setColor(null);
		sourceComponent.registerSub(this);
		dataInformation = setupDataInformation(dataInfo);
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
		
		popoverContent.setText("");
		
		int data = sourceComponent.getOutput(selector).getData();
		
		if(dataInformation == null)
			popoverContent.setText("" + data);
		else{
			String format = "%8s: %5d";
			
			for(int i = 0; i < dataInformation.size(); i++){
				Tuple<String, Data> tuple = dataInformation.get(i);
				tuple.getRight().setData(data);
				data >>= tuple.getRight().getBitSize();
				String nextMessage = String.format(format, tuple.getLeft(), tuple.getRight().getData());
				
				// skip for first one, because there is no content in the popover
				if(i > 0)
					nextMessage = nextMessage.concat("\n".concat(popoverContent.getText()));
				
				popoverContent.setText(nextMessage);	
			}
		}
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
			case "normal": nline.setStrokeWidth(4.5);	break;
			case "thin": nline.setStrokeWidth(1);	break;
			case "thick": nline.setStrokeWidth(7.5);	break;
			default: nline.setStrokeWidth(5); break;
		}
		
		this.clickLine = new Polyline(nodes);
		this.clickLine.setStrokeWidth((nline.getStrokeWidth() +2) * 2);
		this.clickLine.setOpacity(0);
		
		return nline;
	}
	
	@Override
	public void prepareSub(){
		this.popoverContent = new Label("");
		this.popoverContent.setPadding(new Insets(15));
		
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
	
	/**
	 * Json can have optional json array argument, which describes information about data which travels through the "wire".
	 * This method searches for the array and saves a reference to the meaning of passed argument.
	 * @param json null, if input array is null. Otherwise returns list of String-Data touples.
	 */
	private List<Tuple<String, Data>> setupDataInformation(JSONArray jarray) throws JSONException {	
		if(jarray == null) 
			return null;
		
		List<Tuple<String, Data>> list = new ArrayList();
		
		Iterator<Object> jitter = jarray.iterator();
		while(jitter.hasNext()){
			JSONObject jobj = (JSONObject) jitter.next();
			
			// expecting only one value
			String label = jobj.keys().next();
			int bitSize = jobj.getInt(label);
			
			list.add(new Tuple(label, new Data(bitSize)));
		}
		
		// reverse the list since the data is being cut from the right side and not left
		/**
		 * Explanation:
		 *  data: 11001010010001
		 *  
		 * We know there are 7 2-bit elements. Parsing is done like this
		 * 110010100100 | 01
		 * 1100101001 | 00
		 * 11001010 | 01
		 * 110010 | 10
		 * 1100 | 10
		 * 11 | 00
		 * 11
		 * 
		 * Output: 
		 *  "": 11
		 *  "": 00
		 *  "": 10
		 *  "": 10
		 *  "": 01
		 *  "": 00
		 *  "": 01 
		 * 
		 * In order to make this appear in order, we reverse the list, start from end and also print reversed way.
		 */
		Collections.reverse(list);		
		
		return list;
	}
}