/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temporary;

import sk.catheaven.run.*;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Test;

/**
 *
 * @author catlord
 */
public class Main extends Application {
	public static void main(String[] args){
		launch(args);
	}
	
	@Test
	@Override
    public void start(Stage stage) throws IOException {
		stage = new Stage();
	
		// load fxml and pass stage into the controller class
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/catheaven/simmips/Primer.fxml"));
		MainWindowController mwc = new MainWindowController();
		mwc.setStage(stage);
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