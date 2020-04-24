/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.lang.System.Logger;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.IntFunction;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

/**
 *
 * @author catlord
 */
public class MainWindowController implements Initializable {
	private static Logger logger;
	private Stage stage;
	private CodeArea codeEditor;
	private final String modifiedFileSuffix = " *";
	private int fontSize = 12;
	
	@FXML private TabPane tabPane;
	@FXML private Tab codeTab;
	@FXML private AnchorPane codeTabAP;
	@FXML private Tab cpuTab;
	@FXML private VBox asideBox;
		
	public MainWindowController(){		
		MainWindowController.logger = System.getLogger(this.getClass().getName());
		logger.log(Logger.Level.DEBUG, "MainWindowController created");
		System.out.println("MainWindowController: DONE");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		System.out.println("MainWindowController: INIT");
		codeEditor = initCodeEditor();
		
		asideBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));				// TODO - this was set only for debugging
	}
	
	public void setStage(Stage stage){
		this.stage = stage;
		configureStage();
	}
	
	private CodeArea initCodeEditor(){
		CodeArea editor = new CodeArea();
		IntFunction<Node> numberFactory = LineNumberFactory.get(editor);
		IntFunction<Node> arrowFactory = new ArrowFactory(editor.currentParagraphProperty());
		IntFunction<Node> graphicFactory = line -> {
			HBox innerHbox = new HBox(
				numberFactory.apply(line),
				arrowFactory.apply(line)
			);
			innerHbox.setAlignment(Pos.CENTER_LEFT);
			return innerHbox;
		};
        editor.setParagraphGraphicFactory(graphicFactory);
        editor.replaceText(this.getClass().toGenericString() + "\n" + this.getClass().toString());
        editor.moveTo(0, 0);
		
		VirtualizedScrollPane sp = new VirtualizedScrollPane(editor);
		codeTabAP.getChildren().add(sp);

		AnchorPane.setLeftAnchor(sp, 0.0);
		AnchorPane.setRightAnchor(sp, 0.0);
		AnchorPane.setBottomAnchor(sp, 0.0);
		AnchorPane.setTopAnchor(sp, 0.0);

		editor.prefWidthProperty().bind(codeTabAP.widthProperty());
		editor.prefHeightProperty().bind(codeTabAP.heightProperty());
		
		editor.setOnKeyPressed((KeyEvent e) -> markCodeChange());
		
		return editor;
	}
	
	/**
	 * Sets up the stage to allow usage of keyboard shortcuts and other 
	 * useful features.
	 */
	private void configureStage() {
		stage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
			System.out.println("KeyEvent: " + event.toString());
			
			// TODO
			if(event.isControlDown())
				codeEditor.setStyle("-fx-font-size:" + ++fontSize + "px;");
			else if(event.isAltDown())
				codeEditor.setStyle("-fx-font-size:" + --fontSize + "px;");
			
		});
		
		stage.addEventHandler(ZoomEvent.ANY, (ZoomEvent event) -> {
			System.out.println("ZoomEvent: " + event.toString());
		});
		
		stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (WindowEvent event) -> {
			// check if user saved the code
			// and if user really wants to quit
			System.out.println("WindowEvent: " + event.toString());
		});
	}

	/**
	 * If there was a change in code (in editor), we need to mark this change 
	 * and  ask user on exit to save or forget changes, that have been made. 
	 */
	private void markCodeChange(){
		String stageTitle = stage.getTitle();
		if( ! stageTitle.endsWith(modifiedFileSuffix))
			stage.setTitle(stageTitle + modifiedFileSuffix);
	}	
}