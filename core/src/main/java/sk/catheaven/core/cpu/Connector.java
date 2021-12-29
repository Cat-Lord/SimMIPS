package sk.catheaven.core.cpu;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import sk.catheaven.core.Coordinates2D;
import sk.catheaven.model.cpu.ConnectorImpl;

import java.util.List;

@JsonDeserialize(as = ConnectorImpl.class)
public interface Connector {
    String getFrom();
    String getTo();
    String getSelector();
    List<Coordinates2D> getNodes();
    WireType getWireType();
    void reset();
}
