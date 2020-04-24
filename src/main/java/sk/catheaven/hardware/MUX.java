/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.json.JSONObject;
import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class MUX extends BinaryComponent {
	private final Data selector;
		
	public MUX(String label, JSONObject json) {
		super(label, json);
		
		selector = new Data(json.getInt("selector"));
	}

	@Override
	public void execute() {
		output.setData(
			(selector.getData() == 0) ? inputA.getData() : inputB.getData()
		);
	}
}