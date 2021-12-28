package sk.catheaven.core.model;

import sk.catheaven.utils.Coordinates2D;

import java.util.List;

public class Connector {
    public enum WireType {
        THIN,
        NORMAL,
        THICK;

        // Returns type specified as string or a default of NORMAL
        public static Connector.WireType of(String value) {
            for (Connector.WireType type : values()) {
                if (type.name().equalsIgnoreCase(value))
                    return type;
            }
            return Connector.WireType.NORMAL;
        }
    }

    public Connector(List<Coordinates2D> nodes) {
        this.nodes = nodes;
    }

    private final List<Coordinates2D> nodes;

    public List<Coordinates2D> getNodes() {
        return nodes;
    }
}
