package sk.catheaven.model.instructions;

import sk.catheaven.core.instructions.Field;

/**
 * Specifies the field of an instruction. A field has specific bitSize and a label.
 * @author catlord
 */
public class FieldImpl implements Field {
    private String label;
    private int bitSize;
    
    @Override
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    @Override
    public int getBitSize() {
        return bitSize;
    }
    
    public void setBitSize(int bitSize) {
        this.bitSize = bitSize;
    }
}
