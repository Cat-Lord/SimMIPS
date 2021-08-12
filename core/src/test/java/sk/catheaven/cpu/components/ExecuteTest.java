package sk.catheaven.cpu.components;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.cpu.Executable;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("CPU should")
public class ExecuteTest extends CPUContainer {
    
    @Test
    @DisplayName("correctly call execute on every of its components")
    public void componentSingleExecuteTest() {
        for (Executable component : cpu.getComponents().values())
            assertDoesNotThrow(component::execute);
    }
    
    @Test
    @DisplayName("correctly call execute multiple times on every of its components")
    public void componentMultipleExecuteCallsTest() {
        for (int i = 0; i < 15; i++)
            for (Executable component : cpu.getComponents().values())
                assertDoesNotThrow(component::execute);
    }
    
    @Test
    @DisplayName("correctly perform cycle execution")
    public void executeSingleCycle() {
        cpu.executeCycle();
    }
    
    @Test
    @DisplayName("correctly perform cycle execution multiple times in a row")
    public void executionCycleMultipleTimes() {
        for (int i = 0; i < 15; i++)
            cpu.executeCycle();
    }
}
