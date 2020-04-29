/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.run.Loader;

/**
 *
 * @author catlord
 */
public class CPUTest {
	CPU cpu;
	
	public CPUTest() {
		try {
			Loader l = new Loader("sk/catheaven/data/layout.json", "sk/catheaven/data/cpu.json");
			this.cpu = l.getCPU();
		} catch(Exception e) { e.printStackTrace(); System.out.println(e.getMessage()); System.out.println(e.getMessage()); }
	}
	
	@Test
	public void test(){
		if(cpu == null) fail("CPU IS UNDEFINED");
		try {
			cpu.executeCycle();
			Thread.sleep(1000);
		} catch(Exception e) { e.printStackTrace(); System.out.println(e.getMessage()); fail("CPU failed to execute"); return; }
	}
}