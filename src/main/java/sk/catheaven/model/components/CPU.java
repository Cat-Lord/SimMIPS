package sk.catheaven.model.components;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.model.Connector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CPU {
    private static Logger log = LogManager.getLogger();
    
    public static int BIT_SIZE = 32;
    private Map<String, Component> components = new LinkedHashMap<>();
    private List<Connector> connectors = new ArrayList<>();
    
    public CPU() {
    }
    
    public Map<String, Component> getComponents() {
        return components;
    }
    
    public void setComponents(Map<String, Component> components) {
        this.components = components;
    }
    
    @JsonGetter
    public static int getBitSize() {
        return CPU.BIT_SIZE;
    }
    
    @JsonSetter("BIT_SIZE")
    public void setBitSize(int bitSize) {
        CPU.BIT_SIZE = bitSize;
    }
    
    public List<Connector> getConnectors() {
        return connectors;
    }
    
    public void setConnectors(List<Connector> connectors) {
        if (areValidConnectors(connectors))
            this.connectors = connectors;
    }
    
    private boolean areValidConnectors(List<Connector> connectors) {
        boolean ret = true;
        for (Connector connector : connectors) {
            
            // check if component 'from' exists
            if (components.get(connector.getFrom()) == null) {
                log.error("Connector invalid: Component FROM `{}` missing", connector.getFrom());
                ret = false;
            }
            
            // check if component 'from' exists
            if (components.get(connector.getTo()) == null) {
                log.error("Connector invalid: Component TO `{}` missing", connector.getFrom());
                ret = false;
            }
            
            // check if selectors exist
            if (components.get(connector.getFrom()).getOutputs().get(connector.getSelector()) == null) {
                log.error("Component FROM `{}` doesn't provide specified OUTPUT `{}`",
                        components.get(connector.getFrom()).getLabel(),
                        connector.getSelector());
                ret = false;
            }
        }
        return ret;
    }
}
