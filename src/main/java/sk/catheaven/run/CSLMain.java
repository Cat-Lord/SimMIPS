/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.run;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.SimpleFormatter;
import java.util.logging.Logger;
import sk.catheaven.hardware.CPU;

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
			lgr.setLevel(Level.OFF);

			FileHandler fh = new FileHandler("log");
			SimpleFormatter sf = new SimpleFormatter();
			fh.setFormatter(sf);
			lgr.addHandler(fh);
			
			//lgr.log(Level.INFO, lgr.getParent().getName());
			//lgr.log(Level.INFO, Logger.GLOBAL_LOGGER_NAME);
		} catch(IOException e) { System.out.println(e.getMessage()); }
		
		Scanner scanner = new Scanner(System.in);
		try {
			while(true) {
				cpu.executeCycle();
				if(scanner.next().equals("q"))
					break;
				Runtime.getRuntime().exec("clear");
			}
		} catch(Exception e) { e.printStackTrace(); System.out.println(e.getMessage()); }
    }
}