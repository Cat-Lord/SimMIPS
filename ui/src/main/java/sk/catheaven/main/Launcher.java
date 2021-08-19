package sk.catheaven.main;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.greenrobot.eventbus.EventBus;
import sk.catheaven.AppProperties;
import sk.catheaven.events.ApplicationEvent;

import java.io.InputStream;
import java.util.Properties;

public class Launcher extends Application {
    private static final Logger log = LogManager.getLogger();
    public static AppProperties properties;
    private static HostServices localHostServices;

    public static void main(String[] args) {
        properties = new AppProperties();
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        Launcher.setLocalHostServices(getHostServices());

        Scene scene = getScene();
        try {
            scene.getStylesheets().add(getClass().getResource("/css/codeEditor.css").toExternalForm());
        } catch (Exception exception) { log.error(exception.getMessage()); }

        primaryStage.setScene(scene);
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

    @Override
    public void stop() throws Exception {
        EventBus.getDefault().post(new ApplicationEvent(ApplicationEvent.Action.SHUTDOWN));
        super.stop();
    }

    public static void setLocalHostServices(HostServices localHostServices) {
        Launcher.localHostServices = localHostServices;
    }

    public static HostServices getLocalHostServices() {
        return localHostServices;
    }
}
