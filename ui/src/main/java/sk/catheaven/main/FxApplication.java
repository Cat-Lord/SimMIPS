package sk.catheaven.main;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import sk.catheaven.events.ApplicationEvent;

import java.io.IOException;

// TODO - solve hanging threads problem:
//          after closing the application window, some threads are still alive and prevent
//          proper application shutdown. May present problems when running outside of IDE.
public class FxApplication extends Application {
    private static final Logger log = LogManager.getLogger();

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        ApplicationContextInitializer<GenericApplicationContext> initializer =
                context -> {
                    context.registerBean(Application.class, () -> FxApplication.this);
                    context.registerBean(Parameters.class, this::getParameters);
                    context.registerBean(HostServices.class, this::getHostServices);
                };
        this.context = new SpringApplicationBuilder()
                .sources(Launcher.class)
                .initializers(initializer)
                .run(getParameters().getRaw().toArray(new String[0]));
    }

    @Override
    public void start(Stage stage) {
        StringBuilder beans = new StringBuilder("====== UI: Available beans ======\n");
        for (String beanName : context.getBeanDefinitionNames()) {
            String beanClass = context.getBean(beanName).getClass().getSimpleName();
            beans.append("     ").append(beanClass).append(": ").append(beanName).append("\n");
        }
        log.debug(beans);
        context.publishEvent(new StageReadyEvent(stage));
    }

    @Override
    public void stop() {
        context.publishEvent(new ApplicationEvent.SHUTDOWN());
        context.close();
        Platform.exit();
    }

    @Component
    static class StageInitializer {

        private final String applicationTitle;
        private final ApplicationContext applicationContext;

        StageInitializer(@Value("${application.name:SimMIPS}") String applicationTitle,
                         @Value("${application.version:}") String applicationVersion,
                         ApplicationContext applicationContext) {
            this.applicationTitle = applicationTitle.concat(" v").concat(applicationVersion);
            this.applicationContext = applicationContext;
        }

        @EventListener
        public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
            try {
                Stage stage = stageReadyEvent.getStage();
                ClassPathResource fxml = new ClassPathResource("/fxml/PrimeWindow.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(fxml.getURL());
                fxmlLoader.setControllerFactory(this.applicationContext::getBean);

                Parent root = fxmlLoader.load();
                Scene scene = new Scene(root);

                try {
                    stage.getIcons().add(new Image("/simmips.png"));
                    scene.getStylesheets().add(getClass().getResource("/css/codeEditor.css").toExternalForm());
                } catch (Exception exception) { log.error("Failed to decorate stage and/or scene", exception.fillInStackTrace());}

                stage.setScene(scene);
                stage.setTitle(this.applicationTitle);
                stage.setMaximized(true);
                stage.show();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static class StageReadyEvent {
        private final Stage stage;

        StageReadyEvent(Stage stage) {
            this.stage = stage;
        }

        public Stage getStage() {
            return stage;
        }
    }
}
