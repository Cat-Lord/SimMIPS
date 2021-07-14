package sk.catheaven.main;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.instruction.Instruction;
import sk.catheaven.instruction.InstructionType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Launcher {
    private static final Logger log = LogManager.getLogger();
    
    public static void main(String[] args) {
        InputStream instructionsJsonResource = Launcher.class.getResourceAsStream("/design/instructions.json");
        try {
            Instruction[] instructions = Loader.getInstructionSet(instructionsJsonResource);
            log.debug("Successfully loaded {} instructions", instructions.length);
            
            for (Instruction instruction : instructions) {
                log.debug(instruction);
            }
            
            
            
        } catch (IOException exception) { log.error(exception.getMessage()); }
        
        
    }
}
