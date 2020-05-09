/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.AssembledInstruction;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.instructionEssentials.Data;

/**
 * Instruction memory stores assembled code. It provides details about original line of code,
 * assembled instruction code and its multiple representations (binary, hex, decimal).
 * @author catlord
 */
public class InstructionMemory extends Component {
	private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private final Data input, output;
	private final List<AssembledInstruction> program;
	private int lastInstructionIndex;
	
	public InstructionMemory(String label, JSONObject json) {
		super(label, json);
		
		input = new Data(json.getInt("input"));
		output = new Data(json.getInt("output"));

		program = new ArrayList<>();			// ready to receive copied instructions, not the original list
	}
	
	/**
	 * Load a user program, that was assembled and contains no errors.
	 * @param program 
	 */
	public void setProgram(List<AssembledInstruction> program){
		clearProgram();
		
		if(this.program.addAll(program) == false)
			logger.log(Level.SEVERE, "Failed to load the program into instruction memoory !");
		else
			logger.log(Level.INFO, "Program has been set (" + program.size() + " compiled instructions)");
	}
	
	/**
	 * Clears the whole program. As a result, list of assembled instruction is 
	 * empty after this method call.
	 */
	public void clearProgram(){
		program.clear();
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
		int index = Assembler.computeIndex(input);
		lastInstructionIndex = index;
		
		// TODO - consider throwing an exception or handle empty instructions list (or dont ?)
		if(program.isEmpty()){
			logger.log(Level.WARNING, "Executing instruction memory with NO program specified ! Returning empty data");
		}
			
		try {
			output.setData(program.get(index).getIcode().getData());
			logger.log(Level.INFO, "Setting output iCode, address of 0x{0} resulting in index {1}", new Object[]{input.getHex(), index});
		} catch(IndexOutOfBoundsException e) {
			logger.log(Level.WARNING, "Requesting instruction on address 0x{0} (as index {1}), but address is OUT OF BOUNDS", new Object[]{input.getHex(), index});
			output.setData(0);
		}
		
		notifySubs();
	}

	/**
	 * Allows to ask for the assembled instruction , which was set by last <code>execute</code>
	 * call. If there is no such instruction (either program is not set or instruction 
	 * address is out of bounds), returns null.
	 * @return 
	 */
	public AssembledInstruction getLastInstruction(){
		try {
			AssembledInstruction ai = program.get(lastInstructionIndex);
			return ai;
		} catch(IndexOutOfBoundsException e) {
			return createNop();
		}
	}
	
	@Override
	public Data getOutput(String selector) {
		return output.duplicate();
	}

	@Override
	public boolean setInput(String selector, Data data) {
		input.setData(data.getData());
		return true;
	}
	
	@Override
	public String getStatus(){
		String s = "";
		s = s.concat(String.format(statusFormat, new Object[]{"Input", input.getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{"Output", output.getHex()}));
		return s;
	}

	@Override
	public Data getInput(String selector) {
		return input.duplicate();
	}

	@Override
	public void reset() {
		input.setData(0);
		output.setData(0);
	}
	
	private AssembledInstruction createNop(){
		Data out = output.duplicate();
		out.setData(0);
		return new AssembledInstruction(0, null, "Nop", out, out);
	}
}
