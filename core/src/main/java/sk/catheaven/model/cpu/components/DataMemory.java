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
import sk.catheaven.utils.DataFormatter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Data storage unit. InputA represents target address (to read from or write to)
 * and inputB represent actual data. Only one of the control signals (memRead or
 * memWrite) can be active at one time, but inactivity of those signals is allowed.
 * Memory is efficiently stored in a map in a address (number) and data (Data) combination.
 * When a request to an unset memory is made (which means memory at address is null),
 * zero is returned. Otherwise data duplicate at that memory address is returned.
 * Memory can be in one of three states: inactive, memory read or memory write.
 * State when memory read signal and memory write signal is active is forbidden.
 * <p>
 * Calculation of address is complicated, because memory aligns data byte-wise. Although every cell
 * has a specific amount of bytes, storage does not suffer from this limitation and thus following
 * can occur in (i.e. 4-byte wide) memory cells:
 * <p>
 * number: XYZ (spread out in between address 00 and 04 because it got byte-aligned,
 * not address aligned)
 * <p>
 * address:         0x00                0x04
 * ___________________________________
 * content:     ;   |   |   | X  ; YZ |   |   |   ;
 * -----------------------------------
 * <p>
 *
 * todo - needs testing
 * @author catlord
 */
public class DataMemory extends Component {
    private static final Logger log = LogManager.getLogger();
    private static final String MEM_READ_SIGNAL = "memReadSignal";
    private static final String MEM_WRITE_SIGNAL = "memWriteSignal";
    
    private final Map<Integer, Data> memory = new HashMap<>();        // maps addresses represented as data to data values
    private Data memoryBlock = new Data(CPU.getBitSize());            // to avoid overflows, this will be container, which will manage input/output from memory
    private Data memReadSignal, memWriteSignal;
    
    // todo - validate and test
    @Override
    public void execute() {
        Data output = IOHandler.getSingleOutput(getOutputs());
        output.setData(0);
        
        if (memReadSignal.getData() == 1 && memWriteSignal.getData() == 1) {
            log.error("Inconsistent state, both control signals (read & write) are active !");
            return;
        }
        
        int bitWidth = 2;        // maximal number of bits used to differentiate between a memory cell and a following cell
        Data inputA = IOHandler.getInputA(getInputs());
        Data inputB = IOHandler.getInputB(getInputs());
        
        Data address = inputA.newInstance();
        Data nextAddress = inputA.newInstance();
        address.setData(inputA);
        nextAddress.setData(inputA);
        
        // contains the amount we need to shift data while having 4-byte alignment // todo -- - possible refactor, to allow not only 32 bit architecture
        Data dataShift = new Data(bitWidth);
        dataShift.setData(address.getData());
        
        // remove last 2 bits // todo - possible refactor, to allow not only 32 bit architecture
        address.setData(((address.getData() >>> bitWidth) << bitWidth));
        nextAddress.setData(address.getData() + (int) Math.pow(bitWidth, 2));             // todo - refactor
        
        log.debug("InputA:     {}" + DataFormatter.getHex(inputA));
        log.debug("InputB:     {}" + DataFormatter.getHex(inputB));
        log.debug("Address:    {}" + DataFormatter.getHex(address));
        log.debug("Next addr:  {}" + DataFormatter.getHex(nextAddress));
        log.debug("Data shift: {}" + DataFormatter.getHex(dataShift));
        
        if (memReadSignal.getData() == 1) {
            Data testBlock = memory.get(nextAddress.getData());        // first fetch data from following address
            
            output.setData(0);
            
            if (testBlock != null) {
                // first read the following block and shift left (to make room for next number
                memoryBlock.setData((testBlock.getData() << ((int) Math.pow(bitWidth, 2) - dataShift.getData())));
                
                // and then get the next part of the result (could be zero)
                testBlock = memory.get(address.getData());
                if (testBlock != null)
                    memoryBlock.setData(memoryBlock.getData() | (testBlock.getData() >>> dataShift.getData()));
                
                output.setData(memoryBlock.getData());
            }
            log.info("Reading from memory address 0x{}, got number 0x{}",
                    DataFormatter.getHex(address), DataFormatter.getHex(output));
            
        } else if (memWriteSignal.getData() == 1) {
            // write first memory block
            memoryBlock.setData(inputB.getData() << dataShift.getData());            // first perform possible bit size adjustment of the input
            memory.put(address.getData(), memoryBlock.newInstance());
            
            // write next memory block (if any)
            if (dataShift.getData() != 0) {
                memoryBlock.setData(inputB.getData() >>> ((int) Math.pow(bitWidth, 2) - dataShift.getData()));            // first perform possible bit size adjustment of the input
                memory.put(nextAddress.getData(), memoryBlock.newInstance());
            }
            
            // reset memory block for next usage
            memoryBlock.setData(0);
            log.info("Writing to memory address 0x{} value of {}",
                    DataFormatter.getHex(address), DataFormatter.getHex(inputB));
        } else
            log.info("Memory inactive");
    }
    
    public Data getMemReadSignal() {
        return memReadSignal;
    }
    
    public void setMemReadSignal(Data memReadSignal) {
        this.memReadSignal = memReadSignal;
        getInputs().put(MEM_READ_SIGNAL, this.memReadSignal);
    }
    
    public Data getMemWriteSignal() {
        return memWriteSignal;
    }
    
    public void setMemWriteSignal(Data memWriteSignal) {
        this.memWriteSignal = memWriteSignal;
        getInputs().put(MEM_WRITE_SIGNAL, this.memWriteSignal);
    }
    
    public void clearMemory() {
        memory.clear();
    }
}