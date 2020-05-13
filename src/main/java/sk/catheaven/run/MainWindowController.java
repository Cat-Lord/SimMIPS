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
import java.util.ArrayList;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;
import java.util.logging.FileHandler;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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
import javafx.util.Duration;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.reactfx.Subscription;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.hardware.AND;
import sk.catheaven.hardware.CPU;
import sk.catheaven.hardware.Component;
import sk.catheaven.hardware.DataMemory;
import sk.catheaven.hardware.LatchRegister;
import sk.catheaven.hardware.RegBank;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.instructionEssentials.Instruction;
import sk.catheaven.utils.DataTable;
import sk.catheaven.utils.DatapathLabelManager;
import sk.catheaven.utils.RegTable;
import sk.catheaven.utils.Subscriber;
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
	@FXML private Tab datapathTab;
	@FXML private AnchorPane datapathPane;
	@FXML private ScrollPane datapathScrollPane;
	@FXML private AnchorPane codeTabAnchor;

	@FXML private Button assembleButton;
	@FXML private Button playSimulationButton;
	@FXML private Button stepSimulationButton;
	@FXML private Button playFastSimulationButton;
	@FXML private Button pauseSimulationButton;
	@FXML private Button resetSimulationButton;
	
	@FXML private Button zoomInButton;
	@FXML private Button zoomOutButton;


	// LABELS
	@FXML protected Label IFI;		// instruction-fetch instruction
	@FXML protected Label IDI;		// instruction-decode instruction
	@FXML protected Label EXI;		// ..etc
	@FXML protected Label MEMI;
	@FXML protected Label WBI;
	private DatapathLabelManager datapathLabeler;
	
	// Menu items
	@FXML private MenuItem playMI;
	@FXML private MenuItem stepMI;
	@FXML private MenuItem resetMI;
	@FXML private MenuItem playFastMI;
	@FXML private MenuItem pauseMI;
	@FXML private MenuItem assembleMI;
	@FXML private MenuItem zoomInMI;
	@FXML private MenuItem zoomOutMI;
	
	// set of shortcuts (KC == keyCombination)
	private final KeyCombination saveKC;
	private final KeyCombination zoomInKC;
	private final KeyCombination zoomOutKC;
	private final KeyCombination assembleKC;
	
	private ExecutorService executor;		// enables code highlighting
	private static String KEYWORD_PATTERN; 
    private static final String NUMMERO_PATTERN = "\\d*";
    private static final String COMMENT_PATTERN = Assembler.COMMENT_CHAR + ".*";
	private static final String LABEL_PATTERN = "[a-zA-Z]\\w*";
    private static Pattern PATTERN;
	
	private CPU cpu;
	
	private static List<String> colors;				// colors of wires
	private final List<Subscriber> datapathNodes;	// notification-receiving gui elements of datapath
	
	private int currentPhaseLimit = 1;				// phase of execution (IF, ID, EXE, ...)
	
	private double datapathScaleProperty = 0.75;	// graphical scaling of the datapath section
	private double datapathScaleAmount = 0.05;		// the amount datapath will scale each scaling action
	
	private int codeFontSize = 24;

	private Timer defaultButtonTimer;				// timer, that sets button background back to default

	private final ScheduledExecutorService simulationThread;
	ScheduledFuture<?> currentSimulation;
	private long normalSimulationPeriod = 3000;
	private long quickSimulationPeriod = 1000;
	
	private RegTable regTable;						// handles register table updates 
	private DataTable dataTable;					// displays memory to user on request
	private Subscription cleanupWhenDone;			// code highlight
	
	/**
	 * Window and application initialization.
	 */
	public MainWindowController(){
		fileOperator = new FileOperator();
		colors = initColors();
		datapathNodes = new ArrayList<>();
		simulationThread = Executors.newScheduledThreadPool(1);
		
		saveKC = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
		zoomInKC = new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN);
		zoomOutKC = new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN);
		assembleKC = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
		
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
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		KEYWORD_PATTERN = "\\b(";
		for(String mnemo : cpu.getInstructionSet().keySet())
			KEYWORD_PATTERN += mnemo + "|";
		KEYWORD_PATTERN = KEYWORD_PATTERN.substring(0, KEYWORD_PATTERN.lastIndexOf("|")) + ")\\b";
		
		PATTERN = Pattern.compile(
					"(?<KEYWORD>"  + KEYWORD_PATTERN + ")"
				  + "|(?<NUMMERO>" + NUMMERO_PATTERN + ")"
				  + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
	              + "|(?<LABEL>"   + LABEL_PATTERN   + ")"
		  );
		
		codeEditor = initCodeEditor();
			
		datapathLabeler = new DatapathLabelManager(IFI, IDI, EXI, MEMI, WBI);
		
		asideBox.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));
		
		instructionsTable.setVisible(true);
		registersTable.setVisible(false);
		
		setUpInstructionSetTable();
		
		defaultButtonTimer = new Timer();	
		
		// BUTTON ICONS
		playSimulationButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sk/catheaven/simmips/play.png"))));		// play button made by <a href="https://www.flaticon.com/authors/roundicons" title="Roundicons">Roundicons</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
		playSimulationButton.setTooltip(createTooltip("Play Simulation"));
		
		playFastSimulationButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sk/catheaven/simmips/playFast.png"))));	// play fast button made by <a href="https://www.flaticon.com/authors/bqlqn" title="bqlqn">bqlqn</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
		playFastSimulationButton.setTooltip(createTooltip("Play Fast"));
		
		resetSimulationButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sk/catheaven/simmips/stop.png"))));		// stop button made by <a href="https://www.flaticon.com/authors/roundicons" title="Roundicons">Roundicons</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
		resetSimulationButton.setTooltip(createTooltip("Reset Simulation"));
		
		pauseSimulationButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sk/catheaven/simmips/pause.png"))));		// pause button made by <a href="https://www.flaticon.com/authors/bqlqn" title="bqlqn">bqlqn</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
		pauseSimulationButton.setTooltip(createTooltip("Pause Simulation"));
		
		stepSimulationButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sk/catheaven/simmips/step.png"))));		// step button made by <a href="https://www.flaticon.com/authors/freepik" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
		stepSimulationButton.setTooltip(createTooltip("Step Simulation by one Cycle"));
		
		zoomInButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sk/catheaven/simmips/zoomIn.png"))));				// zoomin button made by Icons made by <a href="https://www.flaticon.com/authors/alfredo-hernandez" title="Alfredo Hernandez">Alfredo Hernandez</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
		zoomInButton.setTooltip(createTooltip("Zoom In"));
		
		zoomOutButton.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/sk/catheaven/simmips/zoomOut.png"))));			// zoom out made by Icons made by <a href="https://www.flaticon.com/authors/alfredo-hernandez" title="Alfredo Hernandez">Alfredo Hernandez</a> from <a href="https://www.flaticon.com/" title="Flaticon"> www.flaticon.com</a>
		zoomInButton.setTooltip(createTooltip("Zoom Out"));
		
		// setting accelerators somehow disables shortcut detection (don't know why)
		//assembleMI.setAccelerator(assembleKC);
		//zoomInMI.setAccelerator(zoomInKC);
		//zoomOutMI.setAccelerator(zoomOutKC);
		
		AnchorPane.setLeftAnchor(datapathScrollPane, 0.0);
		AnchorPane.setRightAnchor(datapathScrollPane, 0.0);
		AnchorPane.setTopAnchor(datapathScrollPane, 0.0);
		AnchorPane.setBottomAnchor(datapathScrollPane, 0.0);
		
		initDatapath();
	}
	
	public void setStage(Stage stage){
		this.stage = stage;
		configureStage();
	}
	
	/**
	 * Part of this code was taken from: https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/lineindicator/LineIndicatorDemo.java
	*/
	private CodeArea initCodeEditor(){
		CodeArea editor = new CodeArea();
		IntFunction<Node> numberFactory = LineNumberFactory.get(editor);
		IntFunction<Node> arrowFactory = new ArrowFactory(editor.currentParagraphProperty());
		IntFunction<Node> graphicFactory = line -> {
			HBox innerHbox = new HBox(
				numberFactory.apply(line),
				arrowFactory.apply(line)
			);
			innerHbox.setSpacing(5);
			innerHbox.setAlignment(Pos.CENTER_LEFT);
			return innerHbox;
		};
        editor.setParagraphGraphicFactory(graphicFactory);
        editor.replaceText( "li r1, 15\n" +
							"li r8, 36\n" +
							"\n" +
							"add r1, r1, r8\n" +
							"beq r0,r0, cat\n" +
							"\n" +
							"sw r1, 0(r0)\n" +
							"cat: li r9, 315\n" +
							"addi r18, r31, 2415");
        editor.moveTo(0, 0);
		
		VirtualizedScrollPane sp = new VirtualizedScrollPane(editor);
		codeTabAnchor.getChildren().add(sp);

		AnchorPane.setLeftAnchor(sp, 0.0);
		AnchorPane.setRightAnchor(sp, 0.0);
		AnchorPane.setBottomAnchor(sp, 0.0);
		AnchorPane.setTopAnchor(sp, 0.0);

		editor.prefWidthProperty().bind(codeTabAnchor.widthProperty());
		editor.prefHeightProperty().bind(codeTabAnchor.heightProperty());
		
		editor.setOnKeyPressed((KeyEvent key) -> {
			// ignore arrows
			if(key.getCode().equals(KeyCode.UP)  ||  
				key.getCode().equals(KeyCode.UP)  ||  
				key.getCode().equals(KeyCode.UP)  ||  
				key.getCode().equals(KeyCode.UP))
			  return;
			
			markCodeChange(true);
		});
		
		editor.setStyle("-fx-font: " + codeFontSize + " arial;");
		
		// Following part of code was taken from https://github.com/FXMisc/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsyncDemo.java
		executor = Executors.newSingleThreadExecutor();	
		cleanupWhenDone = editor.multiPlainChanges()
                .successionEnds(java.time.Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(editor.multiPlainChanges())
                .filterMap(t -> {
                    if(t.isSuccess()) {
                        return Optional.of(t.get());
                    } else { return Optional.empty(); }
                })
                .subscribe(this::applyHighlighting);
		
		codeTabAnchor.getChildren().add(editor);
		return editor;
	}
	
	/**
	 * Sets up the stage to allow usage of keyboard shortcuts and other 
	 * useful features.
	 */
	private void configureStage() {
		stage.addEventHandler(KeyEvent.KEY_PRESSED, (KeyEvent kevent) -> {
			if(zoomInKC.match(kevent)){
				zoomIn();
			}
			
			if(zoomOutKC.match(kevent)){
				zoomOut();
			}
			
			if(saveKC.match(kevent)){
				saveFile();
			}
			
			if(assembleKC.match(kevent)){
				assembleCode();
			}
		});
		
		// TODO - dragable datapath
		
		stage.addEventHandler(ZoomEvent.ANY, (ZoomEvent event) -> {
			System.out.println("ZoomEvent: " + event.toString());
//			if(event.isControlDown()  &&  ZoomEvent.)
		});
		
		// TODO - bug in popover makes it that after closing window every popover 
		// stays above content on users screen (noover other applications), so rather
		// than letting the user manually close every popover, every popover is hidden
		stage.setOnHiding((t) -> {
			clearAllPopovers();
		});
		
		stage.setOnHidden((t) -> {
			clearAllPopovers();
		});
		
		stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, (WindowEvent event) -> {
			// check if user saved the code
			// and if user really wants to quit
			System.out.println("WindowEvent: " + event.toString());
			
			// shutdown the simulation thread
			try {
				simulationThread.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException ex) {
				Logger.getLogger(MainWindowController.class.getName()).log(Level.SEVERE, null, ex);
			}
			
			cleanupWhenDone.unsubscribe();
			
			// try to kill all threads, app was not closing all threads before
			for(Thread thrd : Thread.getAllStackTraces().keySet()){
				try {
					thrd.interrupt();
				} catch(Exception e) {}
			}
			
			System.out.println("\nGOODBYE !");
			Platform.exit();
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
			fileOperator.saveFile(stage, codeEditor.getText());
			markCodeChange(false);									// unmark code changes
		} catch(Exception e) { System.out.println(e.getMessage()); return; }
		
		markCodeChange(false);
	}
	
	// TODO set the filename
	public void saveFileAs(){
		try {
			fileOperator.saveFileAs(stage, codeEditor.getText());
		} catch(Exception e) { System.out.println(e.getMessage()); return; }
		
		markCodeChange(false);
	}
	
	/**
	 * When changing tabs from code tab to the 
	 * datapath tab, we need to hide instruction set 
	 * and reveal registers.
	 */
	public void changeTabToCode(){
		registersTable.setVisible(false);
		instructionVbox.setVisible(true);
	}
	
	public void changeTabToDatapath(){
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
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                    matcher.group("NUMMERO") != null ? "nummero" :
					matcher.group("LABEL")   != null ? "code_label" :
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
			stopSimulation();	// reset simulation to its original state
		}
		catch(SyntaxException errors){
			assembleButton.setStyle("-fx-background-color: #ff9980");
			displayErrors(errors.getErrors());
		}
		
		defaultButtonTimer.schedule(new TimerTask(){
			@Override
			public void run() {
				assembleButton.setStyle("-fx-background-color: #e8ebe9;");
			}			
		}, 2000);
	}
	
	/**
	 * Creates a new popup window, which will display set of errors
	 * for user.
	 * @param errors 
	 */
	private void displayErrors(List<Tuple<Integer, String>> errors) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/catheaven/simmips/ErrorList.fxml"));
      
		Parent root;
		
		try {
			root = loader.load();
			loader.<ErrorWindowController>getController().setData(errors);
		} catch(IOException e) { 
			logger.log(Level.SEVERE, "Failed to create Error Window !\n{0}", e.getMessage()); 
			return; 
		}
		
        Scene scene = new Scene(root);
		Stage nstage = new Stage();
		nstage.setTitle("Errors");
        nstage.setScene(scene);
        nstage.show();
	}
	
	/**
	 * Starts the simulation and plays until the user pauses the simulation
	 * or the program quits.
	 */
	public void playSimulation(){
		if( currentSimulation != null  &&  ! currentSimulation.isDone())
			return;
		
		tabPane.getSelectionModel().select(datapathTab);	// display the cpu datapath (QoL feature)
		changeTabToDatapath();
		
		// start the simulation
		currentSimulation = simulationThread.scheduleAtFixedRate(
				() -> { executeStep(); },
				100,		// initial delay
				normalSimulationPeriod,
				TimeUnit.MILLISECONDS
		);
		
		// adjust buttons
		playSimulationButton.setDisable(true);
		playFastSimulationButton.setDisable(false);
		pauseSimulationButton.setDisable(false);
		resetSimulationButton.setDisable(false);
		stepSimulationButton.setDisable(true);
	}
	
	public void playFastSimulation(){
		// if attempt to stop current simulation fails
		if(stopCurrentSimulationThread() == false)
			return;
			
		tabPane.getSelectionModel().select(datapathTab);
		changeTabToDatapath();
		
		currentSimulation = simulationThread.scheduleAtFixedRate(
				() -> { executeStep(); },
				100,		// initial delay
				quickSimulationPeriod,
				TimeUnit.MILLISECONDS
		);
		
		// adjust buttons
		playSimulationButton.setDisable(false);
		playFastSimulationButton.setDisable(true);
		pauseSimulationButton.setDisable(false);
		resetSimulationButton.setDisable(false);
		stepSimulationButton.setDisable(true);
	}
	
	/**
	 * Execute one cycle. If the simulation is paused, continues with
	 * next step. This method is not available, if there is simulation
	 * already running in the background.
	 */
	public void stepSimulation(){
		// This results in a 'pause' behaviour, rather then 'pause & step', because
		// that may be too quick for the user to notice
		if( ! currentSimulation.isDone()) {
			stopCurrentSimulationThread();
			return;
		}
		
		tabPane.getSelectionModel().select(datapathTab);
		changeTabToDatapath();
	
		System.out.println("one step simulation");
		executeStep();
		
		// adjust buttons
		playSimulationButton.setDisable(false);
		playFastSimulationButton.setDisable(false);
		pauseSimulationButton.setDisable(true);
		resetSimulationButton.setDisable(false);
		stepSimulationButton.setDisable(false);
	}
	
	/**
	 * Pauses simulation. After that, the simulation can be played again, 
	 * but will continue from the point it paused in.
	 */
	public void pauseSimulation(){
		stopCurrentSimulationThread();
		
		// adjust buttons
		playSimulationButton.setDisable(false);
		playFastSimulationButton.setDisable(false);
		pauseSimulationButton.setDisable(true);
		resetSimulationButton.setDisable(false);		// we can reset whole datapath even when paused
		stepSimulationButton.setDisable(false);
	}
	
	/**
	 * Stops any simulation currently running. Possible to use even
	 * when no simulation is running, because it will reset data of 
	 * every component to the original state (before the simulation).
	 * <b>Does not</b> clear the program loaded by assembling.
	 */
	public void stopSimulation(){		
		stopCurrentSimulationThread();
		
		currentPhaseLimit = 1;
		
		for(Component c : cpu.getComponents()){
			c.reset();
			c.notifySubs();
		}
		
		for(Connector c : cpu.getWires()){
			c.setColor(null);
		}
		
		datapathLabeler.clear();
		
		playSimulationButton.setDisable(false);
		playFastSimulationButton.setDisable(false);
		pauseSimulationButton.setDisable(true);
		resetSimulationButton.setDisable(true);
		stepSimulationButton.setDisable(false);		
	}
	
	/**
	 * Initializes graphical datapath by adding every graphical component
	 * and setting up event listeners. Graphical components added are
	 * wires (Connector objects) and components (ComponentShape objects).
	 */
	private void initDatapath(){
		for(Connector c : cpu.getWires()){
			c.prepareSub();			// create popover and listener for mouse-press
			datapathPane.getChildren().addAll(c.getLine(), c.getClickLine());
			datapathNodes.add(c);
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
			
			ComponentShape cs = new ComponentShape(c, shape);
			
			// data memory has its own window, popover is not necessary
			if( ! (c instanceof DataMemory))
				cs.prepareSub();		// creates a popover
			
			datapathPane.getChildren().add(shape);
			datapathNodes.add(cs);
			
			if(c instanceof AND){
				datapathLabeler.setSourceComponent(c);
				c.registerSub(datapathLabeler);
			}
			
			if(c instanceof RegBank){
				regTable = new RegTable(registersTable, regIndexColumn, regValueColumn, (RegBank) c);
				c.registerSub(regTable);
			}
			if(c instanceof DataMemory){
				shape.setOnMousePressed( (MouseEvent eh) -> {
					if(eh.isPrimaryButtonDown()  &&  eh.getClickCount() != 2)
						return;
					
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/catheaven/simmips/DataMemory.fxml"));
					DataTable dt = new DataTable((DataMemory) c);
					loader.setController(dt);
					c.registerSub(dt);
					
					Parent root;
					try {
						//loader.<DataTable>getController().setData((DataMemory) c);
						root = loader.load();
					} catch(IOException e) { 
						e.printStackTrace();
						System.out.println(e.getMessage());
						logger.log(Level.SEVERE, "Failed to create Data Memory Window !\n{0}", e.getMessage()); 
						return; 
					}

					Scene scene = new Scene(root);
					Stage nstage = new Stage();
					nstage.setTitle("Data Memory");
					nstage.setScene(scene);
					nstage.show();
				});
			}	
		}
		
		// finaly adjust datapath
		//datapathControl.setTranslateX(-30);
		//datapathControl.setTranslateY(-10);
		datapathPane.setTranslateX(-50);
		datapathPane.setTranslateY(-50);
		adjustScaling();
	}
	
	/**
	 * Executes one single step in cpu. Servers as a helper method
	 * to not call separate methods bound to buttons, since they
	 * behave in relationship to each other. For example calling
	 * <code>stepSimulation()</code> from <code>playSimulation()</code>
	 * would stop the simulation.
	 */
	private void executeStep(){
		Platform.runLater(() -> {
			try {
				cpu.executeCycle();
			} catch(Exception e) { System.out.println(e.getMessage()); }

			String previousLatch = "";
			int phaseIndex = 0;
			int colorIndex = 0;

			for(Connector wire : cpu.getWires()){
				Component sc = wire.getSourceComponent();
				if(sc instanceof LatchRegister  &&   ! previousLatch.equals(sc.getLabel())){
					// dont paint the phase after this !
					// increase the number of phases you will have to
					// paint next time and finish for now
					if(++phaseIndex >= currentPhaseLimit){
						currentPhaseLimit++;		
						break;
					}

					// at this point we know, that this is a new phase we will paint,
					// so change the wire color 
					previousLatch = sc.getLabel();
					colorIndex++;
				}

				wire.setColor(colors.get(colorIndex%colors.size()));
			}
			colors.add(0, colors.remove(colors.size()-1));	// remove last and add it to the first place (new color)

			datapathLabeler.shiftLabels(cpu.getLastInstructionLabel());
		});
	}
	
	/**
	 * Attemps to stop current simulation running, if there is any.
	 * @return If any error occurred, returns false
	 */
	private boolean stopCurrentSimulationThread(){
		if( currentSimulation != null  &&  ! currentSimulation.isDone() )
			return currentSimulation.cancel(false); // allow it to finish its task TODO - test out
		
		// if there was no simulation or the simulation has finished, the thread is in correct (stopped) state
		return true;
	}
	
	/**
	 * Calls every datapath node to clear. Every node, which 
	 * displays content in form of a popover will hide it (if
	 * its not already hidden).
	 */
	public void clearAllPopovers(){
		for(Subscriber sub : datapathNodes)
			sub.clear();
	}
	
	/**
	 * Creates button tooltip. Ideal to set up global settings for tooltips.
	 * @param message Message to display on hover.
	 * @return Tooltip itself.
	 */
	private Tooltip createTooltip(String message){
		Tooltip ttip = new Tooltip(message);
		ttip.setShowDuration(Duration.seconds(1));
		ttip.setHideDelay(Duration.millis(200));
		return ttip;
	}
	
	/**
	 * Generates colors for simulation highlights.
	 * @return List of colors.
	 */
	private ArrayList<String> initColors(){
		ArrayList<String> colorsQ = new ArrayList();
		/*colorsQ.add("#eb4034");
		colorsQ.add("#eb34b1");
		colorsQ.add("#217eff");
		colorsQ.add("#21ffec");
		colorsQ.add("#21ff30");
		colorsQ.add("#d6ff21");
		colorsQ.add("#5a9e80");
		colorsQ.add("#6a00a3");
		colorsQ.add("#ba667d");
		colorsQ.add("#edb200");*/
		
		colorsQ.add("red");
		colorsQ.add("green");
		colorsQ.add("black");
		colorsQ.add("blue");
		colorsQ.add("grey");
		colorsQ.add("brown");
		
		return colorsQ;
	}
	
	/**
	 * Displays help window with basic app information.
	 * @throws IOException 
	 */
	public void displayHelp() throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/sk/catheaven/simmips/Help.fxml"));
		Parent root = (Parent) loader.load();
		
		Scene scene = new Scene(root);
		Stage hstage = new Stage();
		hstage.setScene(scene);
		hstage.show();
	}
	
	/**
	 * According to pane user is on, zooms in the whole view (either code or datapath).
	 */
	public void zoomIn(){
		System.out.println("zoom in");
		if(tabPane.getSelectionModel().getSelectedItem().equals(codeTab))
			codeFontSize++;
		else if(tabPane.getSelectionModel().getSelectedItem().equals(datapathTab))
			datapathScaleProperty += datapathScaleAmount;

		adjustScaling();
	}

	/**
	 * According to pane user is on, zooms out the whole view (either code or datapath).
	 */
	public void zoomOut(){
		System.out.println("zoom out");
		if(tabPane.getSelectionModel().getSelectedItem().equals(codeTab))
			codeFontSize--;
		else if(tabPane.getSelectionModel().getSelectedItem().equals(datapathTab))
			datapathScaleProperty -= datapathScaleAmount;

		if(datapathScaleProperty < 0) datapathScaleProperty = 0;
		if(codeFontSize <= 0) codeFontSize = 1;

		adjustScaling();
	}
	
	private void adjustScaling(){
		datapathPane.setScaleX(datapathScaleProperty);	// dont scale on X
		datapathPane.setScaleY(datapathScaleProperty);	// and scle down Y
		datapathPane.setScaleZ(datapathScaleProperty);	// and Z by a thin

		codeEditor.setStyle("-fx-font: " + codeFontSize + " Arial");
	}
	
	
}