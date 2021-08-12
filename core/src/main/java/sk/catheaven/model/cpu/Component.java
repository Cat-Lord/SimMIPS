package sk.catheaven.model.cpu;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sk.catheaven.model.Data;
import sk.catheaven.model.Style;
import sk.catheaven.model.cpu.components.ALU;
import sk.catheaven.model.cpu.components.AND;
import sk.catheaven.model.cpu.components.Adder;
import sk.catheaven.model.cpu.components.ConstAdder;
import sk.catheaven.model.cpu.components.ConstMUX;
import sk.catheaven.model.cpu.components.ControlUnit;
import sk.catheaven.model.cpu.components.DataMemory;
import sk.catheaven.model.cpu.components.Fork;
import sk.catheaven.model.cpu.components.InstructionMemory;
import sk.catheaven.model.cpu.components.LatchRegister;
import sk.catheaven.model.cpu.components.MUX;
import sk.catheaven.model.cpu.components.PC;
import sk.catheaven.model.cpu.components.RegBank;
import sk.catheaven.model.cpu.components.SignExtend;
import sk.catheaven.service.IOHandler;

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
public abstract class Component implements Executable {
	
	public static String IGNORED_LABEL = "";
	
	protected String label = "";
	private Style style;
	private String description = "";
	
	protected Map<String, Data> inputs = new HashMap<>();
	protected Map<String, Data> outputs = new HashMap<>();
	/**
	 * Selectors are input signals that somehow affect the component behaviour.
	 * At the point of setting selectors all of them are also added to the input map.
	 */
	protected Map<String, Data> selectors = new HashMap<>();
	
	
	/**
	 * If the component has only one input, sets it no matter the <i>targetLabel</i>. Otherwise tries to
	 * set the input pointed at by the <bold>targetLabel</bold> parameter and tries to store data.
	 * @param targetLabel The label of input we want to set the value to.
	 * @param data value to set to the target input.
	 * @return true if the output was found. False if no such input exists.
	 */
	@Override
	public boolean setInput(String targetLabel, Data data) {
		if (this.isSingleInputComponent()) {
			if (IOHandler.getSingleValue(getInputs()) != null) {
				IOHandler.getSingleValue(getInputs()).setData(data);
				return true;
			}
		}
		
		if (getInputs().get(targetLabel) != null) {
			getInputs().get(targetLabel).setData(data);
			return true;
		}
		
		if (getSelectors().get(targetLabel) != null) {
			getSelectors().get(targetLabel).setData(data);
			return true;
		}
		
		return false;
	}
	
	/**
	 * If the component has only one output, sets it no matter the <i>targetLabel</i>. Otherwise tries to
	 * set the output pointed at by the <bold>targetLabel</bold> parameter and tries to store data.
	 * @param targetLabel The label of output we want to set the value to.
	 * @param data value to set to the target output.
	 * @return true if the output was found. False if no such output exists.
	 */
	@Override
	public boolean setOutput(String targetLabel, Data data) {
		if (this.isSingleOutputComponent()) {
			if (IOHandler.getSingleValue(getOutputs()) != null) {
				IOHandler.getSingleValue(getOutputs()).setData(data);
				return true;
			}
		}
		
		if (getOutputs().get(targetLabel) != null) {
			getOutputs().get(targetLabel).setData(data);
			return true;
		}
		
		return false;
	}
	
	@Override
	public Data getInput(String inputLabel) {
		return getInputs().get(inputLabel);
	}
	
	@Override
	public Data getOutput(String outputLabel) {
		return getOutputs().get(outputLabel);
	}
	
	
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
		
		// selectors are special input
		if (inputs != null)
			inputs.putAll(selectors);
	}
	
	/**
	 * Having single input means that we are ignoring input label when setting/getting input.
	 * We have to subtract the amount of selectors we have, because those are only special
	 * kind of input.
	 * @return true when this component has only one single input
	 */
	public boolean isSingleInputComponent() {
		return (getInputs().size() - getSelectors().size()) == 1;
	}
	
	/**
	 * Having single output means that we are ignoring output label when setting/getting output
	 * @return true when this component has only one single output
	 */
	public boolean isSingleOutputComponent() {
		return getOutputs().size() == 1;
	}
	
	@Override
	public String toString() {
		return "Component {" +
				"label='" + label + '\'' +
				", style=" + style + System.lineSeparator() +
				", inputs=" + inputs.size() + " [ " + inputs.getClass().getName() + " ] " +
				", outputs=" + outputs.size() + " [ " + outputs.getClass().getName() + " ] " +
				", selectors=" + selectors.size() + " [ " + selectors.getClass().getName() + " ] " +
				'}';
	}
}
