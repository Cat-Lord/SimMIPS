package sk.catheaven.componentns;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import sk.catheaven.main.Launcher;
import sk.catheaven.main.Loader;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.Executable;
import sk.catheaven.model.cpu.components.CPU;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExecuteTest {
    private CPU cpu;
    
    @BeforeAll
    public void loadCPU() throws Exception {
        InputStream cpuJsonResource = Launcher.class.getResourceAsStream("/design/cpu.json");
        this.cpu = Loader.getCPU(cpuJsonResource);
    }
    
    @Test
    public void executeTest() {
        if (cpu == null)
            fail();
        
        for (Executable component : cpu.getComponents().values())
            component.execute();
    }
}
