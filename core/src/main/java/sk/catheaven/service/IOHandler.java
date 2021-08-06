package sk.catheaven.service;

import sk.catheaven.model.Data;

import java.util.Map;

public class IOHandler {
    public static Data getInputA(Map<?,?> ioMap) {
        return ((Data[]) ioMap.values().toArray())[0];
    }
    
    public static Data getInputB(Map<?,?> ioMap) {
        return ((Data[]) ioMap.values().toArray())[1];
    }
    
    public static Data getSingleOutput(Map<?,?> ioMap) {
        return ((Data[]) ioMap.values().toArray())[0];
    }
    public static Data getSingleInput(Map<?,?> ioMap) {
        return ((Data[]) ioMap.values().toArray())[0];
    }
    public static Data getSingleSelector(Map<?,?> selectorMap) { return ((Data[]) selectorMap.values().toArray())[0]; }
}
