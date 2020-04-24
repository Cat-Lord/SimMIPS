/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.cpuComponentsTests;

import java.io.IOException;
import java.net.URISyntaxException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.hardware.LatchRegister;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.run.Loader;

/**
 *
 * @author catlord
 */
public class LatchRegisterTest {
	private JSONObject cpuJson;
	
	public LatchRegisterTest() {
	}
	
	@Before
	public void setUp() throws IOException, IOException, URISyntaxException {
		Loader l = new Loader();
		cpuJson = new JSONObject(l.readFile("sk/catheaven/data/cpu.json")).getJSONObject("components");
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
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail("Shouldn't have cought an exception ! ");
		}
		
		/**
		 * TESTING FOR ID_EX latch
		 */
		try {
			tl = new LatchRegister("4", cpuJson.getJSONObject("IF_ID"));
			d.setData(0x20916931);		// instruction is: ADDI $17,$4,6931, imm value is 26 929
			tl.setData("iCode", d);
			tl.execute();
			
			System.out.println(" rs: " + tl.getData("rs").getData());
			System.out.println(" rd: " + tl.getData("rd").getData());
			System.out.println(" rt: " + tl.getData("rt").getData());
			System.out.println("imm: " + tl.getData("imm").getData());	
		} catch (Exception e) {
			System.out.println(e.getMessage());
			fail("Shouldn't have cought an exception ! ");
		}
	}
	
}
