package sk.catheaven.model.cpu.components;

import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.ComponentImpl;
import sk.catheaven.service.IOHandler;
/**
 * Single Input -> Single output component
 * Program counter, which stores and forwards address of next instruction to load.
 * @author catlord
 */
public class PC extends ComponentImpl {
    
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
        Data inputA = this.getInput(ComponentImpl.IGNORED_LABEL);
        
        this.getOutput(ComponentImpl.IGNORED_LABEL).setData(inputA);
    }
}
