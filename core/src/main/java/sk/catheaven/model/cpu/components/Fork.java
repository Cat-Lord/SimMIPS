/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.cpu.components;

import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.Component;
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
public class Fork extends Component {
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
        for (Data output : getOutputs().values()) {
            // every output knows how to handle the input because
            // they have specific range which they apply to input
            // see the method:   Data.getData()
            output.setData(this.getInput(Component.IGNORED_LABEL));
        }
    }
}