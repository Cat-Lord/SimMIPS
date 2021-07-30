package sk.catheaven.model;

import sk.catheaven.utils.Node;

import java.util.List;
import java.util.Locale;

public class Connector {
    
    public enum WireType {
        THIN,
        NORMAL,
        THICK;
        
        // Returns type specified as string or a default of NORMAL
        public static WireType of(String value) {
            for (WireType type : values()) {
                if (type.name().equalsIgnoreCase(value))
                    return type;
            }
            return WireType.NORMAL;
        }
    }
    
    private String from;
    private String to;
    private String selector;
    private WireType wireType;
    private List<Node> nodes;
    private List<Tuple<String, Data>> content;
    
    public String getFrom() {
        return from;
    }
    
    public void setFrom(String from) {
        this.from = from;
    }
    
    public String getTo() {
        return to;
    }
    
    public void setTo(String to) {
        this.to = to;
    }
    
    public String getSelector() {
        return selector;
    }
    
    public void setSelector(String selector) {
        this.selector = selector;
    }
    
    public WireType getWireType() {
        return wireType;
    }
    
    public void setWireType(String wireType) {
        this.wireType = WireType.of(wireType);
    }
    
    public List<Node> getNodes() {
        return nodes;
    }
    
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
    
    public List<Tuple<String, Data>> getContent() {
        return content;
    }
    
    public void setContent(List<Tuple<String, Data>> content) {
        this.content = content;
    }
}
