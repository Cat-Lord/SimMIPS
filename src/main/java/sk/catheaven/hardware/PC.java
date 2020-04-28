/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;

/**
 * Program counter, which stores and forwards address of next instruction to load.
 * @author catlord
 */
public class PC extends Component {
	private final Data input, output;
	
	public PC(String label, JSONObject json) {
		super(label);
		
		int bitSize = json.getInt("bitSize");
		
		input  = new Data(bitSize);
		output = new Data(bitSize);
	}

	@Override
	public void execute() {
		output.setData(input.getData());
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
}
