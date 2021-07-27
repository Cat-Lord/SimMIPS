package sk.catheaven.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.components.CPU;
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
                        instruction.getMnemo(), instruction.getType(), instruction.getFields().size());
            }
            log.debug("Successfully loaded {} instructions\n", instructions.length);
            
            CPU cpu = Loader.getCPU(cpuJsonResource);
            
            log.debug("Successfully initiated CPU with {} components\n", cpu.getComponents().size());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
        }
    
    }
}
