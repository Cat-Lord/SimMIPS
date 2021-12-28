package sk.catheaven.main;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "sk.catheaven")
public class Launcher {
    public static void main(String[] args) {
        Application.launch(FxApplication.class, args);
    }
}
