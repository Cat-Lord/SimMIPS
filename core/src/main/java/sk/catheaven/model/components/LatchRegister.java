package sk.catheaven.model.components;

import sk.catheaven.model.Data;
import sk.catheaven.model.Tuple;

import java.util.List;
import java.util.Map;

public class LatchRegister extends Component {
    private Map<String, List<String>> inputsToOutputs;  // specifies which data input is bound to which data output
    private Tuple<String, Data> bubble = new Tuple<>("EMPTY_BUBBLE_SIGNAL", new Data(1));
    
    public Map<String, List<String>> getInputsToOutputs() {
        return inputsToOutputs;
    }
    
    public void setInputsToOutputs(Map<String, List<String>> inputsToOutputs) {
        this.inputsToOutputs = inputsToOutputs;
    }
    
    public Tuple<String, Data> getBubble() {
        return bubble;
    }
    
    public void setBubble(Tuple<String, Data> bubble) {
        this.bubble = bubble;
    }
}
