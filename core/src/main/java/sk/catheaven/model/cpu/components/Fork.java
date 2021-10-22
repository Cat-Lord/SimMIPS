package sk.catheaven.model.cpu.components;

import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.ComponentImpl;
import sk.catheaven.service.IOHandler;

/**
 * Single Input -> Single Output component
 * Forks the input value into exactly multiple output values. It is expected
 * to fork a single input value into two output values, but not required/constrained.
 * Input json file is expected to define output array of values. If this array 
 * contains only a single value, it behaves like an exact copy of the input value. 
 * Meaning requesting the value with <code>getOutput("value")</code> would just copy 
 * result of only one value and duplicate it.
 * Specifying multiple input values will result in multiple output values on 
 * each separate request.
 * @author catlord
 */
public class Fork extends ComponentImpl {
    @Override
    public boolean setInput(String targetLabel, Data data) {
        if (IOHandler.getSingleValue(getInputs()) == null)
            return false;
        
        IOHandler.getSingleValue(getInputs()).setData(data);
        return true;
    }
    
    @Override
    public Data getInput(String inputLabel) {
        return IOHandler.getSingleValue(getInputs());
    }
    
    @Override
    public void execute() {
        for (Data output : getOutputs().values()) {
            // every output knows how to handle the input because
            // they have specific range which they apply to input
            // see the method:   Data.getData()
            output.setData(this.getInput(ComponentImpl.IGNORED_LABEL));
        }
    }
}