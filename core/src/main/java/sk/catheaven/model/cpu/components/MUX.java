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
 * Double Input -> Single Output component.
 * Selector describes the component signal, that is selecting the output value.
 * If the selector is inactive (meaning its value is 0), the output is inputA.
 * If the value of the selector is 1, output is set to inputB.
 * @author catlord
 */
public class MUX extends Component {
    
    @Override
    public Data getOutput(String outputLabel) {
        return IOHandler.getSingleInput(getOutputs());
    }
    
    @Override
    public void execute() {
        Data selector = IOHandler.getSingleSelector(getSelectors());
        Data inputA = IOHandler.getInputA(getInputs());
        Data inputB = IOHandler.getInputB(getInputs());
        
        this.getOutput(Component.IGNORED_LABEL).setData(
            (selector.getData() == 0) ? inputA : inputB
        );
    }
}