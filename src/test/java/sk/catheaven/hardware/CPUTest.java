/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.hardware;

import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.main.Loader;

/**
 *
 * @author catlord
 */
public class CPUTest {
	
	public CPUTest() {
		
	}
	
	@Test
	public void test(){
		CPU cpu1, cpu2;
		try {
			Loader l = new Loader("sk/catheaven/design/layout.json", "sk/catheaven/design/cpu.json");
			cpu1 = l.getCPU();
			
		} catch(Exception e) { 
			e.printStackTrace();
			System.out.println(e.getMessage()); 
			fail("FAILED TO EXECUTE CPU !"); 
			return; 
		}
		
		try {
			cpu1.executeCycle();
			System.out.println("------------CORRECT EXECUTION OF SINGLE CYCLE--------------------");
		} catch( Exception e) {
			System.out.println(e.getMessage());
			fail("Single cycle execution failed !");
		}
		
		System.out.println("\n\n\n\n");
		
		//
		// CPU 2
		//
		try {
			Loader l = new Loader("sk/catheaven/design/layout.json", "sk/catheaven/design/cpu.json");
			cpu2 = l.getCPU();
		} catch(Exception e) { 
			e.printStackTrace();
			System.out.println(e.getMessage()); 
			fail("FAILED TO EXECUTE CPU !"); 
			return; 
		}
		
		try {
			for(int i = 0; i < 100; i++){
				cpu2.executeCycle();
				System.out.println("\n - - - - - - - - - - -");
			}
			System.out.println("================CORRECT EXECUTION 1000 CYCLE EXECUTION==================");
		} catch( Exception e) {
			System.out.println(e.getMessage());
			fail("Single cycle execution failed !");
		}
		
		// Display element phase association		
		for(int i = 0; i < CPU.PHASE_COUNT; i++){
			System.out.println("Phase " + i);
			for(Component c : cpu1.getComponentsOfPhase(i)){
				System.out.println("\t" + c.getLabel());
			}
			System.out.println();
		}
	}
}