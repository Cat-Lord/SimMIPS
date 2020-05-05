/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;
import java.util.logging.Logger;
import sk.catheaven.hardware.CPU;
import sk.catheaven.hardware.Component;

/**
 * Temporary console-main
 * @author catlord
 */
public class CSLMain {
	public static void main(String[] args) {
		CPU cpu;
		try {
			Loader l = new Loader("sk/catheaven/data/layout.json", "sk/catheaven/data/cpu.json");
			cpu = l.getCPU();
		} catch(Exception e) { e.printStackTrace(); System.out.println(e.getMessage()); return; }
		
		try {
			LogManager lm = LogManager.getLogManager();
			Logger lgr = lm.getLogger(Logger.GLOBAL_LOGGER_NAME);
			//lgr.setLevel(Level.OFF);

			FileHandler fh = new FileHandler("log");
			SimpleFormatter sf = new SimpleFormatter();
			fh.setFormatter(sf);
			lgr.addHandler(fh);
		} catch(IOException e) { System.out.println(e.getMessage()); }
		
		Scanner scanner = new Scanner(System.in);
		try {
			String c;
			int phase;
			
			cpu.assembleCode(loadCode());
			
			while(true) {
				c = "9"; 
				if(scanner.next().equals("q"))
					break;
				
				phase = Integer.parseInt(c);
				System.out.println("before");
				printComps(cpu, phase);
				
				cpu.executeCycle();
				
				System.out.println("------------------\nafter");
				printComps(cpu, phase);
			}
		} catch(Exception e) { e.printStackTrace(); System.out.println(e.getMessage()); }
    }
	
	public static void printComps(CPU cpu, int index){
		if(index > CPU.PHASE_COUNT){
			for(int i = 0; i < CPU.PHASE_COUNT; i++){
				printComps(cpu, i);
			}
			return;
		}
		
		List<Component> cmps = cpu.getComponentsOfPhase(index);
		cmps.forEach((component) -> {
			System.out.println(component.getLabel());
			System.out.println(component.getStatus());
		});
	}
	
	private static String loadCode(){
		return "\n\n" +
				"subi r1, r8, 6\n" +
				"li r6, 6\n" +
				"mul r17, r23,r2\n" +
				"sllv r6, r1, r6\n" +
				"\n" +
				"cat:                 sub r1, r1 ,r30\n" +
				"add r1, r1, r3\n" +
				"li r7, 154\n" +
				"bneq r1, r7, cat\n" +
				"beq r1, r1, cat\n" +
				"beq r0, r0, next\n" +
				"\n" +
				"li r1, 951\n" +
				"next: lw r6, 123(r6)\n" +
				"beq r0, r0, super_label_version_million\n" +
				"\n" +
				"super_label_version_million: \n" +
				"; now a little comment for this awesome example\n" +
				"mulu r6, r1, r1\n" +
				"mulu r1, r1, r1\n" +
				"nop\n" +
				"nop\n" +
				"nop\n" +
				"beq r0, r0, ending\n" +
				"nop\n" +
				"nop\n" +
				"nop\n" +
				"nop\n" +
				"ending:add r1, r1, r7\n" +
				"beq r0, r0, cat";
	}
}