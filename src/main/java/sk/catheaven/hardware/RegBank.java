/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class RegBank extends Component {
	private Data[] registers;
	
	public RegBank(String label, JSONObject json) {
		super(label);
		
		setupIO(json);
	}

	private void setupIO(JSONObject json) throws JSONException {
		registers = new Data[json.getInt("regCount")];
	}

	@Override
	public void execute() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Data getData(String selector) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setData(String selector, Data data) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
}
