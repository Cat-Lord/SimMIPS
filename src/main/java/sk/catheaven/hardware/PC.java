/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;

/**
 * Program counter, which stores and forwards address of next instruction to load.
 * @author catlord
 */
public class PC extends Component {
	private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	private final Data input, output;
	
	public PC(String label, JSONObject json) {
		super(label, json);
		
		int bitSize = json.getInt("bitSize");
		
		input  = new Data(bitSize);
		output = new Data(bitSize);
		
		logger.log(Level.INFO, "Input size {0}, output size {0}", new Object[]{input.getBitSize(), output.getBitSize()} );
	}

	@Override
	public void execute() {
		//logger.log(Level.INFO, "Input {0} | Output {1}\n", new Object[]{input.getHex(), output.getHex()});
		output.setData(input.getData());
		notifySubs();
	}

	@Override
	public Data getOutput(String selector) {
		logger.log(Level.INFO, "Output: {0}\n\n", output.getHex());
		return output.duplicate();
	}

	@Override
	public boolean setInput(String selector, Data data) {
		logger.log(Level.INFO, "Input: {0}", data.getHex());
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
	public Data getInput(String selector){
		return input.duplicate();
	}

	@Override
	public void reset() {
		input.setData(0);
		output.setData(0);
	}
}
