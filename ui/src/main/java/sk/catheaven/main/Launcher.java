package sk.catheaven.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.AppProperties;

import java.io.InputStream;
import java.util.Properties;

public class Launcher extends Application {
    private static final Logger log = LogManager.getLogger();
    public static AppProperties properties;

    public static void main(String[] args) {
        properties = new AppProperties();
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(getScene());
        primaryStage.setTitle(properties.get(AppProperties.APPLICATION_NAME));

        try {
            primaryStage.getIcons().add(new Image("/simmips.png"));
        } catch (Exception exception) {
            log.error("Failed to load application icon: {}", exception.getMessage());
        }

        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private Scene getScene() {
        Parent root = new AnchorPane();
        try {
            root = FXMLLoader.load(getClass().getResource("/fxml/PrimeWindow.fxml"));
        } catch (Exception exception) {
            log.error("Failed to load main fxml: {}", exception.getMessage());
            exception.printStackTrace();
        }
        return new Scene(root);
    }

}
