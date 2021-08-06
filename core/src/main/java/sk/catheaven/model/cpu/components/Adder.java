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
 * Multi Input -> Single output component
 * Basic mathematical adder. Used mostly for offset (branch) calculations. Supports unlimited amount of inputs and
 * always gives single output value with respect to bit size of the output.
 * @author catlord
 */
public class Adder extends Component {
    
    @Override
    public Data getOutput(String outputLabel) {
        return IOHandler.getSingleOutput(getOutputs());
    }
    
    @Override
    public void execute() {
        Data output = this.getOutput(Component.IGNORED_LABEL);
        
        for (Data input : getInputs().values())
            output.setData(
                output.getData() + input.getData()
            );
    }
}
