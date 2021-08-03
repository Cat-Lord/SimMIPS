/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.catheaven.hardware.RegBank;
import sk.catheaven.instructionEssentials.Data;

/**
 * Represents an object, which observes changes in register bank.
 * @author catlord
 */
public class RegTable implements Subscriber {
	private final TableView<Tuple<Integer, String>> registersTable;
	private final TableColumn<Data, Integer> regIndexColumn;
	private final TableColumn<Data, String> regValueColumn;
	private final RegBank sourceComponent;
	
	public RegTable(
			TableView<Tuple<Integer, String>> registersTable, 
			TableColumn<Data, Integer> regIndexColumn,
			TableColumn<Data, String> regValueColumn,
			RegBank sourceComponent
		){
		
		this.registersTable = registersTable;
		this.regIndexColumn = regIndexColumn;
		this.regValueColumn = regValueColumn;
		
		this.registersTable.setPlaceholder(new Label("\tRegister Bank not working !"));
		
		this.regIndexColumn.setCellValueFactory(new PropertyValueFactory<>("left"));
		this.regValueColumn.setCellValueFactory(new PropertyValueFactory<>("right"));
		
		this.sourceComponent = sourceComponent;
		
		updateSub();
	}
	
	@Override
	public void prepareSub() {
		// nothing to prepare for this subscriber
	}

	@Override
	public void updateSub() {
		registersTable.getItems().clear();
		
		Data[] regs = sourceComponent.getRegisters();
		for(int i = 0; i < regs.length; i++){
			Tuple<Integer, String> t = new Tuple<>(i, regs[i].getHex());
			registersTable.getItems().add(t);
		}
		
		//registersTable.getSortOrder().add(regIndexColumn);
		registersTable.sort();
	}
	
	@Override
	public void clear(){
		// doesnt have anything to clear
	}
	
}
