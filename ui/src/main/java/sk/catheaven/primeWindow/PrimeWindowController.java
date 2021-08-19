package sk.catheaven.primeWindow;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import sk.catheaven.codeEditor.CodeEditor;
import sk.catheaven.main.Loader;
import sk.catheaven.primeWindow.uiComponents.DatapathController;
import sk.catheaven.primeWindow.uiComponents.InstructionsTableController;
import sk.catheaven.primeWindow.uiComponents.RegistersController;

import java.net.URL;
import java.util.ResourceBundle;

public class PrimeWindowController implements Initializable {
    private CodeEditor codeEditor;

    @FXML TabPane tabPane;
    @FXML Tab codeTab;
    @FXML Tab datapathTab;

    @FXML AnchorPane codePane;

    @FXML private InstructionsTableController instructionsTableController;
    @FXML private RegistersController registersTableController;
    @FXML private DatapathController datapathController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // instructions are only visible when code is being viewed
        instructionsTableController.visibleProperty().bind(codeTab.selectedProperty());
        // registers are only visible if the datapath is being viewed
        registersTableController.visibleProperty().bind(datapathTab.selectedProperty());

        initializeCodeEditor();
    }

    private void initializeCodeEditor() {
        codeEditor = new CodeEditor(Loader.getInstructionSet());
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
}
