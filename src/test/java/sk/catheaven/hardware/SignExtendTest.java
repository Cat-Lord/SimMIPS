/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.io.IOException;
import java.net.URISyntaxException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.hardware.SignExtend;
import sk.catheaven.instructionEssentials.Data;
import sk.catheaven.run.Loader;

/**
 *
 * @author catlord
 */
public class SignExtendTest extends Container {
	
	
	public SignExtendTest() throws IOException, URISyntaxException {
		
	}
	
	@Before
	public void setUp() {
	}
	
	/**
	 * Testing border cases - when the left-most bit is one, all the extended bits must be 1's as well.
	 * If that bit is zero, expansion happens with all 0's.
	 */
	@Test
	public void test() {
		SignExtend se;
		try {
			se = new SignExtend("SE", cpuJson.getJSONObject("SIGN_EXT"));
		} catch(Exception e) { System.out.println(e.getMessage()); fail("Shouldn't have cought an exception"); return; }
		
		Data d = new Data(16);
		se.setInput("", d);
		assertEquals(32, se.getOutput("").getBitSize());
		
		assertEquals("0000 0000 0000 0000 0000 0000 0000 0000", se.getOutput("").getBinary());
		
		d.setData(-1);				// all ones
		se.setInput("", d);
		se.execute(); assertEquals("1111 1111 1111 1111 1111 1111 1111 1111", se.getOutput("").getBinary());
		
		d.setData((-1 >>> 17));		// left-most bit 0, other bits 1, but writing 1 gives 32 bit number, so shift 17 right to erase the left-most 1
		se.setInput("", d);
		se.execute(); assertEquals("0000 0000 0000 0000 0111 1111 1111 1111", se.getOutput("").getBinary());
		
		d.setData((1 << 15));		// left-most bit 1, other bits 0
		se.setInput("", d);
		se.execute(); assertEquals("1111 1111 1111 1111 1000 0000 0000 0000", se.getOutput("").getBinary());
		
		d.setData(69);				// test for a common number
		se.setInput("", d);
		se.execute(); assertEquals("0000 0000 0000 0000 0000 0000 0100 0101", se.getOutput("").getBinary());
	}
}
