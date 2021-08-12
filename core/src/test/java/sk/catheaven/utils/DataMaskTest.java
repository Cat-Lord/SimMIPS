package sk.catheaven.utils;

import org.junit.jupiter.api.Test;
import sk.catheaven.model.Data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 *
 * @author catlord
 */
public class DataMaskTest {
	private int allOnesNumber = 0b11111111111111111111111111111111;
	@Test
	public void bitSizeUpTo6() {
		Data d1 = new Data(1);
		d1.setData(allOnesNumber);
		assertEquals(1, d1.getData());
		
		Data d2 = new Data(2);
		d2.setData(allOnesNumber);
		assertEquals(3, d2.getData());
		
		Data d3 = new Data(3);
		d3.setData(allOnesNumber);
		assertEquals(7, d3.getData());
		
		Data d4 = new Data(4);
		d4.setData(allOnesNumber);
		assertEquals(15, d4.getData());
		
		Data d5 = new Data(5);
		d5.setData(allOnesNumber);
		assertEquals(31, d5.getData());
		
		Data d6 = new Data(6);
		d6.setData(allOnesNumber);
		assertEquals(63, d6.getData());
	}
	
	@Test
	public void bitSizeFrom30To32() {
		Data d30 = new Data(30);
		d30.setData(allOnesNumber);
		assertEquals(1073741823, d30.getData());
		assertNotEquals(2147483647, d30.getData());
		
		Data d31 = new Data(31);
		d31.setData(allOnesNumber);
		assertEquals(2147483647, d31.getData());
		
		Data d32 = new Data(32);
		d32.setData(allOnesNumber);
		assertEquals(allOnesNumber, d32.getData());
	}
}
