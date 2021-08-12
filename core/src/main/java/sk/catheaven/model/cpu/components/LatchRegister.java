package sk.catheaven.model.cpu.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.Component;

import java.util.List;
import java.util.Map;

/**
 * Represents intermediate storage between CPU phases. Is able to simply forward
 * provided inputs or do extraction via data ranges (bitwise shifting left-right
 * to extract numbers from provided input). For this reason there is a need to
 * define the input to output mapping.
 *
 * In case a latch register needs to be reset in in datapath a bubble signal is
 * used. When a bubble is activated, a counter is set and when it reaches 0, the
 * inputs are ignored and outputs are all 0. This is done in case of a jump
 * instruction. This is a way to handle data hazard.
 * @author catlord
 */
public class LatchRegister extends Component {
    private final static Logger log = LogManager.getLogger();
    private final static String BUBBLE_LABEL = "bubble";
    
    private Map<String, List<String>> inputsToOutputs;  // specifies which data input is bound to which data output
    private Data bubble = new Data(1);            // if branching takes place, we indicate it with counter and then reset all outputs to zero (no matter the input)
    
    // we set this to 1 and lower it every execution. If this
    // value gets to zero, set all outputs to 0.
    private int bubbleCounter = -1;
    
    @Override
    public void execute() {
        // if we skip, outputs are already cleared, so don't update them
        if (bubbleCounter != 0) {
            // lower bubble counter
            if(bubbleCounter > 0)
                bubbleCounter--;
    
            for (String inputLabel : inputsToOutputs.keySet()) {
                Data input = getInput(inputLabel);
        
                // write input value to the list of outputs - they know what to do with it
                for (String outputLabel : inputsToOutputs.get(inputLabel))
                    getOutput(outputLabel).setData(input);
            }
        }
        else {
            bubbleCounter = -1;
        
            log.info("Resetting outputs !");
        
            // clear the output, branching takes place
            for (Data output : getOutputs().values())
                output.setData(0);
        }
    }
    
    public Map<String, List<String>> getInputsToOutputs() {
        return inputsToOutputs;
    }
    
    public void setInputsToOutputs(Map<String, List<String>> inputsToOutputs) {
        this.inputsToOutputs = inputsToOutputs;
    }
    
    public Data getBubble() {
        return bubble;
    }
    
    public void setBubble(Data bubble) {
        this.bubble = bubble;
        getInputs().put(BUBBLE_LABEL, this.bubble);
    }
}
