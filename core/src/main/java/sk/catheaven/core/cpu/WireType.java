package sk.catheaven.core.cpu;

public enum WireType {
    THIN,
    NORMAL,
    THICK;

    public static WireType of(String value) {
        for (WireType type : values()) {
            if (type.name().equalsIgnoreCase(value))
                return type;
        }
        return WireType.NORMAL;
    }
}
