package sk.catheaven.primeWindow.uiComponents;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.instructions.Instruction;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class InstructionsTableController implements Initializable {
    private static final Logger log = LogManager.getLogger();

    @FXML private VBox instructionVbox;
    @FXML private TableView<Instruction> instructionsTable;
    @FXML private TableColumn<Instruction, String> mnemoColumn;
    @FXML private TableColumn<Instruction, String> formatColumn;

    @FXML private Button assembleButton;

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
                if (row.isEmpty() == false  &&  isDoubleClick(event))
                    log.info("Selected instruction: {}", row.getItem().getMnemo());		// TODO - popup window
            });
            return row;
        });

        instructionsTable.getSortOrder().add(mnemoColumn);
        instructionsTable.sort();
    }

    // todo - add items into the table !
    public void addItems(Map<String, Instruction> instructionMap) {
        for (String mnemo : instructionMap.keySet())
            instructionsTable.getItems().add(instructionMap.get(mnemo));

    }

    private boolean isDoubleClick(MouseEvent event) {
        return event.getButton() == MouseButton.PRIMARY  &&  event.getClickCount() == 2;
    }

    public BooleanProperty visibleProperty() {
        return instructionVbox.visibleProperty();
    }
}
