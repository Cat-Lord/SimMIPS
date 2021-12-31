package sk.catheaven.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;
import sk.catheaven.core.SyntaxErrorsContainer;
import sk.catheaven.core.Tuple;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@Controller
public class ErrorWindowController implements Initializable {
    @FXML
    private VBox errorsBox;

    public ErrorWindowController(){

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setData(SyntaxErrorsContainer errorsContainer){
        for (Tuple<Integer, String> lineError : errorsContainer.getLineErrors()) {
            errorsBox.getChildren().add(createLabel(
                String.format("Line %3d: %s", lineError.getLeft(), lineError.getRight()))
            );
            errorsBox.getChildren().add(createSeparator());
        }
    }

    private Separator createSeparator(){
        Separator sep = new Separator();
        sep.setPadding(new Insets(8,0,8,0));
        return sep;
    }

    private Label createLabel(String error){
        Label lab = new Label(error);
        lab.setStyle("-fx-font: 15 arial;");
        return lab;
    }
}
