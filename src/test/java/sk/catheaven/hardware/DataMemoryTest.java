/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author catlord
 */
public class DataMemoryTest extends Container {
	
	public DataMemoryTest() {
	}
	
	@Test
	public void test(){
		DataMemory mem;
		try {
			mem = new DataMemory("DMem", findJsonObject("DATA_MEM"));
		} catch(Exception e) { System.out.println(e.getMessage()); fail("Did not create data memory !"); return ;}
		
		// TODO
	}
}
