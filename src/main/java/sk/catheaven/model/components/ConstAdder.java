/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.components;

import sk.catheaven.model.Data;

/**
 * Adder, which adds a constant to the input. The constant is fixed after first 
 * loaded from constructor (in json).
 * @author catlord
 */
public class ConstAdder extends Component {
    private Data constant;
    
    public Data getConstant() {
        return constant;
    }
    
    public void setConstant(Data constant) {
        this.constant = constant;
    }
}
