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
import sk.catheaven.hardware.Component;
import sk.catheaven.hardware.RegBank;
import sk.catheaven.instructionEssentials.Data;

/**
 * Represents an object, which observes changes in register bank.
 * @author catlord
 */
public class RegTable implements Subscriber {
	private TableView<Tuple<Integer, String>> registersTable;
	private TableColumn<Data, Integer> regIndexColumn;
	private TableColumn<Data, String> regValueColumn;
	private RegBank sourceComponent;
	
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
		
	}

	@Override
	public void updateSub() {
		System.out.println("Updating table !");
		registersTable.getItems().clear();
		
		Data[] regs = sourceComponent.getRegisters();
		for(int i = 0; i < regs.length; i++){
			Tuple<Integer, String> t = new Tuple<>(i, regs[i].getHex());
			registersTable.getItems().add(t);
		}
		
		//registersTable.getSortOrder().add(regIndexColumn);
		registersTable.sort();
	}
	
}
