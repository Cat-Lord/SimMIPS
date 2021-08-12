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
 * Basic AND logical operation component.
 * @author catlord
 */
public class AND extends Component {
    
    public Data getOutput(String outputLabel) {
        return IOHandler.getSingleValue(getOutputs());
    }
    
    public void execute() {
        Data output = this.getOutput(Component.IGNORED_LABEL);
        Data inputA = IOHandler.getInputA(getInputs());
        Data inputB = IOHandler.getInputB(getInputs());
        
        for (Data input : getInputs().values())
            output.setData(
                inputA.getData() & inputB.getData()
            );
    }
    
}
