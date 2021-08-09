package sk.catheaven.cpu;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import sk.catheaven.main.Launcher;
import sk.catheaven.main.Loader;
import sk.catheaven.model.cpu.Executable;
import sk.catheaven.model.cpu.components.CPU;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CPUContainer {
    protected static CPU cpu;
    
    @BeforeAll
    static void loadCPU() throws Exception {
        InputStream cpuJsonResource = Launcher.class.getResourceAsStream("/design/cpu.json");
        cpu = Loader.getCPU(cpuJsonResource);
    }
}
