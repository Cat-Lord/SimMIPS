package sk.catheaven.service;

import sk.catheaven.model.Data;

import java.util.Map;

public class IOHandler {
    public static Data getInputA(Map<String, Data> ioMap) {
        Data[] data = getArrayFromMap(ioMap);
        return data[0];
    }
    
    public static Data getInputB(Map<String, Data> ioMap) {
        Data[] data = getArrayFromMap(ioMap);
        return data[1];
    }

    public static Data getSingleValue(Map<String, Data> ioMap) {
        Data[] data = getArrayFromMap(ioMap);
        return data[0];
    }
    
    public static Data getSingleSelector(Map<String, Data> selectorMap) {
        Data[] data = getArrayFromMap(selectorMap);
        return data[0];
    }
    
    private static Data[] getArrayFromMap(Map<String, Data> map) {
        return map.values().toArray(Data[]::new);
    }
}
