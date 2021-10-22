package sk.catheaven.core;

import sk.catheaven.model.cpu.Connector;
import sk.catheaven.model.instructions.AssembledInstruction;

import java.util.Collection;

public interface CPU {
    Collection<Component> getComponents();
    Collection<Connector> getConnectors();

    void execute();                 // single- or multi-cycle
    void reset();                   // simulation goes back to the original state
    void assembleCode(String code);
    boolean hasErrors();
    AssembledInstruction getLastInstruction();
}
