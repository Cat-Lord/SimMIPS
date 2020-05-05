/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.IntFunction;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import sk.catheaven.hardware.CPU;
import sk.catheaven.hardware.Component;
import sk.catheaven.utils.Tuple;

/**
 *
 * @author catlord
 */
public class MainWindowController implements Initializable {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private Stage stage;
	private CodeArea codeEditor;
	private final String modifiedFileSuffix = " *";
	private int fontSize = 12;
	
	@FXML private TabPane tabPane;
	@FXML private Tab codeTab;
	@FXML private AnchorPane codeTabAP;
	@FXML private Tab cpuTab;
	@FXML private VBox asideBox;
	@FXML private AnchorPane datapathPane;
	
	private CPU cpu;
	
	
	public MainWindowController(){
		try {
			Loader l = new Loader("sk/catheaven/data/layout.json", "sk/catheaven/data/cpu.json");
			cpu = l.getCPU();
		} catch(Exception e) { e.printStackTrace(); System.out.println(e.getMessage()); return; }
		
		try {
			LogManager lm = LogManager.getLogManager();
			Logger lgr = lm.getLogger(Logger.GLOBAL_LOGGER_NAME);
			//lgr.setLevel(Level.OFF);

			FileHandler fh = new FileHandler("log");
			SimpleFormatter sf = new SimpleFormatter();
			fh.setFormatter(sf);
			lgr.addHandler(fh);
		} catch(IOException e) { System.out.println(e.getMessage()); }
		logger.log(Level.INFO, "MainWindowController created");
		System.out.println("MainWindowController: DONE");
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		System.out.println("MainWindowController: INIT");
		codeEditor = initCodeEditor();
		
		asideBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));				// TODO - this was set only for debugging
		
		System.out.println(datapathPane.getPrefWidth()+ " | " + datapathPane.getPrefHeight());
		
		datapathPane.setOnMouseMoved((MouseEvent evt) -> {
			System.out.println("Mouse: " + evt.getX() + " | " + evt.getY());
		});
		
		for(Component c : cpu.getComponents()){
			Tuple<Integer, Integer> pos = c.getComponentPosition();
			Tuple<Integer, Integer> size = c.getComponentSize();
			Rectangle rect = new Rectangle(pos.getLeft(), (datapathPane.getPrefHeight() - pos.getRight()), size.getLeft(), size.getRight());
			datapathPane.getChildren().add(rect);
		}
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