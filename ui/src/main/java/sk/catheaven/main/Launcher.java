package sk.catheaven.main;

import javafx.application.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application Entry Point. This should be run first, exceptions apply for debugging.
 */
@SpringBootApplication(scanBasePackages = "sk.catheaven")
public class Launcher {

    // don't forget to specify command-line arguments:
    //      --module-path /path/to/JavaFx/javafx-sdk-VERSION/lib --add-modules javafx.controls,javafx.graphics,javafx.fxml
    public static void main(String[] args) {
        Application.launch(FxApplication.class, args);
    }
}
