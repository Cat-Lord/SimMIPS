/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.IntFunction;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ZoomEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import sk.catheaven.hardware.CPU;
import sk.catheaven.hardware.Component;
import sk.catheaven.instructionEssentials.Instruction;
import sk.catheaven.instructionEssentials.argumentTypes.ArgumentType;
import sk.catheaven.utils.Connector;
import sk.catheaven.utils.Tuple;
/**
 *
 * @author catlord
 */
public class MainWindowController implements Initializable {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private FileOperator fileOperator;
	private Stage stage;
	private CodeArea codeEditor;
	private final String modifiedFileSuffix = " *";
	private int fontSize = 12;
		
	// ASIDE BOX
	@FXML private VBox asideBox;
	
	@FXML private VBox instructionVbox;
	@FXML private TableView<Instruction> instructionsTable;
	@FXML private TableColumn<Instruction, String> mnemoColumn;
	@FXML private TableColumn<Instruction, String> formatColumn;
	
	@FXML private TableView registersTable;
	@FXML private TableColumn regIndexColumn;
	@FXML private TableColumn regValueColumn;
	
	
	// TAB PANES
	@FXML private TabPane tabPane;
	@FXML private Tab codeTab;
	@FXML private Pane cpuPane;
	@FXML private Pane datapathPane;
	@FXML private AnchorPane codeTabAnchor;

	
	private CPU cpu;
	
	
	public MainWindowController(){
		fileOperator = new FileOperator();
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
		
		instructionsTable.setVisible(true);
		registersTable.setVisible(false);
		
		drawDatapath();
		
		setUpInstructionSetTable();
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
		codeTabAnchor.getChildren().add(sp);

		AnchorPane.setLeftAnchor(sp, 0.0);
		AnchorPane.setRightAnchor(sp, 0.0);
		AnchorPane.setBottomAnchor(sp, 0.0);
		AnchorPane.setTopAnchor(sp, 0.0);

		editor.prefWidthProperty().bind(codeTabAnchor.widthProperty());
		editor.prefHeightProperty().bind(codeTabAnchor.heightProperty());
		
		editor.setOnKeyPressed((KeyEvent e) -> markCodeChange(true));
		
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
	private void markCodeChange(boolean mark){
		String stageTitle = stage.getTitle();
		if(mark  &&  ! stageTitle.endsWith(modifiedFileSuffix))
			stage.setTitle(stageTitle + modifiedFileSuffix);										// append file modification suffix
		else if(! mark  &&  stageTitle.endsWith(modifiedFileSuffix))
			stage.setTitle(stageTitle.substring(0, stageTitle.indexOf(modifiedFileSuffix)));		// cut the modification suffix
	}	
	
	public void openFile(){
		String code = "";
		try {
			code = fileOperator.openFile(stage);
		} catch(Exception e) { System.out.println(e.getMessage()); return; }
		
		codeEditor.replaceText(code);
	}
	
	// TODO set the filename
	public void saveFile(){
		try {
			fileOperator.saveFile(stage, "", codeEditor.getText());
		} catch(Exception e) { System.out.println(e.getMessage()); return; }
		
		markCodeChange(false);
	}
	
	// TODO set the filename
	public void saveFileAs(){
		try {
			fileOperator.saveFileAs(stage, "", codeEditor.getText());
		} catch(Exception e) { System.out.println(e.getMessage()); return; }
		
		markCodeChange(false);
	}
	
	/**
	 * When changing tabs from code tab to the 
	 * datapath tab, we need to hide instructionset 
	 * and reveal registers.
	 */
	public void changeTabToCode(){
		registersTable.setVisible(false);
		instructionVbox.setVisible(true);
	}
	
	public void changeTabToCPU(){
		instructionVbox.setVisible(false);
		registersTable.setVisible(true);
	}
	
	/**
	 * Initial setup of Instruction-set table. Settings are set up and instruction set 
	 * is loaded into the table.
	 */
	private void setUpInstructionSetTable(){
		instructionsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);		// single value selection
		instructionsTable.setPlaceholder(new Label("\tInstruction set is \n\tnot properly loaded !"));
		
		mnemoColumn.setCellValueFactory(new PropertyValueFactory<>("mnemo"));
		formatColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
		
		// first put all the known information there
		Map<String, Instruction> iSet = cpu.getInstructionSet();
		for(String mnemo : iSet.keySet())
			instructionsTable.getItems().add(iSet.get(mnemo));
				
		// and after that define row factory event to display 
		// closer information about selected instruction
		// Thanks for the help from James_D: https://stackoverflow.com/questions/30191264/javafx-tableview-how-to-get-the-row-i-clicked
		instructionsTable.setRowFactory(cb -> {
			TableRow<Instruction> row = new TableRow();
			row.setOnMouseClicked( (event) -> {
				if( ! row.isEmpty()  &&  event.getButton() == MouseButton.PRIMARY  &&  event.getClickCount() == 2){
					System.out.println("INSTRUCTION : " + row.getItem().getMnemo());		// TODO - popup window
				}
			});
			return row;
		});
	}
	
	private void drawDatapath(){
		
		for(Connector c : cpu.getWires()){
			datapathPane.getChildren().add(c.getLine());
		}
		
		for(Component c : cpu.getComponents()){
			Tuple<Integer, Integer> pos = c.getComponentPosition();
			Tuple<Integer, Integer> size = c.getComponentSize();
			
			Shape shape;
			if(c.getComponentType().equals("ControlUnit") ||  c.getComponentType().equals("SignExt"))
				shape = new Ellipse(pos.getLeft(), (pos.getRight()), size.getLeft(), size.getRight());
			else
				shape = new Rectangle(pos.getLeft(), (pos.getRight()), size.getLeft(), size.getRight());
			shape.setStroke(Paint.valueOf("black"));
			shape.setFill(Paint.valueOf(c.getColour()));
			datapathPane.getChildren().add(shape);
		}
	}
}