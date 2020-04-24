/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.List;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.AssembledInstruction;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class InstructionMemory extends Component {
	private Data input, output;
	private List<AssembledInstruction> program;
	
	public InstructionMemory(String label, JSONObject json) {
		super(label);
		
		setupIO(json);
	}
	
	/**
	 * Load a user program, that was assembled and contains no errors.
	 * @param program 
	 */
	public void setProgram(List<AssembledInstruction> program){
		this.program = program;
	}

	@Override
	public void execute() {
		int index = Assembler.computeIndex(input);
	}

	private void setupIO(JSONObject json) {
		input = new Data(json.getInt("input"));
		output = new Data(json.getInt("output"));
	}
}
