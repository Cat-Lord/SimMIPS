package sk.catheaven.cpu.components;

import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.cpu.Executable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ExecuteTest extends CPUContainer {
    
    @Test
    public void executeTest() {
        for (Executable component : cpu.getComponents().values())
            assertDoesNotThrow(component::execute);
    }
    
    @Test
    public void multipleExecutes() {
        for (int i = 0; i < 15; i++)
            for (Executable component : cpu.getComponents().values())
                assertDoesNotThrow(component::execute);
    }
}
