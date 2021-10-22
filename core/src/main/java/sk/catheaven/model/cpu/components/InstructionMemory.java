package sk.catheaven.model.cpu.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.ComponentImpl;
import sk.catheaven.model.instructions.AssembledInstruction;
import sk.catheaven.service.IOHandler;
import sk.catheaven.utils.DataFormatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Instruction memory stores assembled code. It provides details about original line of code,
 * assembled instruction code and its multiple representations (binary, hex, decimal).
 * @author catlord
 */
public class InstructionMemory extends ComponentImpl {
    private static final Logger log = LogManager.getLogger();
    
    private final List<AssembledInstruction> program = new ArrayList<>();
    private int lastInstructionIndex;
    
    @Override
    public Data getInput(String inputLabel) {
        return IOHandler.getSingleValue(getInputs());
    }
    
    @Override
    public Data getOutput(String outputLabel) {
        return IOHandler.getSingleValue(getOutputs());
    }
    
    /**
     * From the given input (as address) compute the index, on which is the instruction
     * stored inside the list of assembled instructions. After this computation, set the
     * instruction code as output.
     * If the requested address yields in an out-of-bounds index, result instruction code
     * is zero.
     */
    @Override
    public void execute() {
        int index = 0; //Assembler.computeIndex(input);
        lastInstructionIndex = index;
    
        // TODO - consider throwing an exception or handle empty instructions list (or dont ?)
        if (program.isEmpty())
            log.warn("Executing instruction memory with NO program specified ! Returning empty data");
    
        Data input = this.getInput(ComponentImpl.IGNORED_LABEL);
        Data output = this.getOutput(ComponentImpl.IGNORED_LABEL);
        try {
            output.setData(program.get(index).getIcode());
            log.info("Setting output iCode, address of 0x{} resulting in index {}",
                    DataFormatter.getHex(input), index);
        }
        catch(IndexOutOfBoundsException e) {
            log.warn("Requesting instruction on address 0x{} (as index {}), but address is OUT OF BOUNDS",
                    DataFormatter.getHex(input), index);
            output.setData(0);
        }
    }
    
    /**
     * Allows to ask for the assembled instruction, which was set by last <code>execute</code>
     * call. If there is no such instruction (either program is not set or instruction
     * address is out of bounds), returns null.
     *
     * @return last instruction that was handled during execution or NOP instruction, if there wasn't any before.
     */
    public AssembledInstruction getLastInstruction(){
        try {
            return program.get(lastInstructionIndex);
        }
        catch(IndexOutOfBoundsException e) {
            return AssembledInstruction.getNOPInstructionInstance();
        }
    }
    
    /**
     * Load a user program into memory.
     * @param program User program already assembled and with no errors
     */
    public void setProgram(List<AssembledInstruction> program) {
        this.program.clear();
            
        if(this.program.addAll(program) == false)
            log.error("Failed to load the program into instruction memory !");
        else
            log.info("Program has been set ({} compiled instructions)", program.size());

    }
    
    public List<AssembledInstruction> getProgram() {
        return program;
    }
}