package sk.catheaven.model.instructions;

/**
 * Specifies the field of an instruction. A field has specific bitSize and a label.
 * @author catlord
 */
public class Field {
    private String label;
    private int bitSize;
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
    
    public int getBitSize() {
        return bitSize;
    }
    
    public void setBitSize(int bitSize) {
        this.bitSize = bitSize;
    }
}
