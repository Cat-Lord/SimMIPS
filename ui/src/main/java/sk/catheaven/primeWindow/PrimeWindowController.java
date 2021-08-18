package sk.catheaven.primeWindow;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.net.URL;
import java.util.ResourceBundle;

public class PrimeWindowController implements Initializable {

    @FXML TabPane tabPane;
    @FXML Tab codeTab;
    @FXML Tab datapathTab;

    @FXML private InstructionsTableController instructionsTableController;
    @FXML private RegistersController registersTableController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // instructions are only visible when code is being viewed
        instructionsTableController.visibleProperty().bind(codeTab.selectedProperty());
        // registers are only visible if the datapath is being viewed
        registersTableController.visibleProperty().bind(datapathTab.selectedProperty());


    }
}
