package sk.catheaven.primeWindow.uiComponents;

import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.catheaven.model.Data;
import sk.catheaven.model.Tuple;
import sk.catheaven.model.instructions.Instruction;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class RegistersController implements Initializable {

    @FXML private TableView<Tuple<Integer, String>> registersTable;
    @FXML private TableColumn<Data, Integer> regIndexColumn;
    @FXML private TableColumn<Data, String> regValueColumn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        registersTable.setPlaceholder(new Label("\tRegister Bank not working !"));

        regIndexColumn.setCellValueFactory(new PropertyValueFactory<>("left"));
        regValueColumn.setCellValueFactory(new PropertyValueFactory<>("right"));
    }

    // todo - add items into the table !
    public void addItems() {

    }

    public BooleanProperty visibleProperty() {
        return registersTable.visibleProperty();
    }
}
