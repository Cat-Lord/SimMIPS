/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONObject;
import sk.catheaven.main.Loader;

/**
 * Not a test class, but does the necessary setup for each separate test class. 
 * Integration is therefore easier as well as changes in loading.
 * @author catlord
 */
public class Container {
	protected CPU cpu;
	protected JSONArray cpuJson;
	
	protected Container() {
		Loader l;
		try {
			l = new Loader();
			cpuJson = new JSONObject(l.readFile("sk/catheaven/design/cpu.json")).getJSONArray("components");
			cpu = l.getCPU();
		} catch(Exception e) { System.out.println(e.getMessage()); }
	}
	
	protected JSONObject findJsonObject(String label){
		Iterator<Object> jitter = cpuJson.iterator();
		while(jitter.hasNext()){
			JSONObject jobj = (JSONObject) jitter.next();
			if(jobj.getString("label").equals(label))
				return jobj;
		}
		return null;
	}
	
}
