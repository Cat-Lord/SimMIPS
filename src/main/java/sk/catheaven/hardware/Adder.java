/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.json.JSONObject;

/**
 *
 * @author catlord
 */
public class Adder extends BinaryComponent {

	public Adder(String label, JSONObject json) {
		super(label, json);
	}

	@Override
	public void execute() {
		System.out.print("Executing Adder: " + inputA.getRight().getData() + " + " + inputB.getRight().getData());
		output.setData(
			(inputA.getRight().getData() + inputB.getRight().getData())
		);
		System.out.print(" == " + output.getData() + "\n");
	}
}
