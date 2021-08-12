/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.components.CPU;
import sk.catheaven.model.instructions.Instruction;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Main application window. Application entry.
 * @author catlord
 */
public class Launcher {
	private static final Logger log = LogManager.getLogger();
	public static Properties properties;

	public static void main(String[] args){
		
		InputStream instructionsJsonResource = Launcher.class.getResourceAsStream("/design/instructions.json");
		InputStream cpuJsonResource = Launcher.class.getResourceAsStream("/design/cpu.json");
		try {
			Instruction[] instructions = Loader.getInstructionSet(instructionsJsonResource);
			
			
			for (Instruction instruction : instructions) {
				log.debug("{}: type {} with {} fields",
						instruction.getMnemo(), instruction.getType().getLabel(), instruction.getFields().size());
			}
			log.debug("Successfully loaded {} instructions\n", instructions.length);
			
			CPU cpu = Loader.getCPU(cpuJsonResource);
			
			for (Component component: cpu.getComponents().values())
				log.debug("{}\n{}\n {} input(s) | {} output(s) | {} selector(s)",
						component.getLabel(), component,
						component.getInputs().size(), component.getOutputs().size(), component.getSelectors().size()
				);
			
			log.debug("Successfully initiated CPU with {} components and {} connectors\n",
					cpu.getComponents().size(), cpu.getConnectors().size());
			
			for (String sourceLabel: cpu.getConnectors().keySet())
				log.debug("Component `{}` has {} connections",
						sourceLabel, cpu.getConnectors().get(sourceLabel).size());
			
			int phaseIndex = 1;
			for (List<Component> phase : cpu.getPhases())
				log.debug("Phase {}: {} Components", phaseIndex++, phase.size());

		} catch (Exception exception) {
			log.error(exception.getMessage());
			exception.printStackTrace();
		}
	}
}