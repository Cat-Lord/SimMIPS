/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

/**
 * Main application window. Application entry.
 * @author catlord
 */
public class Launcher extends Application {
	private static final Logger log = LogManager.getLogger();
	public static Properties properties;

	public static void main(String[] args){
		
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws IOException {
		stage = new Stage();
		
		// load fxml and pass stage into the controller class
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/catheaven/simmips/Primer.fxml"));
		MainWindowController mwc = new MainWindowController();
		mwc.setStage(stage);
		mwc.setHostServices(this.getHostServices());
		loader.setController(mwc);
		
		Parent mainPane = loader.load();
		
		Scene scene = new Scene(mainPane);
		scene.getStylesheets().add(getClass().getResource("/sk/catheaven/simmips/stylesheet.css").toExternalForm());
		
		// set up and launch stage
		stage.setTitle("SimMIPS");
		stage.setScene(scene);
		stage.show();
	}
}