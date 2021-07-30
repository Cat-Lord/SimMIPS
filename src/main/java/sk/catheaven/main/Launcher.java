package sk.catheaven.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.components.CPU;
import sk.catheaven.model.components.Component;
import sk.catheaven.model.instructions.Instruction;

import java.io.InputStream;

public class Launcher {
    private static final Logger log = LogManager.getLogger();
    
    public static void main(String[] args) {
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
            
            for (Component component: cpu.getComponents())
                log.debug("{}\n{}\n {} input(s) | {} output(s) | {} selector(s)",
                        component.getLabel(), component,
                        component.getInputs().size(), component.getOutputs().size(), component.getSelectors().size()
                );
            
            log.debug("Successfully initiated CPU with {} components and {} connectors\n",
                                cpu.getComponents().size(), cpu.getConnectors().size());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
        }
    
    }
}
