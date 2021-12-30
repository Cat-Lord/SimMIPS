package sk.catheaven.primeWindow.uiComponents;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import sk.catheaven.model.Wire;
import sk.catheaven.core.cpu.CPU;
import sk.catheaven.core.cpu.Component;
import sk.catheaven.core.cpu.Connector;
import sk.catheaven.events.SimulationEvent;
import sk.catheaven.model.DatapathComponent;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class DatapathController implements Initializable {
    @FXML AnchorPane datapathPane;

    // Phases Labels
    @FXML private Label InstructionFetchLabel;
    @FXML private Label InstructionDecodeLabel;
    @FXML private Label ExecuteLabel;
    @FXML private Label MemoryLabel;
    @FXML private Label WritebackLabel;

    private List<Label> labelsList;     // labels on top of the datapath representing specific phase (display current instruction for the specific phase)

    private final List<DatapathComponent> datapathComponents;
    private final List<Wire> wires;

    @Autowired
    public DatapathController(CPU cpu) {
        datapathComponents = new ArrayList<>();
        wires = new ArrayList<>();

        for (Component component : cpu.getComponents())
            datapathComponents.add(new DatapathComponent(component));

        for (Connector connector : cpu.getConnectors())
            wires.add(new Wire(connector));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // have to be initialized here because they are loaded from FXML
        labelsList = List.of(InstructionFetchLabel, InstructionDecodeLabel, ExecuteLabel, MemoryLabel, WritebackLabel);

        // add wires first so they are visually 'below' the components
        for (Wire wire  : wires) {
            datapathPane.getChildren().add(wire.getLine());
            datapathPane.getChildren().add(wire.getClickLine());
        }

        for (DatapathComponent component : datapathComponents) {
            Node shape = component.getNode();
            datapathPane.getChildren().add(shape);
        }
    }

    @EventListener
    public void simulationEvent(SimulationEvent.PLAY event) {

    }

    @EventListener
    public void simulationEvent(SimulationEvent.STOP event) {

    }

    @EventListener
    public void simulationEvent(SimulationEvent.PAUSE event) {

    }

    @EventListener
    public void simulationEvent(SimulationEvent.RESET event) {

    }

    @EventListener
    public void simulationEvent(SimulationEvent.STEP event) {

    }

    @EventListener
    public void simulationEvent(SimulationEvent.PLAY_FAST event) {

    }

    @EventListener
    public void simulationEvent(SimulationEvent.ZOOM_IN event) {

    }

    @EventListener
    public void simulationEvent(SimulationEvent.ZOOM_OUT event) {

    }
}
