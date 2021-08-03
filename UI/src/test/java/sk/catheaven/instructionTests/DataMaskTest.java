/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionTests;

import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class DataMaskTest {
	
	public DataMaskTest() {
	}

	@Test
	public void dataTest() {
		Data d1 = new Data(1);
		d1.setData(-1);
		assertEquals(1, d1.getData());
		
		Data d2 = new Data(2);
		d2.setData(-1);
		assertEquals(3, d2.getData());
		
		Data d3 = new Data(3);
		d3.setData(-1);
		assertEquals(7, d3.getData());
		
		Data d4 = new Data(4);
		d4.setData(-1);
		assertEquals(15, d4.getData());
		
		Data d5 = new Data(5);
		d5.setData(-1);
		assertEquals(31, d5.getData());
		
		Data d6 = new Data(6);
		d6.setData(-1);
		assertEquals(63, d6.getData());
		
		
		// ..........
		
		
		Data d31 = new Data();
		d31.setData(2147483647);
		assertEquals(2147483647, d31.getData());
		
		Data d32 = new Data();
		d32.setData(2147483647);
		assertEquals(2147483647, d32.getData());
		
		Data d32_fail = new Data(30);
		d32_fail.setData(2147483647);
		assertNotEquals(2147483647, d32_fail.getData());
	}
}
