package sk.catheaven.primeWindow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import org.greenrobot.eventbus.EventBus;
import sk.catheaven.events.SimulationEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    @FXML private MenuItem openMenuItem;
    @FXML private MenuItem saveMenuItem;
    @FXML private MenuItem saveAsMenuItem;

    @FXML private MenuItem playMenuItem;
    @FXML private MenuItem stepMenuItem;
    @FXML private MenuItem resetMenuItem;
    @FXML private MenuItem playFastMenuItem;
    @FXML private MenuItem pauseMenuItem;

    @FXML private MenuItem assembleMenuItem;

    @FXML private MenuItem zoomInMenuItem;
    @FXML private MenuItem zoomOutMenuItem;

    private Stage aboutStage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.ALT_DOWN, KeyCombination.CONTROL_DOWN));

        zoomInMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN));
        zoomOutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN));

        playMenuItem.setOnAction(event -> EventBus.getDefault().post(new SimulationEvent(SimulationEvent.Action.PLAY)));
        stepMenuItem.setOnAction(event -> EventBus.getDefault().post(new SimulationEvent(SimulationEvent.Action.STEP)));
        resetMenuItem.setOnAction(event -> EventBus.getDefault().post(new SimulationEvent(SimulationEvent.Action.RESET)));
        playFastMenuItem.setOnAction(event -> EventBus.getDefault().post(new SimulationEvent(SimulationEvent.Action.PLAY_FAST)));
        pauseMenuItem.setOnAction(event -> EventBus.getDefault().post(new SimulationEvent(SimulationEvent.Action.PAUSE)));

        zoomInMenuItem.setOnAction(event -> EventBus.getDefault().post(new SimulationEvent(SimulationEvent.Action.ZOOM_IN)));
        zoomOutMenuItem.setOnAction(event -> EventBus.getDefault().post(new SimulationEvent(SimulationEvent.Action.ZOOM_OUT)));
    }

    public void displayAboutWindow() throws IOException {
        if (aboutStage == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/About.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            aboutStage = new Stage();
            aboutStage.setScene(scene);
        }

        aboutStage.show();
    }

}
