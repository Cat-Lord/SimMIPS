package sk.catheaven.model.cpu.components;

import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.ComponentImpl;
import sk.catheaven.service.IOHandler;

/**
 * Single Input -> Single Output component
 * Adder, which adds a constant to the input. The constant is fixed after first 
 * loaded.
 * @author catlord
 */
public class ConstAdder extends ComponentImpl {
    private Data constant = new Data(CPUBase.getBitSize() / Byte.SIZE); // default size of constant
    
    @Override
    public Data getInput(String inputLabel) {
        return IOHandler.getSingleValue(getInputs());
    }
    
    @Override
    public Data getOutput(String outputLabel) {
        return IOHandler.getSingleValue(getOutputs());
    }
    
    @Override
    public void execute() {
        this.getOutput(ComponentImpl.IGNORED_LABEL).setData(
            this.getInput(ComponentImpl.IGNORED_LABEL).getData() + constant.getData()
        );
    }
    
    public Data getConstant() {
        return constant;
    }
    
    public void setConstant(Data constant) {
        this.constant = constant;
    }
}
