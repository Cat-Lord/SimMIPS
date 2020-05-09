/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

import javafx.scene.control.Label;
import sk.catheaven.hardware.Component;

/**
 * Takes care of labeling instructions which are currently in the 
 * datapath. If a branch was taken, displays first three instructions
 * as inactive (and displays them like this every cycle of execution
 * until they are properly "flushed"). 
 * Explanation: Branch is taken when AND gate determines two input 
 * signals as active. This happens in execution phase. Execution
 * phase is fourth in order, so we need to disable first three
 * instructions. Next cycle, we enable first and disable instructions
 * two to four, etc.
 * @author catlord
 */
public class DatapathLabelManager implements Subscriber {
	private final Label[] labels;
	
	// indicates current positions in labels array, where to start disabling 
	// instructions (from inactive indicator + 1 up to max 3 labels)
	private int inactiveIndicator = -1;
	boolean inactiveLabels = false;
	
	Component sourceComponent;
	
	public DatapathLabelManager(
			Label IFI,
			Label IDI,
			Label EXI,
			Label MEMI,
			Label WBI,
			Component sourceComponent
		) {
		labels = new Label[]{ IFI, IDI, EXI, MEMI,WBI };
		this.sourceComponent = sourceComponent;
	}	
	
	public void shiftLabels(String newInstruction){
		for(int i = labels.length - 1; i > 0; i--)
			labels[i].setText(labels[i-1].getText());
		
		labels[0].setText(newInstruction);
		
		if(inactiveLabels){
			inactiveLabels = false;
			inactiveIndicator = 0;
			for(int i = 0; i < 3; i++)
				deactivateLabel(labels[i]);
			return;
		}
		
		if(inactiveIndicator >= 0){
			// if this isn't true, we just catched the branching process 
			// and will disable first three labels
			activateLabel(labels[inactiveIndicator]);		// this label will get changed, activate it
			
			inactiveIndicator++;							// and "shift" the chain

			int limit = inactiveIndicator + 3;
			
			// deactivate next three (or less) instruction labels
			for(int j = inactiveIndicator; (j < labels.length)  &&  (j < limit);  j++)
				deactivateLabel(labels[j]);

		}
		
		if(inactiveIndicator >= labels.length)
			inactiveIndicator = -1;
	}

	/**
	 * We check, if the new value is 1 (active branch), and if it is, we
	 * mark labels as inactive
	 */
	@Override
	public void updateSub() {
		// if we are taking branch
		if(sourceComponent.getOutput("").getData() == 1)
			inactiveLabels = true;
	}

	@Override
	public void clear() {
		inactiveLabels = false;
		inactiveIndicator = -1;
		
		for(int i = 0; i < labels.length; i++){
			labels[i].setText("");
			activateLabel(labels[i]);
		}
	}
		
	@Override
	public void prepareSub() {
		
	}
	
	private void activateLabel(Label label){
		label.setStyle("-fx-text-inner-color: black;");
		label.setStyle("-fx-opacity: 1;");
	}
	
	private void deactivateLabel(Label label){
		label.setStyle("-fx-text-inner-color: #d4d4d4;");
		label.setStyle("-fx-opacity: 0.5;");
	}
	
}
