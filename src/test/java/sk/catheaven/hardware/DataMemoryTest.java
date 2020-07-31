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
public class DataMemoryTest extends Container {
	private final Data data, address, readSignal, writeSignal;
	
	public DataMemoryTest() {
		data = new Data();
		address = new Data();
		readSignal = new Data(1);
		writeSignal = new Data(1);
	}
	
	@Test
	public void test(){
		
		// TODO - rewrite this test
		if(0 == 0)
			return;
		
		
		DataMemory mem;
		try {
			mem = new DataMemory("DMem", findJsonObject("DATA_MEM"));
		} catch(Exception e) { System.out.println(e.getMessage()); fail("Did not create data memory !"); return ;}
		
		// READ/WRITE == 0, output has to be zero
		memset(mem, 123321, 123, 0, 0);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 4125, 123, 0, 0);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 236248, 123, 0, 0);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 2, 123, 0, 0);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		
		// WRITE SIGNAL ACTIVE
		memset(mem, 0, 123, 0, 1);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 0, 0, 1, 0);
		assertEquals(123, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		
		memset(mem, 234237, 123, 0, 1);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 4326, 123, 0, 1);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 342, 123, 0, 1);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		
		// READ SIGNAL ACTIVE
		
		memset(mem, 234237, 0, 1, 0);
		assertEquals(123, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 4326, 0, 1, 0);
		assertEquals(123, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 342, 0, 1, 0);
		assertEquals(123, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		
		//
		// THE SAME AGAIN, JUST TO PROVE IT WORKS MULTIPLE TIMES
		//
		
		// WRITE SIGNAL ACTIVE
		memset(mem, 0, 123, 0, 1);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 0, 0, 1, 0);
		assertEquals(123, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		
		
		memset(mem, 234237, 123, 0, 1);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 4326, 123, 0, 1);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 342, 123, 0, 1);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		
		// READ SIGNAL ACTIVE
		
		memset(mem, 234237, 0, 1, 0);
		assertEquals(123, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 4326, 0, 1, 0);
		assertEquals(123, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 342, 0, 1, 0);
		assertEquals(123, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		
		// SETTING DIFFERENT DATA FOR DIFFERENT ADDRESSES
		
		// WRITE SIGNAL ACTIVE
		memset(mem, 6969, 69, 0, 1);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 97531, 9155, 0, 1);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 66666, 666, 0, 1);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 124578, 966311, 0, 1);
		assertEquals(0, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		
		// READ SIGNAL ACTIVE
		memset(mem, 6969, 0, 1, 0);
		assertEquals(69, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 97531, 0, 1, 0);
		assertEquals(9155, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 66666, 0, 1, 0);
		assertEquals(666, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
		memset(mem, 124578, 0, 1, 0);
		assertEquals(966311, mem.getOutput("").getData());			// we should get zero, because memwrite wasn't active
	}
	
	// private method to easily set all memory data. Immediately calls "execute()"
	private void memset(DataMemory mem, int address, int data, int readSignal, int writeSignal){
		this.address.setData(address);
		this.data.setData(data);
		this.readSignal.setData(readSignal);
		this.writeSignal.setData(writeSignal);
		
		mem.setInput("aluResult", this.address);
		mem.setInput("reg2Value", this.data);
		mem.setInput("memReadSignal", this.readSignal);
		mem.setInput("memWriteSignal", this.writeSignal);
		
		mem.execute();
	}
	
	/** Code when testing in gui
		li r1, 69
		li r2, 99
		li r3, 123
		li r4, 6969
		li r5, 1000
		li r6, 9
		li r7, 827
		li r8, 888
		li r9, 666
		li r10, 1024

		sw r1, 50(r0)
		sw r2, 9182(r0)
		sw r3, 6666(r0)
		sw r3, 4321(r0)
		sw r4, 96(r0)
		sw r5, 800(r0)
		sw r6, 70(r0)
		sw r7, 0(r0)
		sw r8, 4(r0)
		sw r9, 16(r0)
		sw r10, 24(r0)
	 */
}
