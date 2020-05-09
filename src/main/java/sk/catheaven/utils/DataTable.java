/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.catheaven.hardware.DataMemory;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.instructionEssentials.Data;

/**
 * Controller, which handles the data memory window. Allows user to see a limited number 
 * of data memory values. If the user needs to see certain memory cell or range of cells
 * they can enter a number into the input text field and the table will get updated.
 * @author catlord
 */
public class DataTable implements Initializable, Subscriber {
	private static final int TABLE_LIMIT = 100;			// limits number of elements in table
	
	@FXML private TableView<Tuple<Integer, String>> dataTable;
	@FXML private TableColumn<Data, Integer> addressColumn;
	@FXML private TableColumn<Data, String> valueColumn;
	
	@FXML private TextField addressInput;
	@FXML private Label warning;
	
	private DataMemory sourceComponent;
	
	public DataTable(DataMemory sourceComponent){
		
	}
	
	public void setData(DataMemory sourceComponent){
		this.sourceComponent = sourceComponent;
	}
	
	/**
	 * Gets the input from inputField and parses it as number.
	 * After that, tries to adjust the content of the data table
	 * to start from the given number as the base address.
	 */
	public void filterTable(){
		int address;
		try { 
			address = Integer.parseUnsignedInt(addressInput.getText());
		} catch(NumberFormatException e){
			warning.setVisible(true);
			Timer timer = new Timer();
			timer.schedule(new TimerTask()  {
				@Override
				public void run() {
					warning.setVisible(false);
				}
			}, 5000);
			return;
		}
		
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		this.addressColumn.setCellValueFactory(new PropertyValueFactory<>("left"));
		this.valueColumn.setCellValueFactory(new PropertyValueFactory<>("right"));
		
		this.dataTable.setPlaceholder(new Label("Data Memory not working !"));
		updateSub();
	}


	@Override
	public void updateSub() {
		fillTable(0);
	}

	@Override
	public void prepareSub() {
		// nothing to prepare
	}
	
	@Override
	public void clear() {
		// nothing to clear
	}
	
	private void fillTable(int startingAddress){
		dataTable.getItems().clear();
		
		Data[] memory = sourceComponent.getMemory();
		int startingIndex = Assembler.computeIndex(startingAddress);
		
		int limit = startingAddress + (memory.length - TABLE_LIMIT);
		
		for(int i = 0; i < TABLE_LIMIT; i++){
			if(i >= limit  ||  startingIndex + i >= memory.length)
				break;
			
			Tuple<Integer, String> t = new Tuple(Assembler.computeAddress(i + startingIndex), memory[i + startingIndex].getHex())	;
			dataTable.getItems().add(t);
		}
		
	}
	
}
