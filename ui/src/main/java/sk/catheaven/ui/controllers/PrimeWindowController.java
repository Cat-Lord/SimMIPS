package sk.catheaven.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import sk.catheaven.codeEditor.CodeEditor;
import sk.catheaven.core.SyntaxErrorsContainer;
import sk.catheaven.core.cpu.CPU;
import sk.catheaven.core.instructions.Instruction;
import sk.catheaven.events.CPUEvent;
import sk.catheaven.events.SimulationEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

@Controller
public class PrimeWindowController implements Initializable {
    private static Logger log = LogManager.getLogger();
    private CodeEditor codeEditor;

    @Autowired
    private Instruction[] instructionSet;
    @Autowired
    private CPU cpu;
    @Autowired
    private ConfigurableApplicationContext context;

    @FXML TabPane tabPane;
    @FXML Tab codeTab;
    @FXML Tab datapathTab;

    @FXML AnchorPane codePane;

    @FXML private Button assembleButton;
    private static String SUCCESSFUL_COMPILATION_STYLE = "-fx-background-color: #80ff80";
    private static String COMPILATION_FAILURE_STYLE = "-fx-background-color: #80ff80";

    @FXML private InstructionsTableController instructionsTableController;
    @FXML private RegistersController registersTableController;
    @FXML private DatapathController datapathController;

    private Timer defaultButtonTimer;       // timer, that sets button background back to default

    public PrimeWindowController() {
        defaultButtonTimer = new Timer();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        assembleButton.toFront();

        // instructions are only visible when code is being viewed
        instructionsTableController.visibleProperty().bind(codeTab.selectedProperty());
        instructionsTableController.getInstructionVbox().getChildren().add(assembleButton);

        // registers are only visible if the datapath is being viewed
        registersTableController.visibleProperty().bind(datapathTab.selectedProperty());

        initializeCodeEditor();
    }

    private void initializeCodeEditor() {
        codeEditor = new CodeEditor(instructionSet);
        CodeArea codeArea = codeEditor.getCodeArea();

        VirtualizedScrollPane<CodeArea> editorPane = new VirtualizedScrollPane<>(codeArea);
        codePane.getChildren().add(editorPane);

        AnchorPane.setLeftAnchor(editorPane, 0.0);
        AnchorPane.setRightAnchor(editorPane, 0.0);
        AnchorPane.setBottomAnchor(editorPane, 0.0);
        AnchorPane.setTopAnchor(editorPane, 0.0);

        codeArea.insertText(0, getSampleProgram());
        codeArea.moveTo(0,0);
    }

    @EventListener
    public void assembleCodeEvent(CPUEvent.COMPILE event) {
    }

    @FXML
    public void assembleCode(){
        log.debug("Assemble code !");
        String code = codeEditor.getCodeArea().getText();

        SyntaxErrorsContainer errors = cpu.assembleCode(code);

        if (errors.isEmpty()) {
            assembleButton.setStyle(SUCCESSFUL_COMPILATION_STYLE);
            context.publishEvent(new SimulationEvent.STOP());
        }
        else {
            assembleButton.setStyle(COMPILATION_FAILURE_STYLE);
            displayErrors(errors);
        }

        defaultButtonTimer.schedule(new TimerTask(){
            @Override
            public void run() {
                assembleButton.setStyle(null);
            }
        }, 2000);
    }

    private String getSampleProgram() {
    return """
            ; An awesome example
            li r1, 15
            li r8, 36

            add r1, r1, r8
            beq r0,r0, cat

            sw r1, 0(r0)
            cat: li r9, 315
            addi r18, r31, 2415""";
    }

    /**
     * Creates a new popup window, which will display set of errors
     * for user.
     * @param errorsContainer
     */
    private void displayErrors(SyntaxErrorsContainer errorsContainer) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/catheaven/simmips/ErrorList.fxml"));

        Parent root;
        try {
            root = loader.load();
            loader.<ErrorWindowController>getController().setData(errorsContainer);
        } catch(IOException e) {
            log.error("Failed to create Error Window !", e.fillInStackTrace());
            return;
        }

        Scene scene = new Scene(root);
        Stage nstage = new Stage();
        nstage.setTitle("Errors");
        nstage.setScene(scene);
        nstage.show();
    }
}
