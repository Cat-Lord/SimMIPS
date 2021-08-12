/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.cpu.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.service.IOHandler;

/**
 * Single Input -> Single Output component
 * From the input sign-extends the output. If the input begins with a hexadecimal
 * value of F, output is extended with hexadecimal value F. Otherwise output is 
 * extended with 0's.
 * @author catlord
 */
public class SignExtend extends Component {
    private Logger log = LogManager.getLogger();
    
    // initial value of 0, so the bit difference doesn't make sense
    // and thus it is possible to see if it was already set or no.
    private int bitDifference = 0;
    
    @Override
    public boolean setInput(String targetLabel, Data data) {
        if (this.getInput(Component.IGNORED_LABEL) != null) {
            this.getInput(Component.IGNORED_LABEL).setData(data);
            return true;
        }
        return false;
    }
    
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
        Data input = this.getInput(Component.IGNORED_LABEL);
    
        this.getOutput(Component.IGNORED_LABEL).setData(
            // first shift to the left and then sign-shift to the right to preserve the sign
            ((input.getData() << getBitDifference()) >> getBitDifference())
        );
    
    }
    
    private int getBitDifference() {
        
        if (this.bitDifference == 0) {
            Data input = this.getInput(Component.IGNORED_LABEL);
            Data output = this.getOutput(Component.IGNORED_LABEL);
            
            this.bitDifference = Math.abs(input.getBitSize() - output.getBitSize());
            if (this.bitDifference == 0) {
                log.error("Bit difference must not be 0");
                this.bitDifference = 1;
            }
        }
        
        return this.bitDifference;
    }
}
