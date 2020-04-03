/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.json.JSONObject;

/**
 * JSON description - the inputs in json are simple - one instruction as integer number.
 * Outputs are specified by intervals. Each interval is described by a number, which
 * represent by how many bits does the IF_ID latch should shift. 
 * Format goes like this: "outputLabel" : "shift_to_left-shift_to_right"
 * @author catlord
 */
public class LatchRegister extends Component {
	private int iCode;
	private int newAddress;
	
	public LatchRegister(String label, JSONObject json) {
		super(label);
		iCode = 0;
		newAddress = 0;
	}
	
	
}
