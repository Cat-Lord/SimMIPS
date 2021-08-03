package sk.catheaven.model.instructions;

import java.util.List;

public class InstructionType {
    private String label;			// I, R, ...
    private List<Field> fields;
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public List<Field> getFields() {
        return fields;
    }
    
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
