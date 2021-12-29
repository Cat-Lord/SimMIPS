package sk.catheaven.model.cpu;

import sk.catheaven.core.Data;
import sk.catheaven.core.cpu.CPU;
import sk.catheaven.core.cpu.Component;
import sk.catheaven.core.cpu.Connector;
import sk.catheaven.core.instructions.AssembledInstruction;
import sk.catheaven.model.cpu.components.CPUBase;
import sk.catheaven.service.Assembler;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CPUImpl implements CPU {

    private CPUBase cpu;

    private Assembler assembler;

    public CPUImpl(CPUBase cpu, Assembler assembler) {
        this.cpu = cpu;
        this.assembler = assembler;
    }

    @Override
    public Collection<Component> getComponents() {
        return cpu.getComponents().values();
    }

    @Override
    // todo - rewrite to NOT create a new list but rather return unmodifiable list instead
    public Collection<Connector> getConnectors() {
        return cpu.getConnectors().values().stream()
                                    .flatMap(List::stream)
                                    .collect(Collectors.toList());
    }

    @Override
    public Data[] getRegisters() {
        return cpu.getRegisters();
    }

    @Override
    public void execute() {
        cpu.executeCycle();
    }

    @Override
    public void reset() {
        for (Component component : cpu.getComponents().values())
            component.reset();

        for (Connector connector : getConnectors())
            connector.reset();
    }

    @Override
    public void assembleCode(String code) {
        List<AssembledInstruction> program = assembler.assembleCode(code);
        cpu.setAssembledCode(program);
    }

    @Override
    public boolean hasErrors() {
        return assembler.getSyntaxErrors().isEmpty() == false;
    }

    @Override
    public AssembledInstruction getLastInstruction() {
        try {
            return cpu.getProgram().get(cpu.getProgram().size() - 1);
        } catch (IndexOutOfBoundsException ignored) {}
        return null;
    }

}
