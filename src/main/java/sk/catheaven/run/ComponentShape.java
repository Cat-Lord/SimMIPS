/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import sk.catheaven.hardware.Component;
import sk.catheaven.utils.Subscriber;
import sk.catheaven.utils.Tuple;

/**
 * Graphical representation of component (including wires - Connector class). Defines 
 * popup window behavior, displays possible description and implements subscriber to
 * update content on every source component change.
 * @author catlord
 */
public class ComponentShape implements Subscriber {
	private Label popoverContent;
	private final Component sourceComponent;
	private final Shape shape;
	private PopOver popOver;
	private final String description;
	
	public ComponentShape(Component sourceComponent, Shape shape){
		this.sourceComponent = sourceComponent;
		this.shape = shape;
		this.description = sourceComponent.getDescription();
		sourceComponent.registerSub(this);
	}

	@Override
	public void updateSub() {
		if(this.popOver != null){
			popoverContent.setText(description + "\n" + sourceComponent.getStatus());
		}
	}

	@Override
	public void prepareSub() {
		this.popoverContent = new Label("");
		this.popOver = new PopOver(popoverContent);
		popOver.setTitle("\t" + sourceComponent.getComponentType() + ":: " + sourceComponent.getLabel());
        popOver.setDetachable(true);
        popOver.setDetached(true);
        popOver.setAnimated(true);
        popOver.cornerRadiusProperty().bind(new SimpleDoubleProperty(6));
        popOver.headerAlwaysVisibleProperty().bind(new SimpleBooleanProperty(true));
        popOver.closeButtonEnabledProperty().bind(new SimpleBooleanProperty(true));
		popoverContent.setStyle("-fx-padding: 8;");
		
		this.shape.setOnMouseClicked((MouseEvent evt) -> {
			if(this.popOver.isShowing())	
			this.popOver.hide(Duration.ZERO);
			else
				if(evt.getClickCount() == 2)
					this.popOver.show(this.shape, evt.getScreenX(), evt.getScreenY());
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
	 * Creates a text description on top of the shape
	 */
	public Label createLabel(){
		Tuple<Integer, Integer> pos = sourceComponent.getComponentPosition();
		Tuple<Integer, Integer> size = sourceComponent.getComponentPosition();
		Label label = new Label();
		label.setText(sourceComponent.getSymbol());
		label.setStyle("-fx-font: 18 arial");
		label.setStyle("-fx-font-weight: bolder;");
		label.applyCss();
		if(shape instanceof Ellipse){
			label.setLayoutX(pos.getLeft() - 10); 
			label.setLayoutY(pos.getRight() - 7);
		} else {
			label.setLayoutX(pos.getLeft() + 3); 
			label.setLayoutY(pos.getRight() + 2);
		}
		label.toFront();
		return label;
	}
}
