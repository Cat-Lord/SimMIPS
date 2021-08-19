package sk.catheaven.primeWindow.uiComponents;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URL;
import java.util.ResourceBundle;


public class ControlPanel implements Initializable {
    private static Logger log = LogManager.getLogger();
    @FXML private Button playSimulationButton;
    @FXML private Button stepSimulationButton;
    @FXML private Button playFastSimulationButton;
    @FXML private Button pauseSimulationButton;
    @FXML private Button resetSimulationButton;

    @FXML private Button zoomInButton;
    @FXML private Button zoomOutButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeButton(playSimulationButton,      "/icons/play.png");		// play icon made by <a href="https://www.flaticon.com/authors/roundicons" title="Roundicons">Roundicons</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
        initializeButton(playFastSimulationButton,  "/icons/playFast.png");	// play fast icon made by <a href="https://www.flaticon.com/authors/bqlqn" title="bqlqn">bqlqn</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
        initializeButton(resetSimulationButton,     "/icons/stop.png");		// stop  icon made by <a href="https://www.flaticon.com/authors/roundicons" title="Roundicons">Roundicons</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
        initializeButton(pauseSimulationButton,     "/icons/pause.png");	// pause icon made by <a href="https://www.flaticon.com/authors/bqlqn" title="bqlqn">bqlqn</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
        initializeButton(stepSimulationButton,      "/icons/step.png");		// step  icon made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
        initializeButton(zoomInButton,              "/icons/zoomIn.png");	// zoomIn icon made by Icons made by <a href="https://www.flaticon.com/authors/alfredo-hernandez" title="Alfredo Hernandez">Alfredo Hernandez</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
        initializeButton(zoomOutButton,             "/icons/zoomOut.png");	// zoomOut made by Icons made by <a href="https://www.flaticon.com/authors/alfredo-hernandez" title="Alfredo Hernandez">Alfredo Hernandez</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
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
