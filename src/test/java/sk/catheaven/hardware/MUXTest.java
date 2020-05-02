/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.hardware.MUX;
import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class MUXTest extends Container {
	
	public MUXTest() {
	}
	
	@Test
	public void test(){
		MUX mux;
		
		try {
			mux = new MUX("mux", findJsonObject("BRANCH_MUX"));
		} catch(Exception e) { System.out.println(e.getMessage()); fail("Shouldn't have cought an exception"); return; }
		
		Data signal = new Data(1);
		signal.setData(1);
		
		// INPUT A
		Data inA = new Data();
		inA.setData(69);
		
		// INPUT B
		Data inB = new Data();
		inB.setData(512);
		
		/**
		 *									BRANCH_MUX
		 * 
		 * Set inputs and request data (should give back input A, because we didn't set signal to 1)
		 */
		mux.setInput("newAddress", inA);
		mux.setInput("branchAddress", inB);
		mux.execute(); assertEquals(inA.getData(), mux.getOutput("").getData());
		
		// set signal to 1 and expect input B value as output
		mux.setInput("branchSignal", signal);
		mux.execute(); assertEquals(inB.getData(), mux.getOutput("").getData());
		
		// change input data
		inA.setData(1000);
		inB.setData(9864);
		
		mux.setInput("branchAddress", inB);
		mux.execute(); assertEquals(inB.getData(), mux.getOutput("").getData());
		
		signal.setData(0);
		mux.setInput("branchSignal", signal);
		mux.execute(); assertEquals(69, mux.getOutput("").getData());
		
		mux.setInput("newAddress", inA);
		mux.execute(); assertEquals(inA.getData(), mux.getOutput("").getData());
	}
	
}