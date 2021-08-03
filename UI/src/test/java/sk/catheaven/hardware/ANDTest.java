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
public class ANDTest extends Container {
	
	public ANDTest() {
	}

	/**
	 * Test of execute method, of class AND.
	 */
	@Test
	public void testExecute() {
		AND and;
		try {
			and = new AND("AND+", findJsonObject("BRANCH_AND"));
		} catch(Exception e){ System.out.println(e.getMessage()); fail("Shouldn't have cought an exception"); return; }
		
		Data d1 = new Data();
		Data d2 = new Data();
		d1.setData(22637124);
		d2.setData(32123);
		
		and.setInput("branchSignal", d1);
		and.setInput("zeroResultSignal", d2);
		and.execute();
		assertEquals(0, and.getOutput("").getData());
		
		d1.setData(31);
		d2.setData(63);
		
		and.setInput("branchSignal", d1);
		and.setInput("zeroResultSignal", d2);
		and.execute();
		assertEquals(1, and.getOutput("").getData());
	}
	
}
