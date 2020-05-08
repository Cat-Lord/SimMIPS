/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;

/**
 * From the input sign-extends the output.
 * @author catlord
 */
public class SignExtend extends Component {
	private final Data input, output;
	private final int bitDiff;			// difference in bit size, used in shifting when execute() method is called
	
	public SignExtend(String label, JSONObject json) throws Exception {
		super(label, json);
		
		int iSize = json.getInt("input");
		int oSize = json.getInt("output");
		
		if(iSize >= oSize)
			throw new Exception(String.format("Input size is greater or equal to output size (%d > %d)", iSize, oSize));
		
		bitDiff = oSize - iSize;
		input = new Data(iSize);
		output = new Data(oSize);	
	}
	
	@Override
	public void execute() {
		output.setData(
			((input.getData() << bitDiff) >> bitDiff)		// first shift to left and then sign-shift to the right to preserve the sign
		);
		
		notifySubs();
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
	
	public Data getInput(String selector){
		return input.duplicate();
	}

	@Override
	public void reset() {
		input.setData(0);
		output.setData(0);
	}
}
