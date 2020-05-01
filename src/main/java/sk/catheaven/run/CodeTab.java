/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.util.logging.Logger;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.IntFunction;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.instructionEssentials.Assembler;

/**
 *
 * @author catlord
 */
public class CodeTab implements Initializable {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	@FXML HBox hbox;
	@FXML ScrollPane pane;
	@FXML Button assembleButton;
	
	private static final Color defaultButtonColor = Color.valueOf("#e8e8e8");
	private CodeArea ca;
	private Assembler assembler;
	
	public CodeTab() throws IOException, URISyntaxException {
		Loader l = new Loader("sk/catheaven/data/layout.json", "sk/catheaven/data/cpu.json");
		assembler = l.getCPU().getAssembler();
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		
		
		if(url == null){
			System.out.println("URL NULL");
		}
		else
			System.out.println("URL: " + url.getPath());
		if(rb == null){
			System.out.println("RB NULL");
		}
		else{
			System.out.println("Resource keys: ");
			for(String key : rb.keySet()){
				System.out.println("\t" + rb.getString(key));
			}
			System.out.println("Bundle: " + rb.getBaseBundleName());
		}
		
		try {
			this.ca = initHbox();
		} catch (Exception ex) {
			logger.log(Level.WARNING, ex.getMessage());
		}
		
		assembleButton.setBackground(new Background(new BackgroundFill(defaultButtonColor, CornerRadii.EMPTY, Insets.EMPTY)));
	}
	
	private CodeArea initHbox() throws Exception {
		if(this.hbox == null){
			throw new Exception("HBOX IS NULL !!!");
		}
		
		CodeArea codeArea = new CodeArea();
		
		IntFunction<Node> numberFactory = LineNumberFactory.get(codeArea);
		IntFunction<Node> arrowFactory = new ArrowFactory(codeArea.currentParagraphProperty());
		IntFunction<Node> graphicFactory = line -> {
			HBox innerHbox = new HBox(
				numberFactory.apply(line),
				arrowFactory.apply(line)
			);
			innerHbox.setAlignment(Pos.CENTER_LEFT);
			return innerHbox;
		};
        codeArea.setParagraphGraphicFactory(graphicFactory);
        codeArea.replaceText(";place for your code\n\n\n");
        codeArea.moveTo(0, 0);
		
		//boolean add = this.hbox.getChildren().add(codeArea);
		//System.out.println("Adding of new child: " + add);


		codeArea.setPrefSize(this.pane.getPrefHeight(), this.pane.getPrefWidth());
		this.pane.setContent(codeArea);

		return codeArea;
	}
	
	public void assemble() throws Exception {
		if(this.ca == null)
			throw new Exception("CodeArea not defined");
		
		String code = ca.getText();
		System.out.println(code);
		try{
			assembler.assembleCode(code);
			assembleButton.setBackground(new Background(new BackgroundFill(Color.LIGHTGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
		} catch(SyntaxException e){
			System.out.println(e.getMessage());
			assembleButton.setBackground(new Background(new BackgroundFill(Color.valueOf("#fd5858"), CornerRadii.EMPTY, Insets.EMPTY)));
		}
		
		new java.util.Timer().schedule( 
			new java.util.TimerTask() {
				@Override
				public void run() {
					assembleButton.setBackground(new Background(new BackgroundFill(defaultButtonColor, CornerRadii.EMPTY, Insets.EMPTY)));
				}
			}, 
			2800 
		);
	}
}
