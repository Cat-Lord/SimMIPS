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
public class DataTest {
	
	public DataTest() {
	}

	@Test
	public void test() {
		
		//
		// Initialization tests
		//
		Data data = new Data();
		assertEquals(0, data.getData());
		assertEquals("0000 0000 0000 0000 0000 0000 0000 0000", data.getBinary());
		
		//
		// d1
		//
		Data d1 = new Data(1);
		
		d1.setData(0);
		assertEquals(0, d1.getData());
		assertEquals("0", d1.getBinary());
		
		d1.setData(1);
		assertEquals(1, d1.getData());
		assertEquals("1", d1.getBinary());
		
		d1.setData(-1);
		assertNotEquals(-1, d1.getData());
		
		d1.setData(2);
		assertNotEquals(2, d1.getData());		// should not be equal, since number is only 1 bit
		
		
		//
		// d2
		//
		Data d2 = new Data(2);
		
		d2.setData(0);
		assertEquals(0, d2.getData());
		assertEquals("00", d2.getBinary());
		
		d2.setData(1);
		assertEquals(1, d2.getData());
		assertEquals("01", d2.getBinary());
		
		d2.setData(2);
		assertEquals(2, d2.getData());
		assertEquals("10", d2.getBinary());
		
		d2.setData(3);
		assertEquals(3, d2.getData());
		assertEquals("11", d2.getBinary());
		
		d2.setData(4);
		assertNotEquals(4, d2.getData());
		assertEquals("00", d2.getBinary());
		
		
		//
		// d3
		//
		Data d3 = new Data(5);
		
		d3.setData(4);
		assertEquals(4, d3.getData());
		assertEquals("0 0100", d3.getBinary());
		
		d3.setData(16);
		assertEquals(16, d3.getData());
		assertEquals("1 0000", d3.getBinary());
		
		d3.setData(45);
		assertEquals(13, d3.getData());
		assertEquals("0 1101", d3.getBinary());
		
		d3.setData(-5);
		assertEquals(27, d3.getData());
		assertEquals("1 1011", d3.getBinary());
		
		//
		// d32
		//
		Data d32 = new Data(100);
		
		d32.setData(1234);
		assertEquals(1234, d32.getData());
		assertEquals("0000 0000 0000 0000 0000 0100 1101 0010", d32.getBinary());
		
		d32.setData(12341234);
		assertEquals(12341234, d32.getData());
		assertEquals("0000 0000 1011 1100 0100 1111 1111 0010", d32.getBinary());
	}
}
