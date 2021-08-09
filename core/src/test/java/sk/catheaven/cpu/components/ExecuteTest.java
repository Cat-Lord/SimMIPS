package sk.catheaven.cpu.components;

import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.cpu.Executable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

public class ExecuteTest extends CPUContainer {
    
    @Test
    public void executeTest() {
        for (Executable component : cpu.getComponents().values())
            assertDoesNotThrow(component::execute);
    }
}
