package sk.catheaven.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import sk.catheaven.events.SimulationEvent;

import java.net.URL;
import java.util.ResourceBundle;

// TODO add reset button and stop button - both with different purposes of stopping running simulation and reverting to
// starting state

@Controller
public class ControlPanel implements Initializable {
    private static Logger log = LogManager.getLogger();
    @FXML private Button playSimulationButton;
    @FXML private Button stepSimulationButton;
    @FXML private Button playFastSimulationButton;
    @FXML private Button pauseSimulationButton;
    @FXML private Button resetSimulationButton;         // also used as STOP

    @FXML private Button zoomInButton;
    @FXML private Button zoomOutButton;

    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeButton(playSimulationButton,      "/icons/play.png");		// play icon made by <a href="https://www.flaticon.com/authors/roundicons" title="Roundicons">Roundicons</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
        initializeButton(playFastSimulationButton,  "/icons/playFast.png");	// play fast icon made by <a href="https://www.flaticon.com/authors/bqlqn" title="bqlqn">bqlqn</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
        initializeButton(resetSimulationButton,     "/icons/stop.png");		// stop  icon made by <a href="https://www.flaticon.com/authors/roundicons" title="Roundicons">Roundicons</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
        initializeButton(pauseSimulationButton,     "/icons/pause.png");	    // pause icon made by <a href="https://www.flaticon.com/authors/bqlqn" title="bqlqn">bqlqn</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
        initializeButton(stepSimulationButton,      "/icons/step.png");		// step  icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
        initializeButton(zoomInButton,              "/icons/zoomIn.png");	    // zoomIn icon made by Icons made by <a href="https://www.flaticon.com/authors/alfredo-hernandez" title="Alfredo Hernandez">Alfredo Hernandez</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
        initializeButton(zoomOutButton,             "/icons/zoomOut.png");	    // zoomOut made by Icons made by <a href="https://www.flaticon.com/authors/alfredo-hernandez" title="Alfredo Hernandez">Alfredo Hernandez</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>

        playSimulationButton.setOnMouseClicked(    handler -> {
            log.debug("SimulationEvent.PLAY");
            context.publishEvent(new SimulationEvent.PLAY());

            playSimulationButton.setDisable(true);
            playFastSimulationButton.setDisable(false);
            pauseSimulationButton.setDisable(false);
            resetSimulationButton.setDisable(false);
            stepSimulationButton.setDisable(true);
        });

        playFastSimulationButton.setOnMouseClicked(handler -> {
            log.debug("SimulationEvent.PLAY_FAST");
            context.publishEvent(new SimulationEvent.PLAY_FAST());

            playSimulationButton.setDisable(false);
            playFastSimulationButton.setDisable(true);
            pauseSimulationButton.setDisable(false);
            resetSimulationButton.setDisable(false);
            stepSimulationButton.setDisable(true);
        });

        resetSimulationButton.setOnMouseClicked(handler -> {
            log.debug("SimulationEvent.RESET");
            context.publishEvent(new SimulationEvent.RESET());

            playSimulationButton.setDisable(false);
            playFastSimulationButton.setDisable(false);
            pauseSimulationButton.setDisable(true);
            resetSimulationButton.setDisable(true);
            stepSimulationButton.setDisable(false);
        });

        pauseSimulationButton.setOnMouseClicked(handler -> {
            log.debug("SimulationEvent.PAUSE");
            context.publishEvent(new SimulationEvent.PAUSE());

            playSimulationButton.setDisable(false);
            playFastSimulationButton.setDisable(false);
            pauseSimulationButton.setDisable(true);
            resetSimulationButton.setDisable(false);		// we can reset whole datapath even when paused
            stepSimulationButton.setDisable(false);
        });

        stepSimulationButton.setOnMouseClicked(handler -> {
            log.debug("SimulationEvent.STEP");
            context.publishEvent(new SimulationEvent.STEP());

            playSimulationButton.setDisable(false);
            playFastSimulationButton.setDisable(false);
            pauseSimulationButton.setDisable(true);
            resetSimulationButton.setDisable(false);
            stepSimulationButton.setDisable(false);
        });

        zoomInButton.setOnMouseClicked(handler -> {
            log.debug("SimulationEvent.ZOOM_IN");
            context.publishEvent(new SimulationEvent.ZOOM_IN());
        });

        zoomOutButton.setOnMouseClicked(handler -> {
            log.debug("SimulationEvent.ZOOM_OUT");
            context.publishEvent(new SimulationEvent.ZOOM_OUT());
        });
    }

    /**
     * Attaches icons to buttons. Icons are located by URL supplied as parameter. If an icon was found, the text
     * inside the button is used as tooltip and erased from button text.
     * @param button Button to initialize.
     * @param imageURL String path to the image resource.
     */
    private void initializeButton(Button button, String imageURL) {
        Image image = new Image(imageURL);
        if (image.isError() == false) {
            button.setTooltip(new Tooltip(button.getText()));
            button.setText("");
            button.setGraphic(new ImageView(image));
        }
    }
}
