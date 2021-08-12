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
 * MUX with one input.The second input is fixed constant. This constant set from input json
 * from the constructor. <i>selector</i> defines, which output will be selected on action.
 * On default, the first input (<i>input</i> in this case) will be selected.
 * @author catlord
 */
public class ConstMUX extends Component {
    private Data constant = new Data(CPU.getBitSize() / Byte.SIZE); // default size of constant
    
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
        Data selector = IOHandler.getSingleSelector(getSelectors());
    
        this.getOutput(Component.IGNORED_LABEL).setData(
            (selector.getData() == 0) ? input : constant
        );
    }
        
    public Data getConstant() {
        return constant;
    }
    
    public void setConstant(Data constant) {
        this.constant = constant;
    }
}
