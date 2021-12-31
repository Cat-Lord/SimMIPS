package sk.catheaven.ui.controllers;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.controlsfx.control.PopOver;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import sk.catheaven.events.ApplicationEvent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

// TODO: Create a way to instantiate popovers with specific type (component, connector) to obtain relevant information

@Service
public class PopOverFactory implements Initializable {
    private final List<PopOver> existingPopovers;

    @FXML TableView contentTable;
    @FXML TableColumn firstColumn;
    @FXML TableColumn firstColumnValue;
    @FXML TableColumn secondColumn;
    @FXML TableColumn secondColumnValue;

    public PopOverFactory() {
        existingPopovers = new ArrayList<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public PopOver newPopoverInstance() {
        PopOver popOver = new PopOver();

        popOver.arrowSizeProperty().bind(new SimpleDoubleProperty(0));
        popOver.setDetachable(true);
        popOver.setDetached(true);
        popOver.setAnimated(false);
        popOver.cornerRadiusProperty().bind(new SimpleDoubleProperty(1));
        popOver.headerAlwaysVisibleProperty().bind(new SimpleBooleanProperty(false));
        popOver.closeButtonEnabledProperty().bind(new SimpleBooleanProperty(true));
        popOver.setTitle("");

        existingPopovers.add(popOver);
        return popOver;
    }

    @EventListener
    public void onApplicationHide(ApplicationEvent.HIDE event) {
        for (PopOver popOver : existingPopovers)
            if (popOver.isShowing())
                popOver.hide();
    }

}
