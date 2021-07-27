package sk.catheaven.model.components;

import java.util.ArrayList;
import java.util.List;

public class CPU {
    public static int BIT_SIZE = 32;
    private List<Component> components;
    
    public CPU() {
        components = new ArrayList<>();
    }
    
    public List<Component> getComponents() {
        return components;
    }
    
    public void setComponents(List<Component> components) {
        this.components = components;
    }
    
    public static int getBitSize() {
        return BIT_SIZE;
    }
    
    public static void setBitSize(int bitSize) {
        BIT_SIZE = bitSize;
    }
}
