/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.hardware.LatchRegister;
import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class LatchRegisterTest extends Container {
	
	public LatchRegisterTest() {
	}
	
	@Before
	public void setUp() throws IOException, IOException, URISyntaxException {
	}
	
	/**
	 * Testing different latch registers from <i>cpu.json</i> file.
	 */
	@Test
	public void test(){
		LatchRegister tl;
		Data d = new Data();
		
		/**
		 * TESTING FOR MEM_WB latch
		 */
		try {
			tl = new LatchRegister("1", cpuJson.getJSONObject("MEM_WB"));	
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail("Shouldn't have cought an exception ! ");
		}
		
		/**
		 * TESTING FOR EX_MEM latch
		 */
		try {
			tl = new LatchRegister("2", cpuJson.getJSONObject("EX_MEM"));	
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail("Shouldn't have cought an exception ! ");
		}
		
		/**
		 * TESTING FOR ID_EX latch
		 */
		try {
			tl = new LatchRegister("3", cpuJson.getJSONObject("ID_EX"));
			d.setData(69);
			tl.setInput("reg1Value", d);
			tl.execute();
			assertEquals(69,tl.getOutput("aluInputA").getData());
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail("Shouldn't have cought an exception ! ");
		}
		
		/**
		 * TESTING FOR ID_EX latch
		 */
		try {
			tl = new LatchRegister("4", cpuJson.getJSONObject("IF_ID"));
			d.setData(0x20916931); // instruction is: ADDI $17,$4,6931, imm value is 26 929
			tl.setInput("iCode", d);
			tl.execute();
			
			assertEquals(4, tl.getOutput("rs").getData());
			assertEquals(17, tl.getOutput("rt").getData());
			assertEquals(13, tl.getOutput("rd").getData());
			assertEquals(26929,tl.getOutput("imm").getData());
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail("Shouldn't have cought an exception ! ");
		}
	}
	
}
