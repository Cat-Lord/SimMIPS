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
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.controlsfx.control.PopOver;
import sk.catheaven.hardware.Component;
import sk.catheaven.utils.Subscriber;

/**
 * Graphical representation of component (including wires - Connector class).
 * @author catlord
 */
public class ComponentShape implements Subscriber {
	private final Component sourceComponent;
	private final Shape shape;
	private PopOver popOver;
	
	public ComponentShape(Component sourceComponent, Shape shape){
		this.sourceComponent = sourceComponent;
		this.shape = shape;
		sourceComponent.registerSub(this);
	}
	
	// TODO - add listener to connector

	@Override
	public void updateSub() {
		((Label) (popOver.getContentNode())).setText(sourceComponent.getStatus());
	}

	@Override
	public void prepareSub() {
		this.popOver = new PopOver();
		popOver.setContentNode(new Label());
		popOver.setTitle(sourceComponent.getComponentType() + ":: " + sourceComponent.getLabel());
        popOver.setDetachable(true);
        popOver.setDetached(true);
        popOver.setAnimated(true);
        popOver.cornerRadiusProperty().bind(new SimpleDoubleProperty(6));
        popOver.headerAlwaysVisibleProperty().bind(new SimpleBooleanProperty(true));
        popOver.closeButtonEnabledProperty().bind(new SimpleBooleanProperty(true));
		
		shape.setOnMouseClicked((MouseEvent evt) -> {
			if(popOver.isShowing())
				popOver.hide(Duration.ZERO);
			else
				popOver.show(shape, evt.getScreenX(), evt.getScreenY());
		});
		
		popOver.getRoot().setStyle("-fx-padding: 10 5 5 5;");
	}
}
