package sk.catheaven.ui.controllers;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import sk.catheaven.core.cpu.CPU;
import sk.catheaven.core.instructions.Instruction;
import sk.catheaven.events.SimulationEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.TimerTask;

@Controller
public class InstructionsTableController implements Initializable {
    private static final Logger log = LogManager.getLogger();

    @Autowired
    private Instruction[] instructionSet;

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private CPU cpu;

    @FXML private VBox instructionVbox;
    @FXML private TableView<Instruction> instructionsTable;
    @FXML private TableColumn<Instruction, String> mnemoColumn;
    @FXML private TableColumn<Instruction, String> formatColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instructionsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        instructionsTable.setPlaceholder(new Label("\tInstruction set is \n\tnot properly loaded !"));
        mnemoColumn.setCellValueFactory(new PropertyValueFactory<>("mnemo"));
        formatColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        // and after that define row factory event to display
        // closer information about selected instruction
        // Thanks for the help from James_D: https://stackoverflow.com/questions/30191264/javafx-tableview-how-to-get-the-row-i-clicked
        instructionsTable.setRowFactory(callback -> {
            TableRow<Instruction> row = new TableRow<>();
            row.setOnMouseClicked( (event) -> {
                // TODO - popup window on selected instruction (show basic info)
                // doesnt work for now, doesnt register clicks for some reason
                if (row.isEmpty() == false  &&  isDoubleClick(event))
                    log.info("Selected instruction: {}", row.getItem().getMnemo());
            });
            return row;
        });

        instructionsTable.getSortOrder().add(mnemoColumn);
        instructionsTable.sort();
        addItems();
    }

    public VBox getInstructionVbox() {
        return instructionVbox;
    }

    public void addItems() {
        for (Instruction instruction : instructionSet)
            instructionsTable.getItems().add(instruction);
    }

    private boolean isDoubleClick(MouseEvent event) {
        return event.getButton() == MouseButton.PRIMARY  &&  event.getClickCount() == 2;
    }

    public BooleanProperty visibleProperty() {
        return instructionVbox.visibleProperty();
    }
}
