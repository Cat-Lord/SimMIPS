package sk.catheaven.model.cpu.components;

import sk.catheaven.core.Data;
import sk.catheaven.model.cpu.ComponentImpl;
import sk.catheaven.service.IOHandler;

/**
 * Multi Input -> Single output component
 * Basic mathematical adder. Used mostly for offset (branch) calculations. Supports unlimited amount of inputs and
 * always gives single output value with respect to bit size of the output.
 * @author catlord
 */
public class Adder extends ComponentImpl {
    
    @Override
    public Data getOutput(String outputLabel) {
        return IOHandler.getSingleValue(getOutputs());
    }
    
    @Override
    public void execute() {
        Data output = this.getOutput(ComponentImpl.IGNORED_LABEL);
        
        for (Data input : getInputs().values())
            output.setData(
                output.getData() + input.getData()
            );
    }
}
