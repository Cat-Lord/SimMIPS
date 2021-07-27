/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.components;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sk.catheaven.model.Data;
import sk.catheaven.model.Style;

import java.util.HashMap;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
		@JsonSubTypes.Type(value = Adder.class),
		@JsonSubTypes.Type(value = ALU.class),
		@JsonSubTypes.Type(value = AND.class),
		@JsonSubTypes.Type(value = ConstAdder.class),
		@JsonSubTypes.Type(value = ConstMUX.class),
		@JsonSubTypes.Type(value = ControlUnit.class),
		@JsonSubTypes.Type(value = DataMemory.class),
		@JsonSubTypes.Type(value = Fork.class),
		@JsonSubTypes.Type(value = InstructionMemory.class),
		@JsonSubTypes.Type(value = LatchRegister.class),
		@JsonSubTypes.Type(value = MUX.class),
		@JsonSubTypes.Type(value = PC.class),
		@JsonSubTypes.Type(value = RegBank.class),
		@JsonSubTypes.Type(value = SignExtend.class),
})
public abstract class Component {
	
	protected String label = "";
	private Style style;
	private String description = "";
	
	private Map<String, Data> inputs = new HashMap<>();
	private Map<String, Data> outputs = new HashMap<>();
	private Map<String, Data> selectors = new HashMap<>();
	
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
	
	@Override
	public String toString() {
		return "Component {" +
				"label='" + label + '\'' +
				", style=" + style + System.lineSeparator() +
				", description='" + description + '\'' +
				", inputs=" + inputs.size() +
				", outputs=" + outputs.size() +
				", selectors=" + selectors.size() +
				'}';
	}
}
