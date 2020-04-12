/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionEssentials;

/**
 * The same as data, but provides the ability to label it.
 * @author catlord
 */
public class DataContainer {
	private final String label;
	private final Data data;
	
	public DataContainer(String label, int bitSize){
		this.label = label;
		this.data = new Data(bitSize);
	}
	
	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the data
	 */
	public Data getData() {
		return data;
	}
	
}
