package sk.catheaven.model.cpu.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.core.Data;
import sk.catheaven.model.DataImpl;
import sk.catheaven.model.cpu.ComponentImpl;
import sk.catheaven.service.IOHandler;
import sk.catheaven.utils.DataFormatter;

import java.util.HashMap;
import java.util.Map;

/**
 * Data storage unit. Can store up to the CPU architecture bit size.
 * InputA represents target address (to read from or write to)
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
 * @author catlord
 */
public class DataMemory extends ComponentImpl {
    private static final Logger log = LogManager.getLogger();
    private static final String MEM_READ_SIGNAL = "memReadSignal";
    private static final String MEM_WRITE_SIGNAL = "memWriteSignal";
    private static final int BIT_WIDTH = calculateBitWidth();           // maximal number of bits used to differentiate between a memory cell and a following cell
    
    private final Map<Integer, Data> memory = new HashMap<>();        // maps addresses represented as data to data values
    private Data memReadSignal, memWriteSignal;
    
    private final Data memoryBlock = new DataImpl();                            // to avoid overflows, this will be container, which will manage input/output from memory
    private final Data baseAddress = new DataImpl();
    private final Data nextAddress = new DataImpl();
    private final Data addressShiftExtractor = new DataImpl(BIT_WIDTH);                // used to distribute input value between base and next address, see class-level javadoc
    
    // todo - validate and test
    @Override
    public void execute() {
        Data output = IOHandler.getSingleValue(getOutputs());
        output.setData(0);
        
        if (memReadSignal.getData() == 1 && memWriteSignal.getData() == 1) {
            log.error("Inconsistent state, both control signals (read & write) are active !");
            return;
        }
    
        Data inputAddress = IOHandler.getInputA(getInputs());
        Data inputValue   = IOHandler.getInputB(getInputs());
    
        baseAddress.setData(inputAddress);
        nextAddress.setData(inputAddress);
    
        // clear the last BIT_WIDTH bits
        baseAddress.setData( (baseAddress.getData() >>> BIT_WIDTH) << BIT_WIDTH );
        nextAddress.setData(baseAddress.getData() + CPUBase.getByteSize());
        
        addressShiftExtractor.setData(inputAddress); // will extract only value of BIT_WIDTH bit size
    
        log.debug("Input Value:  {}", DataFormatter.getHex(inputValue));
        log.debug("Address:      {}", DataFormatter.getHex(baseAddress));
        log.debug("Next address: {}", DataFormatter.getHex(nextAddress));
        log.debug("Shifting input by: {}", addressShiftExtractor.getData());
        
        // TODO - test correctness
        if (memReadSignal.getData() == 1) {
            Data nextAddressValue = memory.get(nextAddress.getData());        // first fetch data from following baseAddress
            
            if (nextAddressValue != null) {
                // first read the following block and shift left (to make room for next number)
                memoryBlock.setData(
                        (nextAddressValue.getData() <<
                                (CPUBase.BIT_SIZE - addressShiftExtractor.getData() * Byte.SIZE))
                );
            }
                
            // and then get the next part of the result (could be zero)
            Data baseAddressValue = memory.get(baseAddress.getData());
            if (baseAddressValue != null)
                memoryBlock.setData(
                        memoryBlock.getData() |
                                (baseAddressValue.getData() >>> addressShiftExtractor.getData() * Byte.SIZE)
                );
            
            output.setData(memoryBlock.getData());
            log.info("Reading from memory baseAddress 0x{}, got number 0x{}",
                    DataFormatter.getHex(baseAddress), DataFormatter.getHex(output));
        }
        else if (memWriteSignal.getData() == 1) {
            
            // write first memory block -> Shift by the amount of bytes it is required
            memoryBlock.setData(inputValue.getData() << addressShiftExtractor.getData() * Byte.SIZE);            // first perform possible bit size adjustment of the input
            memory.put(baseAddress.getData(), memoryBlock.newInstance());
            
            // write next memory block (if any)
            if (addressShiftExtractor.getData() != 0) {
                memoryBlock.setData(inputValue.getData() >>>
                        (CPUBase.getBitSize() - (addressShiftExtractor.getData() * Byte.SIZE))
                );
                memory.put(nextAddress.getData(), memoryBlock.newInstance());
            }
            
            // reset memory block for next usage
            memoryBlock.setData(0);
            log.info("Writing to memory baseAddress 0x{} value of {}",
                    DataFormatter.getHex(baseAddress), DataFormatter.getHex(inputValue));
        }
        else
            log.debug("Memory inactive");
    }
    
    @Override
    public Data getOutput(String outputLabel) {
        return IOHandler.getSingleValue(getOutputs());
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
    
    /**
     * Calculate by how many bits do we need to shift in order to get base address.
     * Example:
     *  4 byte alignment addresses: 0, 4, 8, 12, ...
     *  If we have address 10, our base address is 8
     *  How do we know that ?
     *
     *  10 in binary: 1010
     *   8 in binary: 1000
     *
     *   So by 'erasing' the last 2 bits we got our base address.
     *   But for a general architecture... how many bits do we need to shift with ?
     *
     *   Depends on our architecture.
     *
     *   32-bit architecture is a 4 byte architecture. We need to
     *   calculate 2^X = 4 (which is 2) and shift by this value.
     *
     *   For a 64-bit (8 byte) architecture this would be 3 and so on. We can help
     *   ourselves with a little bit of logarithm arithmetics.
     *
     *  2 to the power of what is our N-byte architecture ?
     *        2 ^ X = N
     *  X * log2(2) = log2(N)
     *        X * 1 = log2(N)
     *
     *  Logarithm base b of number N = log10(N) / log10(b)
     *  For logarithm of base 2 => log10(N) / log10(2)
     *
     * @return number of bits we need to shift left and right to get base address
     * from any number.
     */
    private static int calculateBitWidth() {
        return (int) (Math.log(CPUBase.getByteSize()) / Math.log(2));
    }
}