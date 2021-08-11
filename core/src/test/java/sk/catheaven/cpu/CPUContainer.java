package sk.catheaven.cpu;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import sk.catheaven.main.Launcher;
import sk.catheaven.main.Loader;
import sk.catheaven.model.Tuple;
import sk.catheaven.model.cpu.components.CPU;
import sk.catheaven.model.instructions.Instruction;
import sk.catheaven.service.Assembler;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CPUContainer {
    protected static CPU cpu;
    protected static List<Instruction> instructions;
    protected static Assembler assembler;
    
    @BeforeAll
    static void loadCPU() throws Exception {
        InputStream instructionResource = Launcher.class.getResourceAsStream("/design/instructions.json");
        instructions = Arrays.asList(Loader.getInstructionSet(instructionResource));
        
        Map<String, Instruction> instructionMap = new HashMap<>();
        for (Instruction instruction : instructions)
            instructionMap.put(instruction.getMnemo(), instruction);
    
        assembler = new Assembler(instructionMap);
        
        InputStream cpuJsonResource = Launcher.class.getResourceAsStream("/design/cpu.json");
        cpu = Loader.getCPU(cpuJsonResource);
    }
    
    protected Supplier<String> assemblerErrorSupplier() {
        return () -> {
            StringBuilder errors = new StringBuilder("\n");
            for (Tuple<Integer, String> error : assembler.getSyntaxErrors().getLineErrors())
                errors.append("\t").append(error.getLeft()).append(": ").append(error.getRight()).append("\n");
    
            for (String error : assembler.getSyntaxErrors().getMessageErrors())
                errors.append("\t").append(error).append("\n");
            return errors.toString();
        };
    }
}
