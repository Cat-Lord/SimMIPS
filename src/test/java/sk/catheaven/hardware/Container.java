/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.io.IOException;
import java.net.URISyntaxException;
import org.json.JSONObject;
import sk.catheaven.run.Loader;

/**
 * Not a test class, but does the necessary setup for each separate test class. 
 * Integration is therefore easier as well as changes in loading.
 * @author catlord
 */
public class Container {
	CPU cpu;
	protected JSONObject cpuJson;
	
	protected Container() {
		Loader l;
		try {
			l = new Loader();
			cpuJson = new JSONObject(l.readFile("sk/catheaven/data/cpu.json")).getJSONObject("components");
			cpu = l.getCPU();
		} catch(Exception e) { System.out.println(e.getMessage()); }
	}
	
}
