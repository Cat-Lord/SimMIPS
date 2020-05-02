/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.utilsTests;

import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.utils.Cutter;

/**
 *
 * @author catlord
 */
public class CutterTest {
	
	public CutterTest() {
	}
	
	@Test
	public void test(){
		Data data = new Data();
		data.setData(69);
		
		// first test data capture without cutting
		Cutter ctr;
		
		try {
			ctr = new Cutter(data.getBitSize(), "" + data.getBitSize());
		} catch(Exception e) { System.out.println(e.getMessage()); fail("AF not created"); return; }
		assertEquals(data.getBitSize(), ctr.getCutData().getBitSize());		// bit sizes should be the same
		
		ctr.setDataToCut(data);
		assertEquals(data.getData(), ctr.getCutData().getData());
		
		data.setData(12345);
		ctr.setDataToCut(data);
		assertEquals(data.getData(), ctr.getCutData().getData());
		
		
		// next check data with less bit size
		
		Cutter ct;
		try {
			ct = new Cutter(data.getBitSize(), "10");		// limit cutter to 10 bits
		} catch(Exception e) { System.out.println(e.getMessage()); fail("Cutter not created"); return; }
		assertEquals(10, ct.getCutData().getBitSize());		// bit sizes should be the same
		
		data.setData(12345);			
		ct.setDataToCut(data);
		assertEquals(57, ct.getCutData().getData());		// after cutting 10 bits, this should result in 57
		
		data.setData(69);
		ct.setDataToCut(data);
		assertEquals(data.getData(), ct.getCutData().getData());		// this should be unchanged
		
		
		Cutter cutter;
		try {
			cutter = new Cutter(data.getBitSize(), "21-25");		// limit cutter to 16 bits, while always erase 4 left and 12 right (16 - 4 == 12)
		} catch(Exception e) { System.out.println(e.getMessage()); fail("Cutter not created"); return; }
		assertEquals(7, cutter.getCutData().getBitSize());		// bit sizes should be the same
		
		
		data.setData(11121745); // next number to test shifts
		cutter.setDataToCut(data);
		assertEquals(69, cutter.getCutData().getData());
		
		data.setData(176836986);
		Cutter nextCutter;
		try {
			nextCutter = new Cutter(data.getBitSize(), "8-25");		// limit cutter to 7 bits, while always erase 4 left and 12 right (16 - 4 == 12)
		} catch(Exception e) { System.out.println(e.getMessage()); fail("Cutter not created"); return; }
		assertEquals(7, nextCutter.getCutData().getBitSize());
		
		nextCutter.setDataToCut(data);
		assertEquals(69, nextCutter.getCutData().getData());
	}
}
