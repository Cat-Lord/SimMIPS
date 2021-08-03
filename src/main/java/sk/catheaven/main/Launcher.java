/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.main;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.components.CPU;
import sk.catheaven.model.components.Component;
import sk.catheaven.model.instructions.Instruction;

/**
 * Main application window. Application entry.
 * @author catlord
 */
public class Launcher extends Application {
	private static final Logger log = LogManager.getLogger();
	public static Properties properties;

	public static void main(String[] args){
		
		InputStream instructionsJsonResource = Launcher.class.getResourceAsStream("/sk/catheaven/design/instructions.json");
		InputStream cpuJsonResource = Launcher.class.getResourceAsStream("/sk/catheaven/design/cpu.json");
		try {
			Instruction[] instructions = Loader.getInstructionSet(instructionsJsonResource);
			
			
			for (Instruction instruction : instructions) {
				log.debug("{}: type {} with {} fields",
						instruction.getMnemo(), instruction.getType().getLabel(), instruction.getFields().size());
			}
			log.debug("Successfully loaded {} instructions\n", instructions.length);
			
			CPU cpu = Loader.getCPU(cpuJsonResource);
			
			for (Component component: cpu.getComponents().values())
				log.debug("{}\n{}\n {} input(s) | {} output(s) | {} selector(s)",
						component.getLabel(), component,
						component.getInputs().size(), component.getOutputs().size(), component.getSelectors().size()
				);
			
			log.debug("Successfully initiated CPU with {} components and {} connectors\n",
					cpu.getComponents().size(), cpu.getConnectors().size());
		} catch (Exception exception) {
			log.error(exception.getMessage());
			exception.printStackTrace();
		}
	}
	
	@Override
    public void start(Stage stage) throws IOException {
		try {
			final var primaryScreen = Screen.getPrimary().getBounds();
			stage = new Stage();
			stage.setWidth(primaryScreen.getWidth());
			stage.setHeight(primaryScreen.getHeight());

			// load fxml and pass stage into the controller class
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/catheaven/fxml/MainWindow.fxml"));
			MainWindowController mwc = new MainWindowController();
			mwc.setStage(stage);
			mwc.setHostServices(this.getHostServices());
			loader.setController(mwc);

			Parent mainPane = loader.load();
			Scene scene = new Scene(mainPane);
			scene.getStylesheets().add(Objects.requireNonNull(
					getClass().getResource("/sk/catheaven/stylesheets/stylesheet.css")).toExternalForm());

			// set up and launch stage
			stage.setTitle(properties.getProperty("applicationName"));
			stage.setScene(scene);
			stage.show();
		} catch(Exception exception) {
			System.err.println("Unable to start the application");
			System.err.println(exception.getMessage());
		}
    }
}