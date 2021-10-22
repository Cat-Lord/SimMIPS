package sk.catheaven.model.cpu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.catheaven.core.CPU;
import sk.catheaven.core.Component;
import sk.catheaven.model.cpu.Connector;
import sk.catheaven.model.cpu.components.CPUBase;
import sk.catheaven.model.instructions.AssembledInstruction;
import sk.catheaven.service.Assembler;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CPUImpl implements CPU {

    @Autowired
    CPUBase cpu;

    @Autowired
    Assembler assembler;

    @Override
    public Collection<Component> getComponents() {
        return cpu.getComponents().values();
    }

    @Override
    public Collection<Connector> getConnectors() {
        return cpu.getConnectors().values().stream()
                                    .flatMap(List::stream)
                                    .collect(Collectors.toList());
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
            connector.getContentDataList().forEach((data) -> data.setData(0));
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