/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.components;

import sk.catheaven.model.Data;

/**
 * MUX with one input.The second input is fixed constant. This constant set from input json
 * from the constructor. <i>selector</i> defines, which output will be selected on action.
 * On default, the first input (<i>input</i> in this case) will be selected.
 * @author catlord
 */
public class ConstMUX extends Component {
    private Data constant;
    
    public void execute() {
        // if selectors.get(0);  // todo - how
    }
        
        public Data getConstant() {
        return constant;
    }
    
    public void setConstant(Data constant) {
        this.constant = constant;
    }
}
