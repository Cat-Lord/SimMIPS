package sk.catheaven.model.instructions;

import java.util.List;

/**
 * Represents instruction type and stores all required instruction fields for that type
 * along with its bit size. Server the purpose to know, which fields to require from
 * which instruction type and what sizes are the fields of.
 * @author catlord
 */
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
