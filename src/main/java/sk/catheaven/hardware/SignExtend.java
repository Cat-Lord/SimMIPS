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
	private Data input, output;
	private int bitDiff;			// difference in bit size
	
	public SignExtend(String label, JSONObject json) throws Exception {
		super(label);
		
		setupIO(json);
	}

	private void setupIO(JSONObject json) throws Exception {
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
			((input.getData() << bitDiff) >> bitDiff)					// first shift to left and then sign-shift to the right to preserve the sign
		);
	}
	
}
