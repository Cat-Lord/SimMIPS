package sk.catheaven.model.cpu;

import sk.catheaven.core.Coordinates2D;
import sk.catheaven.core.Data;
import sk.catheaven.core.Tuple;
import sk.catheaven.core.cpu.Connector;
import sk.catheaven.core.cpu.WireType;

import java.util.List;

/**
 * Holds information about connection between specific output of one component and
 * specific input of another component. Has information about visuals (wire type
 * and nodes, which are line breakpoints when trying to display this connector).
 *
 * Also contains a special list of 'content' - this provides the connector with
 * additional information about how to handle the data it contains.
 *
 * Example:
 * If a connector contains one 12-bit number which is in turn constructed by a
 * concatenation of 6-, 3- and 3-bit numbers, then every member of 'content'
 * list will know how to extract specific 4-bit number and display its data.
 *
 * So number `10101101010100` will be split into `101011-0101-0100` in this case.
 *
 */
public class ConnectorImpl implements Connector {
    
    private String from;
    private String to;
    private String selector;
    private WireType wireType;
    private List<Coordinates2D> nodes;
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
    
    public List<Coordinates2D> getNodes() {
        return nodes;
    }
    
    public void setNodes(List<Coordinates2D> nodes) {
        this.nodes = nodes;
    }
    
    public List<Tuple<String, Data>> getContent() {
        return content;
    }

    public void reset() {
        for (Tuple<?, Data> contentTuple : getContent())
            contentTuple.getRight().setData(0);
    }
    
    public void setContent(List<Tuple<String, Data>> content) {
        this.content = content;
    }
}
