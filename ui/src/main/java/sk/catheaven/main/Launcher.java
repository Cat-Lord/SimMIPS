package sk.catheaven.main;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private Scene getScene() {
        Parent root = new VBox();
        Scene scene = new Scene(root);
        return scene;
    }

}
