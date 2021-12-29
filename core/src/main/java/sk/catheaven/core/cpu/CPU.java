package sk.catheaven.core.cpu;

import sk.catheaven.core.Data;
import sk.catheaven.core.instructions.AssembledInstruction;

import java.util.Collection;

@org.springframework.stereotype.Component
public interface CPU {
    Collection<Component> getComponents();
    Collection<Connector> getConnectors();
    Data[] getRegisters();

    void execute();                 // single- or multi-cycle
    void reset();                   // simulation goes back to the original state
    void assembleCode(String code);
    boolean hasErrors();
    AssembledInstruction getLastInstruction();
}
