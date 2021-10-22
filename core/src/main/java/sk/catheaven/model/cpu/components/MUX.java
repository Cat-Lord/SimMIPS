package sk.catheaven.model.cpu.components;

import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.ComponentImpl;
import sk.catheaven.service.IOHandler;

/**
 * Double Input -> Single Output component.
 * Selector describes the component signal, that is selecting the output value.
 * If the selector is inactive (meaning its value is 0), the output is inputA.
 * If the value of the selector is 1, output is set to inputB.
 * @author catlord
 */
public class MUX extends ComponentImpl {
    
    @Override
    public Data getOutput(String outputLabel) {
        return IOHandler.getSingleValue(getOutputs());
    }
    
    @Override
    public void execute() {
        Data selector = IOHandler.getSingleSelector(getSelectors());
        Data inputA = IOHandler.getInputA(getInputs());
        Data inputB = IOHandler.getInputB(getInputs());
        
        this.getOutput(ComponentImpl.IGNORED_LABEL).setData(
            (selector.getData() == 0) ? inputA : inputB
        );
    }
}