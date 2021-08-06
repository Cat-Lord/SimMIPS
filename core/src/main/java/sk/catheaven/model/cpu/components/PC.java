package sk.catheaven.model.cpu.components;

import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.service.IOHandler;
/**
 * Single Input -> Single output component
 * Program counter, which stores and forwards address of next instruction to load.
 * @author catlord
 */
public class PC extends Component {
    
    @Override
    public Data getInput(String inputLabel) {
        return IOHandler.getSingleInput(getInputs());
    }
    
    @Override
    public Data getOutput(String outputLabel) {
        return IOHandler.getSingleOutput(getOutputs());
    }
    
    @Override
    public void execute() {
        Data inputA = this.getInput(Component.IGNORED_LABEL);
        
        this.getOutput(Component.IGNORED_LABEL).setData(inputA);
    }
}
