/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author catlord
 */
public class Main extends Application {
	public static void main(String[] args){
		launch(args);
	}
	
	@Override
    public void start(Stage stage) throws IOException {
		stage = new Stage();
	
		// load fxml and pass stage into the controller class
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/catheaven/simmips/Primer.fxml"));
		Parent mainPane = (Parent)loader.load();
		loader.<MainWindowController>getController().setStage(stage);

		Scene scene = new Scene(mainPane);
		
		// set up and launch stage
		stage.setTitle("SimMIPS");
        stage.setScene(scene);			
        stage.show();
    }
}