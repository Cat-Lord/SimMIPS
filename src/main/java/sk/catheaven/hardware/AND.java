/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import IObase.Input;
import org.json.JSONException;
import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class AND extends BinaryComponent {
	public AND(String label, JSONObject json) {
		super(label, json);
	}
	
	@Override
	public void execute(){
		output.setData(
				inputA.getData() & inputB.getData()
		);
	}
}
