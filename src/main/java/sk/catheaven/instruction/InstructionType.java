package sk.catheaven.instruction;

import java.util.List;

public class InstructionType {
    private String type;			// I, R, ...
    private List<Field> fields;
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public List<Field> getFields() {
        return fields;
    }
    
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
