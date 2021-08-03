/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.components;

import sk.catheaven.model.Data;

import java.util.Map;

/**
 * Control unit which generates control signals. Maps instruction operation
 * code to output control signals. Has only one input, which is instruction 
 * in numeric representation and outputs one number which are simply 
 * signals concatenated into single output. These outputs are later forked
 * to get separate signals.
 * @author catlord
 */
public class ControlUnit extends Component {
    private Data opCode;
    private Data func;
    private String funcDependant;
    
    // MAPPINGS
    // TODO - 'controlCodes' needs to be linked` hash map in order to preserve insertion order (we need to access the signal labels in order)
    private Map<String, Data> controlCodes;
    private Map<Integer, Integer[]> opcodeToControlSignals;
    private Map<Integer, Integer> funcToALUOpCode;
    
    public Data getOpCode() {
        return opCode;
    }
    
    public void setOpCode(Data opCode) {
        this.opCode = opCode;
    }
    
    public Data getFunc() {
        return func;
    }
    
    public void setFunc(Data func) {
        this.func = func;
    }
    
    public String getFuncDependant() {
        return funcDependant;
    }
    
    public void setFuncDependant(String funcDependant) {
        this.funcDependant = funcDependant;
    }
    
    public Map<String, Data> getControlCodes() {
        return controlCodes;
    }
    
    public void setControlCodes(Map<String, Data> controlCodes) {
        this.controlCodes = controlCodes;
    }
    
    public Map<Integer, Integer[]> getOpcodeToControlSignals() {
        return opcodeToControlSignals;
    }
    
    public void setOpcodeToControlSignals(Map<Integer, Integer[]> opcodeToControlSignals) {
        this.opcodeToControlSignals = opcodeToControlSignals;
    }
    
    public Map<Integer, Integer> getFuncToALUOpCode() {
        return funcToALUOpCode;
    }
    
    public void setFuncToALUOpCode(Map<Integer, Integer> funcToALUOpCode) {
        this.funcToALUOpCode = funcToALUOpCode;
    }
}