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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.IntFunction;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
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
import javafx.scene.input.MouseEvent;
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
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.hardware.CPU;
import sk.catheaven.hardware.Component;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.instructionEssentials.Instruction;
import sk.catheaven.utils.Connector;
import sk.catheaven.utils.Tuple;

/**
 *
 * @author catlord
 */
public class MainWindowController implements Initializable {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private final String modifiedFileSuffix = " *";
	
	private FileOperator fileOperator;
	private Stage stage;
	private CodeArea codeEditor;
		
	// ASIDE BOX
	@FXML private VBox asideBox;
	
	@FXML private VBox instructionVbox;
	@FXML private TableView<Instruction> instructionsTable;
	@FXML private TableColumn<Instruction, String> mnemoColumn;
	@FXML private TableColumn<Instruction, String> formatColumn;
	
	@FXML private TableView<Tuple<Integer, String>> registersTable;
	@FXML private TableColumn<Data, Integer> regIndexColumn;
	@FXML private TableColumn<Data, String> regValueColumn;
	
	@FXML private HBox datapathControl;
	
	// TAB PANES
	@FXML private TabPane tabPane;
	@FXML private Tab codeTab;
	@FXML private Pane cpuPane;
	@FXML private Pane datapathPane;
	@FXML private AnchorPane codeTabAnchor;

	@FXML private Button assembleButton;

	// LABELS
	@FXML private Label IFI;		// instruction-fetch instruction
	@FXML private Label IDI;		// instruction-decode instruction
	@FXML private Label EXI;		// ..etc
	@FXML private Label MEMI;
	@FXML private Label WBI;
			
	private ExecutorService executor;		// enables code highlighting
	private static String KEYWORD_PATTERN; 
    private static final String NUMMERO_PATTERN = "\\d*";
    private static final String COMMENT_PATTERN = Assembler.COMMENT_CHAR + ".*";
	private static final String LABEL_PATTERN = "[a-zA-Z]\\w*";
    private static Pattern PATTERN;
	
	private double datapathScaleProperty = 0.8;
	private CPU cpu;
	private PopOver popOver;
	
	private Timer defaultButtonTimer;		// timer, that sets button background back to default
	private TimerTask defButtonTask;
	
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
		KEYWORD_PATTERN = "\\b(";
		for(String mnemo : cpu.getInstructionSet().keySet())
			KEYWORD_PATTERN += mnemo + "|";
		
		KEYWORD_PATTERN = KEYWORD_PATTERN.substring(0, KEYWORD_PATTERN.lastIndexOf("|")) + ")\\b";
		PATTERN = Pattern.compile(
					"(?<KEYWORD>" + KEYWORD_PATTERN + ")"
				  + "|(?<NUMMERO>" + NUMMERO_PATTERN + ")"
				  + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
	              + "|(?<LABEL>" + LABEL_PATTERN + ")"
		  );
		
		codeEditor = initCodeEditor();
		codeTabAnchor.getChildren().add(codeEditor);
			
		IFI.setText("");
		IDI.setText("");
		EXI.setText("");
		MEMI.setText("");
		WBI.setText("");
		
		datapathPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
		asideBox.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
		
		instructionsTable.setVisible(true);
		registersTable.setVisible(false);
		
		drawDatapath();
		
		setUpInstructionSetTable();
		setUpRegistersTable();
		
		// init timer
		defaultButtonTimer = new Timer();
		defButtonTask = new TimerTask(){
			@Override
			public void run() {
				assembleButton.setStyle("-fx-background-color: #e8ebe9;");
				System.out.println("color reset");
			}			
		};
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
			innerHbox.setSpacing(4);
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
		
		editor.setStyle("-fx-font: 27 arial;");
		
		// Following part of code was taken from https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsyncDemo.java
		executor = Executors.newSingleThreadExecutor();	
		Subscription cleanupWhenDone = editor.multiPlainChanges()
                .successionEnds(java.time.Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(editor.multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);
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
			
			
			/*codeTabAnchor.setScaleX(codeEditorScaleProperty);
			codeTabAnchor.setScaleY(codeEditorScaleProperty);
			codeTabAnchor.setScaleZ(codeEditorScaleProperty);*/
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
		
		instructionsTable.getSortOrder().add(mnemoColumn);
		instructionsTable.sort();
	}
	
	private void setUpRegistersTable(){
		registersTable.setPlaceholder(new Label("\tRegister Bank not working !"));
		
		regIndexColumn.setCellValueFactory(new PropertyValueFactory<>("left"));
		regValueColumn.setCellValueFactory(new PropertyValueFactory<>("right"));
		
		Data[] regs = cpu.getRegisters();
		for(int i = 0; i < regs.length; i++){
			Tuple<Integer, String> t = new Tuple<>(i, regs[i].getHex());
			registersTable.getItems().add(t);
		}
		
		//registersTable.getSortOrder().add(regIndexColumn);
		registersTable.sort();
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
			
			shape.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent evt) {
                if (popOver != null && !popOver.isDetached()) {
                    popOver.hide();
                }
				
				if (popOver != null && popOver.isShowing()) {
					popOver.hide();
				}

				double targetX = evt.getScreenX();
				double targetY = evt.getScreenY();

				System.out.println("Creating a new POPOVER");
				popOver = createPopOver();
				popOver.setTitle(c.getComponentType() +":: " + c.getLabel());
				popOver.setContentNode(new Label(c.getStatus()));
				popOver.show(shape, targetX, targetY);
            }
        });
			
			datapathPane.getChildren().add(shape);
		}
		
		datapathPane.setScaleX(datapathScaleProperty);	// dont scale on X
		datapathPane.setScaleY(datapathScaleProperty);	// and scle down Y
		datapathPane.setScaleZ(datapathScaleProperty);	// and Z by a thin
	}
	
	/**
	 * Create a basic PopOver from the controlsfx library.
	 * @return 
	 */
	private PopOver createPopOver() {
        PopOver popOver = new PopOver();
        popOver.setDetachable(false);
        popOver.setDetached(false);
        popOver.arrowSizeProperty().bind(new SimpleDoubleProperty(12));
        popOver.arrowIndentProperty().bind(new SimpleDoubleProperty(12));
        popOver.arrowLocationProperty().bind(new SimpleObjectProperty<>(ArrowLocation.LEFT_TOP));
        popOver.cornerRadiusProperty().bind(new SimpleDoubleProperty(6));
        popOver.headerAlwaysVisibleProperty().bind(new SimpleBooleanProperty(false));
        popOver.setAnimated(true);
        popOver.closeButtonEnabledProperty().bind(new SimpleBooleanProperty(true));
        return popOver;
    }
	
	// Method origin here: https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsyncDemo.java
	private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeEditor.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

	// Method origin here: https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsyncDemo.java
    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeEditor.setStyleSpans(0, highlighting);
    }

	// Method origin here: https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsyncDemo.java
    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("NUMMERO") != null ? "nummero" :
					matcher.group("LABEL")   != null ? "label" :
                    matcher.group("COMMENT") != null ? "comment" :
                    null; /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
	
	
	public void assembleCode(){
		String code = codeEditor.getText();
		if(code.isEmpty()) return;
		
		try {
			cpu.assembleCode(code);
			assembleButton.setStyle("-fx-background-color: #80ff80");
			// datapathControl.setDisable(false);		// i I really wanted to display errors
		}
		catch(SyntaxException errors){
			assembleButton.setStyle("-fx-background-color: #ff9980");
			displayErrors(errors.getErrors());
		}
		defaultButtonTimer.schedule(defButtonTask, 2000);
	}
	
	private void displayErrors(List<Tuple<Integer, String>> errors){
		errors.forEach(e -> {
			System.out.println("Line " + e.getLeft() + ": " + e.getRight());
		});
	}
}