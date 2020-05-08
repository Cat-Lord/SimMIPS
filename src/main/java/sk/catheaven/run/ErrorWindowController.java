/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import sk.catheaven.utils.Tuple;

/**
 *
 * @author catlord
 */
public class ErrorWindowController implements Initializable {
	@FXML private VBox errorsBox;
	
	public ErrorWindowController(){
		
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
	}
	
	public void setData(List<Tuple<Integer, String>> errors){
		for(int i = 0; i < errors.size(); i++){
			errorsBox.getChildren().add(createLabel(
					String.format("Line %3d: %s", errors.get(i).getLeft(), errors.get(i).getRight()))
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
