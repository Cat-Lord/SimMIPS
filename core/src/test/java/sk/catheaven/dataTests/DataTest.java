/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.dataTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sk.catheaven.model.Data;
import sk.catheaven.utils.DataFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 *
 * @author catlord
 */
public class DataTest {

	@Test
	public void initializationTest() {
		Data data = new Data();
		assertEquals(0, data.getData());
		assertEquals("1111 1111 1111 1111 1111 1111 1111 1111",
							  DataFormatter.getBinary(data.getMask(), data.getBitSize()));
		assertEquals("0000 0000 0000 0000 0000 0000 0000 0000", DataFormatter.getBinary(data));
	}
	
	@Test
	public void limitedBitSizeOfOneTest() {
		Data d1 = new Data(1);
		
		d1.setData(0);
		assertEquals(0, d1.getData());
		assertEquals("0", DataFormatter.getBinary(d1));
		
		d1.setData(1);
		assertEquals(1, d1.getData());
		assertEquals("1", DataFormatter.getBinary(d1));
		
		d1.setData(-1);
		assertNotEquals(-1, d1.getData());	// bit size 1, so result should be 1
		
		d1.setData(2); // binary: 10
		assertEquals(0, d1.getData());
		assertNotEquals(2, d1.getData());		// should not be equal, since number is only 1 bit
	}
	
	@Test
	public void limitedBitSizeOfTwoTest() {
		Data dataWithBitSize2 = new Data(2);
		
		dataWithBitSize2.setData(0);
		assertEquals(0, dataWithBitSize2.getData());
		assertEquals("00", DataFormatter.getBinary(dataWithBitSize2));
		
		dataWithBitSize2.setData(1);
		assertEquals(1, dataWithBitSize2.getData());
		assertEquals("01", DataFormatter.getBinary(dataWithBitSize2));
		
		dataWithBitSize2.setData(2);
		assertEquals(2, dataWithBitSize2.getData());
		assertEquals("10", DataFormatter.getBinary(dataWithBitSize2));
		
		dataWithBitSize2.setData(3);
		assertEquals(3, dataWithBitSize2.getData());
		assertEquals("11", DataFormatter.getBinary(dataWithBitSize2));
		
		dataWithBitSize2.setData(4);
		assertNotEquals(4, dataWithBitSize2.getData());
		assertEquals("00", DataFormatter.getBinary(dataWithBitSize2));
	}
	
	@Test
	public void limitedBitSizeOfFiveTest() {
		Data bitSize5 = new Data(5);
		
		bitSize5.setData(4);
		assertEquals(4, bitSize5.getData());
		assertEquals("0 0100", DataFormatter.getBinary(bitSize5));
		
		bitSize5.setData(16);
		assertEquals(16, bitSize5.getData());
		assertEquals("1 0000", DataFormatter.getBinary(bitSize5));
		
		bitSize5.setData(45);
		assertEquals(13, bitSize5.getData());
		assertEquals("0 1101", DataFormatter.getBinary(bitSize5));
		
		bitSize5.setData(-5);
		assertEquals(27, bitSize5.getData());
		assertEquals("1 1011", DataFormatter.getBinary(bitSize5));
	}
	
	@Test
	public void largeBitSizeTest() {
		Data d32 = new Data(100);		// limited by max default size of 32
		
		d32.setData(1234);
		assertEquals(1234, d32.getData());
		assertEquals("0000 0000 0000 0000 0000 0100 1101 0010", DataFormatter.getBinary(d32));
		
		d32.setData(12341234);
		assertEquals(12341234, d32.getData());
		assertEquals("0000 0000 1011 1100 0100 1111 1111 0010", DataFormatter.getBinary(d32));
	}
}
