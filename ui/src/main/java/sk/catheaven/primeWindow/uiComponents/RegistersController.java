package sk.catheaven.primeWindow.uiComponents;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import sk.catheaven.core.Component;
import sk.catheaven.model.Data;
import sk.catheaven.model.Tuple;
import sk.catheaven.model.cpu.components.RegBank;

import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class RegistersController implements Initializable {

    @FXML private TableView<Data> registersTable;
    @FXML private TableColumn<Data, Void> regIndexColumn;
    @FXML private TableColumn<Data, Integer> regValueColumn;

    @Autowired
    private Component registerBank;

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
        registersTable.getItems().addAll(((RegBank) registerBank).getRegisters());
    }

    public BooleanProperty visibleProperty() {
        return registersTable.visibleProperty();
    }
}
