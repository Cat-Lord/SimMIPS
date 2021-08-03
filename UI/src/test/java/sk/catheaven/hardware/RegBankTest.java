/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.junit.Test;
import static org.junit.Assert.*;

import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class RegBankTest extends Container {
	
	public RegBankTest() {
	}
	
	@Test
	public void test(){
		RegBank rb;
		try {
			rb = new RegBank("RegBank", findJsonObject("REG_BANK"));
		} catch(Exception e) { System.out.println(e.getMessage()); fail("Shouldn't have cought an exception"); return; }
		
		Data signal = new Data(1);
		signal.setData(1);
		
		// test data 
		Data d1 = new Data(32);
		d1.setData(69);
		Data destReg = new Data(5);
		destReg.setData(16);
		
		rb.setInput("regWriteSignal", signal);
		rb.setInput("destRegValue", d1);
		rb.setInput("destReg", destReg);			// set destination register 
		rb.setInput("rt", destReg);					// and rt the same, to immediately request data after writin it
		
		rb.execute();
		assertEquals(d1.getData(), rb.getOutput("reg2Value").getData());
	}
}
