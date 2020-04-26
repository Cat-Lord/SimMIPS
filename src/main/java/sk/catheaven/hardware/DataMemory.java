/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class DataMemory extends Component {
	private final Map<String, Data> inputs;	
	
	public DataMemory(String label, JSONObject json) {
		super(label);
		
		inputs = new HashMap<>();
		
		setupIO(json);
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

	private void setupIO(JSONObject json) {
		
	}
	
}
