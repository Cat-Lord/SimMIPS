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
	
	public InstructionMemory(String label, JSONObject json) {
		super(label);
		
		
		
		input = new Data(json.getInt("input"));
		output = new Data(json.getInt("output"));
		
		program = new ArrayList<>();			// ready to receive copied instructions, not the original list
	}
	
	/**
	 * Load a user program, that was assembled and contains no errors.
	 * @param program 
	 */
	public void setProgram(List<AssembledInstruction> program){
		if(this.program.addAll(program) == false)
			logger.log(Level.SEVERE, "Failed to load the program into instruction memoory !");
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
		
		// TODO - consider throwing an exception or handle empty instructions list (or dont ?)
		if(program.isEmpty()){
			
		}
			
		try {
			output.setData(program.get(index).getIcode().getData());
		} catch(IndexOutOfBoundsException e) {
			logger.log(Level.WARNING,  "Requesting instruction on address 0x" + input.getHex() + " (as index " + index + "), but address is OUT OF BOUNDS");
			output.setData(0);
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
	
	public String getStatus(){
		String s = "";
		s = s.concat(String.format(statusFormat, new Object[]{"Input", input.getHex()}));
		s = s.concat(String.format(statusFormat, new Object[]{"Output", output.getHex()}));
		return s;
	}
}
