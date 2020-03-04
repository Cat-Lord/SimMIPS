/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utils;

import java.util.List;

/**
 * Specifies the field of an instruction. A field has specific bitSize and a label.
 * @author catlord
 */
public class Field {
	private final String label;
	private final int bitSize;
	
	public Field(String label, int bitSize){
		this.label = label;
		this.bitSize = bitSize;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the bitSize
	 */
	public int getBitSize() {
		return bitSize;
	}
}
