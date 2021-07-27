package sk.catheaven.model.components;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

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
    
    @JsonGetter
    public static int getBitSize() {
        return CPU.BIT_SIZE;
    }
    
    @JsonSetter("BIT_SIZE")
    public void setBitSize(int bitSize) {
        CPU.BIT_SIZE = bitSize;
    }
}
