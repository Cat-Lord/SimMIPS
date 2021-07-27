/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.components;

import sk.catheaven.model.Data;
import sk.catheaven.utils.Style;

import java.util.Map;

public abstract class Component {
	
	protected String label;
	private Style style;
	private String symbol = "";
	private String type;
	private String description = "";
	
	private Map<String, Data> inputs;
	private Map<String, Data> outputs;
	private Map<String, Data> selectors;
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public Style getStyle() {
		return style;
	}
	
	public void setStyle(Style style) {
		this.style = style;
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Map<String, Data> getInputs() {
		return inputs;
	}
	
	public void setInputs(Map<String, Data> inputs) {
		this.inputs = inputs;
	}
	
	public Map<String, Data> getOutputs() {
		return outputs;
	}
	
	public void setOutputs(Map<String, Data> outputs) {
		this.outputs = outputs;
	}
	
	public Map<String, Data> getSelectors() {
		return selectors;
	}
	
	public void setSelectors(Map<String, Data> selectors) {
		this.selectors = selectors;
	}
}
