/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
	private enum NumberFormat {
		HEX, DEC, OCT
	};
	
	private static final int TABLE_LIMIT = 100;			// limits number of elements in table
	
	@FXML private TableView<Tuple<Integer, String>> dataTable;
	@FXML private TableColumn<Data, Integer> addressColumn;
	@FXML private TableColumn<Data, String> valueColumn;
	
	@FXML private TextField addressInput;
	@FXML private Label warning;
	
	@FXML private ChoiceBox numberFormatChoiceBox;
	
	private DataMemory sourceComponent;
	
	public DataTable(DataMemory sourceComponent){
		this.sourceComponent = sourceComponent;
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		this.addressColumn.setCellValueFactory(new PropertyValueFactory<>("left"));
		this.valueColumn.setCellValueFactory(new PropertyValueFactory<>("right"));
		
		this.dataTable.setPlaceholder(new Label("Out of memory bounds !"));
		
		// add the option to confirm action with enter key
		addressInput.addEventHandler(KeyEvent.KEY_TYPED, (KeyEvent kevent) -> {
			if(kevent.getCode() == KeyCode.ENTER){
				System.out.println("ENTER");
				filterTable();
			}
		});			
		
		// add all possible number formats into the choice box
		numberFormatChoiceBox.getItems().addAll(
			NumberFormat.HEX, NumberFormat.DEC, NumberFormat.OCT 
		);
		// and inform user of its intended usage
		numberFormatChoiceBox.setTooltip(new Tooltip("Select number format of address"));
		numberFormatChoiceBox.getSelectionModel().select(0);
		
		updateSub();
	}
	
	/**
	 * Gets the input from inputField and parses it as number.
	 * After that, tries to adjust the content of the data table
	 * to start from the given number as the base address.
	 */
	public void filterTable(){
		int address = 0;
		try { 
			NumberFormat format = (NumberFormat) numberFormatChoiceBox.getSelectionModel().getSelectedItem();
			System.out.println("Selected: " + format);
			if(format.equals(NumberFormat.HEX))
				address = Integer.parseUnsignedInt(addressInput.getText(), 16);
			if(format.equals(NumberFormat.DEC))
				address = Integer.parseUnsignedInt(addressInput.getText(), 10);
			if(format.equals(NumberFormat.OCT))
				address = Integer.parseUnsignedInt(addressInput.getText(), 8);
			
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
		
		addressInput.setText("");
		fillTable(address);
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
	
	/**
	 * With a given starting address fills the table with <code>TABLE_LIMIT</code> 
	 * number of elements, starting on <param>startingAddress</param>.
	 * @param startingAddress 
	 */
	private void fillTable(int startingAddress){
		dataTable.getItems().clear();
		int limit = startingAddress + TABLE_LIMIT;
		
		for(int i = startingAddress; i < limit; i++){
			Tuple<Integer, String> t = new Tuple(startingAddress, sourceComponent.getMemBlock(startingAddress).getHex());
			dataTable.getItems().add(t);
		}
	}
}
