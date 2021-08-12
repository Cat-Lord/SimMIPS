package sk.catheaven.model.cpu.components;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.Connector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CPU {
    private static Logger log = LogManager.getLogger();
    
    public static int BIT_SIZE = 32;
    private Map<String, Component> components = new LinkedHashMap<>();
    private Map<String, List<Connector>> connectors;
    private List<List<Component>> phases;
    
    public CPU() {
    }
    
    public Map<String, Component> getComponents() {
        return components;
    }
    
    public void setComponents(Map<String, Component> components) {
        this.components = components;
        
        this.phases = splitComponentsIntoPhases();
    }
    
    @JsonGetter
    public static int getBitSize() {
        return CPU.BIT_SIZE;
    }
    
    @JsonSetter("BIT_SIZE")
    public void setBitSize(int bitSize) {
        CPU.BIT_SIZE = bitSize;
    }
    
    public Map<String, List<Connector>> getConnectors() {
        return connectors;
    }
    
    public void setConnectors(Map<String, List<Connector>> connectors) {
        if (areValidConnectors(connectors))
            this.connectors = connectors;
    }
    
    public List<List<Component>> getPhases() {
        return phases;
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
        return CPU.getBitSize()/Byte.SIZE;
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
                if (sourceComponent.isSingleOutputComponent() == false  &&
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
                if (targetComponent.isSingleInputComponent() == false &&
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
