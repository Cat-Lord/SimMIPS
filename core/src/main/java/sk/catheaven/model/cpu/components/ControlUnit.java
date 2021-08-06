/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.cpu.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.service.IOHandler;

import java.util.Map;

/**
 * Control unit which generates control signals. Maps instruction operation
 * code to output control signals. Has only one input, which is instruction 
 * in numeric representation and outputs one number which are simply 
 * signals concatenated into single output. These outputs are later forked
 * to get separate signals.
 * Example:
 *      Signals (with their specific bit sizes):
 *          "A" (bitSize 3), "B" (bitSize 5)
 *      Values:
 *          1: 5, 27
 *          2: 3, 8
 *
 *      So if we get opCode 1, we set our control signals accordingly:
 *          A = 5   (binary: 101)
 *          B = 27  (binary: 11011)
 *      and output gets concatenated in binary like this: 101 11011
 * @author catlord
 */
public class ControlUnit extends Component {
    private Logger log = LogManager.getLogger();
    
    private Data opCode;
    private Data func;
    private String funcDependant;
    
    // MAPPINGS
    private Map<String, Data> controlSignal;                 // order matters -> needs to be linked hash map
    private Map<Integer, Integer[]> opcodeToControlSignals;
    private Map<Integer, Integer> funcToALUOpCode;
    
    @Override
    public void execute() {
        parseInput();
    
        assignValuesToSignals();
        
        // adjust the funcDependant field according to 'funcToALUOpCode' mapping
        if (opCode.getData() == 0) {
            Data funcDependant = controlSignal.get(this.funcDependant);
            
            if (funcDependant == null)
                log.error("Func dependant field is null");
            else
                funcDependant.setData(funcToALUOpCode.get(func.getData()));
        }
        
        setOutputValue();
    }
    
    private void setOutputValue() {
        Data output = this.getOutput(Component.IGNORED_LABEL);
        
        // sanity set, prepare output 'default' value
        output.setData(0);
    
        // shift value of output bitwise left and 'append new value' of every control signal
        for (Data controlSignal : controlSignal.values())
            output.setData(
                    ( (output.getData() << controlSignal.getBitSize()) | controlSignal.getData() )
            );
    }
    
    /**
     * Get input and let the Data variables parse this input and extract necessary values.
     */
    private void parseInput() {
        Data input = this.getInput(Component.IGNORED_LABEL);
    
        opCode.setData(input);
        func.setData(input);
    }
    
    /**
     * According to opCode gets the array of respective values and
     * sets these values to every signal.
     */
    private void assignValuesToSignals() {
        Integer[] signalValues = opcodeToControlSignals.get(opCode.getData());
        Data[] signals = controlSignal.values().toArray(Data[]::new);
    
        if (signals.length != signalValues.length)
            log.error("Number of control codes differs from respective values ! {} code != {} values",
                    signals.length, signalValues.length);
    
        // prepare all output values
        for (int i = 0; i < signals.length; i++)
            signals[i].setData(signalValues[i]);
    }
    
    @Override
    public Data getInput(String inputLabel) {
        return IOHandler.getSingleInput(getInputs());
    }
    
    @Override
    public Data getOutput(String outputLabel) {
        return IOHandler.getSingleOutput(getOutputs());
    }
    
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
    
    public Map<String, Data> getControlSignal() {
        return controlSignal;
    }
    
    public void setControlSignal(Map<String, Data> controlSignal) {
        this.controlSignal = controlSignal;
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