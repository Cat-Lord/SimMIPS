package sk.catheaven.main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.components.CPU;
import sk.catheaven.model.instructions.Instruction;

import java.util.List;

public class Launcher extends Application {
    private static final Logger log = LogManager.getLogger();
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            Instruction[] instructions = Loader.getInstructionSet();
        
        
            for (Instruction instruction : instructions) {
                log.debug("{}: type {} with {} fields",
                        instruction.getMnemo(), instruction.getType().getLabel(), instruction.getFields().size());
            }
            log.debug("Successfully loaded {} instructions\n", instructions.length);
        
            CPU cpu = Loader.getCPU();
        
            for (Component component: cpu.getComponents().values())
                log.debug("{}\n{}\n {} input(s) | {} output(s) | {} selector(s)",
                        component.getLabel(), component,
                        component.getInputs().size(), component.getOutputs().size(), component.getSelectors().size()
                );
        
            log.debug("Successfully initiated CPU with {} components and {} connectors\n",
                    cpu.getComponents().size(), cpu.getConnectors().size());
        
            for (String sourceLabel: cpu.getConnectors().keySet())
                log.debug("Component `{}` has {} connections",
                        sourceLabel, cpu.getConnectors().get(sourceLabel).size());
        
            int phaseIndex = 1;
            for (List<Component> phase : cpu.getPhases())
                log.debug("Phase {}: {} Components", phaseIndex++, phase.size());
        
        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
        }
        
        primaryStage.setTitle("Meow");
        Button btn = new Button();
        btn.setText("Cats are love'");
        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
    
}
