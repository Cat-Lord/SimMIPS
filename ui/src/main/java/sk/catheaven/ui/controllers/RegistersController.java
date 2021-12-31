package sk.catheaven.ui.controllers;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import sk.catheaven.core.Data;
import sk.catheaven.core.cpu.CPU;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class RegistersController implements Initializable {
    private static final Logger log = LogManager.getLogger();

    @FXML private TableView<Data> registersTable;
    @FXML private TableColumn<Data, Void> regIndexColumn;
    @FXML private TableColumn<Data, Integer> regValueColumn;

    private Data[] registers;

    public RegistersController(CPU cpu) {
        this.registers = cpu.getRegisters();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registersTable.setPlaceholder(new Label("\tRegister Bank not working !"));

        regIndexColumn.setCellFactory(col -> {
            TableCell<Data, Void> cell = new TableCell<>();

            cell.textProperty().bind(Bindings.createStringBinding(() -> {
                if (cell.isEmpty()) {
                    return null ;
                } else {
                    return Integer.toString(cell.getIndex());
                }
            }, cell.emptyProperty(), cell.indexProperty()));

            return cell;
        });
        regValueColumn.setCellValueFactory(new PropertyValueFactory<>("data"));
        addItems();
    }

    public void addItems() {
        registersTable.getItems().addAll(registers);
    }

    public BooleanProperty visibleProperty() {
        return registersTable.visibleProperty();
    }
}
