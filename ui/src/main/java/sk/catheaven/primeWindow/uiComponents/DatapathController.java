package sk.catheaven.primeWindow.uiComponents;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import sk.catheaven.events.SimulationEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class DatapathController implements Initializable {
    @FXML VBox datapathPane;
    @FXML ScrollPane datapathScrollPane;

    // Phases Labels
    @FXML private Label InstructionFetchLabel;
    @FXML private Label InstructionDecodeLabel;
    @FXML private Label ExecuteLabel;
    @FXML private Label MemoryLabel;
    @FXML private Label WritebackLabel;

    private List<Label> labelsList;     // store those labels in a list

    public DatapathController() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        labelsList = List.of(InstructionFetchLabel, InstructionDecodeLabel, ExecuteLabel, MemoryLabel, WritebackLabel);
    }

    @Subscribe
    public void simulationEvent(SimulationEvent event) {
        switch (event.getAction()) {
            case PLAY: break;
            case STOP: break;
            case PAUSE: break;
            case STEP: break;
            case PLAY_FAST: break;
            default: break;
        }
    }

    public VBox getDatapathPane() {
        return datapathPane;
    }
}
