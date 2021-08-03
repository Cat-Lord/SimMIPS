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

import sk.catheaven.instructionEssentials.Data;

/**
 *
 * @author catlord
 */
public class ControlUnitTest extends Container {
	
	public ControlUnitTest() {
	}

	@Before
	public void setUp() throws IOException, URISyntaxException {
	}
	
	@Test
	public void test(){
		ControlUnit cu;
		
		try{
			cu = new ControlUnit("cu", findJsonObject("CU"));
		} catch(Exception e){ System.out.println(e.getMessage()); fail("Shouldn't have cought an exception"); return; }
		
		Data data = new Data(); // initiate empty data
		cu.setInput("this is ignored, because CU has only one input", data);
		cu.execute();
		
		assertEquals("100 0000 0011", cu.getOutput("this is ignored as well, because CU has only one output").getBinary());
	}
	
}