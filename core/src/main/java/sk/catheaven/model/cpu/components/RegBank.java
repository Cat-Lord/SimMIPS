package sk.catheaven.model.cpu.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.core.Data;
import sk.catheaven.model.DataImpl;
import sk.catheaven.model.cpu.ComponentImpl;

import java.util.Map;

/**
 * Double Input (+ 2 special inputs) -> Double Output component
 * Reg bank, which provides temporary storage for CPU calculations. Has multiple inputs and 
 * two outputs, as well as regWrite signal.
 * @author catlord
 */
public class RegBank extends ComponentImpl {
    private final static Logger log = LogManager.getLogger();
    private final static String REG_WRITE_SIGNAL_LABEL = "regWriteSignal";
    private final static String DEST_REG_LABEL = "destReg";
    private final static String DEST_REG_VALUE_LABEL = "destRegValue";
    
    private int dataBitSize = 0;                    // bit size of data stored in registers
    private int regCount;                           // number of registers
    private Data[] registers;		                // storage
    private Data regWriteSignal;
    private Data destReg;
    private Data destRegValue;
    
    // Input (as map key) has a value of register INDEX and the value, which is stored
    // in this register then gets propagated to the specific output (as map value).
    private Map<String, String> inputsToOutputs;
    
    /**
     * Propagates input to output: Input points to a register index
     * and output gets its value. Example for one input->output pair:
     *        Input.getData(): 14
     * Register[14].getData(): 622
     *                 Output: output.setData(622)
     *
     * If write signal si active, writes to register pointed at by destReg
     * the value of destRegValue.
     */
    @Override
    public void execute() {
        if (getRegWriteSignal().getData() == 1) {
        
            // register with index 0 is read-only
            if (destReg.getData() != 0)
                registers[destReg.getData()].setData( destRegValue );
        }
        
        for (String inputLabel : inputsToOutputs.keySet()) {
            String outputLabel = inputsToOutputs.get(inputLabel);
    
            int registerIndex = getInput(inputLabel).getData();
    
            getOutput(outputLabel).setData(registers[registerIndex]);
        }
    }
    
    /**
     * Returns a copy of all registers.
     * @return copy of all registers.
     */
    public Data[] getRegisters() {
        Data[] registers = new Data[this.registers.length];
        for (int i = 0; i < this.registers.length; i++)
            registers[i] = this.registers[i].newInstance();
        return registers;
    }
    
    public int getDataBitSize() {
        return dataBitSize;
    }
    
    public void setDataBitSize(int dataBitSize) {
        this.dataBitSize = dataBitSize;
        
        // if registers exist, set each of them
        if (registers != null)
            initRegisters();
    }
    
    public int getRegCount() {
        return regCount;
    }
    
    public void setRegCount(int regCount) {
        this.regCount = regCount;
        this.registers = new Data[regCount];
        
        // if we know how much bits does each register have, set it
        if (dataBitSize != 0)
            initRegisters();
    }

    @Override
    public void setInputs(Map<String, Data> inputs) {
        super.setInputs(inputs);
    }
    
    public Data getRegWriteSignal() {
        return regWriteSignal;
    }
    
    public void setRegWriteSignal(Data regWriteSignal) {
        this.regWriteSignal = regWriteSignal;
        getInputs().put(REG_WRITE_SIGNAL_LABEL, regWriteSignal);
    }
    
    public Data getDestReg() {
        return destReg;
    }
    
    public void setDestReg(Data destReg) {
        this.destReg = destReg;
        getInputs().put(DEST_REG_LABEL, destReg);
    }
    
    public Data getDestRegValue() {
        return destRegValue;
    }
    
    public void setDestRegValue(Data destRegValue) {
        this.destRegValue = destRegValue;
        getInputs().put(DEST_REG_VALUE_LABEL, destRegValue);
    }
    
    public Map<String, String> getInputsToOutputs() {
        return inputsToOutputs;
    }
    
    public void setInputsToOutputs(Map<String, String> inputsToOutputs) {
        this.inputsToOutputs = inputsToOutputs;
        
        for (String key : inputsToOutputs.keySet())
            if (getInputs().get(key) == null)
                log.error("Input unavailable: {}", key);
            
        for (String value : inputsToOutputs.values())
            if (getOutputs().get(value) == null)
                log.error("Output unavailable: {}", value);
    }
    
    
    private void initRegisters() {
        for (int i = 0; i < regCount; i++)
            registers[i] =  new DataImpl(dataBitSize);
    }
}
