package sk.catheaven.model.aluOperations;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sk.catheaven.model.Data;
import sk.catheaven.model.aluOperations.operations.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "label")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ADD.class),
        @JsonSubTypes.Type(value = AND.class),
        @JsonSubTypes.Type(value = SUB.class),
        @JsonSubTypes.Type(value = BNEQ.class),
        @JsonSubTypes.Type(value = DIV.class),
        @JsonSubTypes.Type(value = DIVU.class),
        @JsonSubTypes.Type(value = LUI.class),
        @JsonSubTypes.Type(value = MUL.class),
        @JsonSubTypes.Type(value = MULU.class),
        @JsonSubTypes.Type(value = NOR.class),
        @JsonSubTypes.Type(value = OR.class),
        @JsonSubTypes.Type(value = SLLV.class),
        @JsonSubTypes.Type(value = SRLV.class),
        @JsonSubTypes.Type(value = SUB.class),
        @JsonSubTypes.Type(value = XOR.class)
})
public class Operation {
    public Operation() { }
    public Operation(Integer code) { setCode(code); }
    
    public Data perform(Data inputA, Data inputB) {
        return new Data();
    }
    
    private int code;
    private String label;
    
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
}
