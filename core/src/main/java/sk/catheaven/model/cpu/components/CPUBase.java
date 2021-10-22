package sk.catheaven.model.cpu.components;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import sk.catheaven.core.Component;
import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.Connector;
import sk.catheaven.model.instructions.AssembledInstruction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Component
public class CPUBase {
    private static Logger log = LogManager.getLogger();
    
    public static int BIT_SIZE = 32;
    private Map<String, Component> components;
    
    // maps source component to a list of target components via Connectors (basically a wire in CPU)
    private Map<String, List<Connector>> connectors;
    private List<List<Component>> phases;
    
    private InstructionMemory instructionMemory;
    private RegBank regBank;

    @Autowired
    public CPUBase(int bitSize, Map<String, Component> components, Map<String, List<Connector>> connectors) {
        CPUBase.BIT_SIZE = bitSize;
        this.components = components;
        if (areValidConnectors(connectors))
            this.connectors = connectors;

        parseComponents();
        phases = splitComponentsIntoPhases();
    }

    private void parseComponents() {
        for (Component component : getComponents().values()) {
            if (component instanceof RegBank)
                this.regBank = (RegBank) component;
            else if (component instanceof InstructionMemory)
                this.instructionMemory = (InstructionMemory) component;
        }
    }

    public Map<String, Component> getComponents() {
        return components;
    }
    
    @JsonGetter
    public static int getBitSize() {
        return CPUBase.BIT_SIZE;
    }

    public Map<String, List<Connector>> getConnectors() {
        return connectors;
    }

    public List<List<Component>> getPhases() {
        return phases;
    }
    
    public Data[] getRegisters() {
        return regBank.getRegisters();
    }
    
    /**
     * Return list of components for a specific pipeline phase.
     * @param index Index of specific pipeline phase. List of phases goes like this:
     * 0: Instruction Fetch (IF)
     * 1: Instruction Decode (ID)
     * 2: Execute (EX)
     * 3: Memory (MEM)
     * 4: WriteBack (WB)
     * @return List of components of each phase
     */
    public List<Component> getComponentsOfPhase(int index){
        try {
            return phases.get(index);
        } catch (Exception ignored) {}
        
        return null;
    }
    
    /**
     * Returns label of the last executed memory from instruction memory component.
     * For more detailed info, see the <i>InstructionMemory</i> component.
     * @return Label of the last executed instruction
     */
    public String getLastInstructionLabel(){
        return instructionMemory.getLastInstruction().getInstruction().getMnemo();
    }
    
    /**
     * Tries to assemble code.
     * @param program assembled program without errors
     */
    public void setAssembledCode(List<AssembledInstruction> program) {
        instructionMemory.setProgram(program);
    }

    public List<AssembledInstruction> getProgram() {
        return instructionMemory.getProgram();
    }

    /**
     * Executing cycle means first passing output values of components to inputs of
     * target components. After this step the components are ready to handle input
     * values and construct output values themselves, hence calling <code>execute</code>.
     * <bold>Note:</bold> Execution starts from the last phase down to the first one - so
     * we traverse the CPU 'backwards', when it comes to component execution !
     */
    public void executeCycle() {

        for (int i = phases.size()-1; i >= 0; i--) {
            for (Component sourceComponent : phases.get(i)) {

                // execute first, to handle the input values and AFTER pass the output to other components
                sourceComponent.execute();

                // for every possible target component that is connected to this source component component
                for (Connector connector : connectors.get(sourceComponent.getLabel())) {
                    String selector = connector.getSelector();

                    components.get(connector.getTo()).setInput(selector, sourceComponent.getOutput(selector));
                }
            }
        }
    }
    
    /**
     * Calculates number of bytes for this CPU's architecture. Example:
     *      Bit size: 32
     *      One byte: 8 bits
     *
     *      returned byte size: 32 / 8 == 4 bytes
     * @return Byte size of this  CPU's architecture
     */
    public static int getByteSize() {
        return CPUBase.getBitSize()/Byte.SIZE;
    }
    
    /**
     * Assign list of components to every phase and then store those phases. A phase consists of all components up to
     * the first appearance of a Latch Register. Phases (except the first one) always start with a latch register.
     */
    private List<List<Component>> splitComponentsIntoPhases() {
        List<List<Component>> phasesList = new ArrayList<>();
        List<Component> currentPhase = new ArrayList<>();
        
        for (Component component : components.values()) {
            if (component instanceof LatchRegister) {
                phasesList.add(currentPhase);
                currentPhase = new ArrayList<>();
            }
            
            currentPhase.add(component);
        }
        phasesList.add(currentPhase);

        return phasesList;
    }
    
    private boolean areValidConnectors(Map<String, List<Connector>> connectors) {
        boolean ret = true;
        
        for (String sourceComponentLabel : connectors.keySet()) {
            Component sourceComponent = components.get(sourceComponentLabel);
            
            // check if component 'from' exists
            if (sourceComponent == null) {
                log.error("Connector invalid: Component FROM `{}` missing", sourceComponentLabel);
                ret = false;
                continue;
            }

            // check if component 'to' exists
            for (Connector connector : connectors.get(sourceComponentLabel)) {
                
                // check if source is multi-output and has the specified output labeled this way
                if (sourceComponent.isSingleOutput() == false  &&
                        sourceComponent.getOutput(connector.getSelector()) == null) {
                    
                    log.error("Connector invalid: Source component `{}` doesn't provide output `{}`",
                            sourceComponentLabel,
                            connector.getSelector()
                    );
                    ret = false;
                    continue;
                }

                Component targetComponent = components.get(connector.getTo());
                
                if (targetComponent == null) {
                    log.error("Connector invalid: Component from {} TO `{}` missing",
                                    sourceComponentLabel,
                                    connector.getTo()
                    );
                    ret = false;
                    continue;
                }
                
                // check if selectors in target inputs exist
                if (targetComponent.isSingleInput() == false &&
                        targetComponent.getInput(connector.getSelector()) == null) {
                    log.error("Connector invalid: From {} | Target component `{}` doesn't provide specified INPUT `{}`",
                            sourceComponentLabel,
                            components.get(connector.getTo()).getLabel(),
                            connector.getSelector());
                    ret = false;
                }
            }
        }
        return ret;
    }


}
