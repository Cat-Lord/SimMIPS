package sk.catheaven.model.cpu.components;

import sk.catheaven.core.Data;
import sk.catheaven.model.cpu.ComponentImpl;
import sk.catheaven.service.IOHandler;

/**
 * Basic AND logical operation component.
 * @author catlord
 */
public class AND extends ComponentImpl {
    
    public Data getOutput(String outputLabel) {
        return IOHandler.getSingleValue(getOutputs());
    }
    
    public void execute() {
        Data output = this.getOutput(ComponentImpl.IGNORED_LABEL);
        Data inputA = IOHandler.getInputA(getInputs());
        Data inputB = IOHandler.getInputB(getInputs());
        
        for (Data input : getInputs().values())
            output.setData(
                inputA.getData() & inputB.getData()
            );
    }
    
}
