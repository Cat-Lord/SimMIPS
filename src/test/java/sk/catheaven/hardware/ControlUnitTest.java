/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.io.IOException;
import java.net.URISyntaxException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.hardware.ControlUnit;
import sk.catheaven.run.Loader;

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
			cu = new ControlUnit("cu", cpuJson.getJSONObject("CU"));
		} catch(Exception e){ System.out.println(e.getMessage()); fail("Shouldn't have cought an exception"); return; }
	}
	
}