package sk.catheaven.model.aluOperations;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import sk.catheaven.core.Data;
import sk.catheaven.core.cpu.Operation;
import sk.catheaven.model.DataImpl;
import sk.catheaven.model.aluOperations.operations.ADD;
import sk.catheaven.model.aluOperations.operations.AND;
import sk.catheaven.model.aluOperations.operations.BNEQ;
import sk.catheaven.model.aluOperations.operations.DIV;
import sk.catheaven.model.aluOperations.operations.DIVU;
import sk.catheaven.model.aluOperations.operations.LUI;
import sk.catheaven.model.aluOperations.operations.MUL;
import sk.catheaven.model.aluOperations.operations.MULU;
import sk.catheaven.model.aluOperations.operations.NOR;
import sk.catheaven.model.aluOperations.operations.OR;
import sk.catheaven.model.aluOperations.operations.SLLV;
import sk.catheaven.model.aluOperations.operations.SRLV;
import sk.catheaven.model.aluOperations.operations.SUB;
import sk.catheaven.model.aluOperations.operations.XOR;

/**
 * Operation on 2 inputs. Label denotes the name of this operation and perform
 * is the execution of that operation.
 */
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
public class OperationImpl implements Operation {
    public OperationImpl() { }
    public OperationImpl(Integer code) { setCode(code); }
    
    @Override
    public Data perform(Data inputA, Data inputB) {
        return new DataImpl();
    }
    
    private int code;
    private String label;
    
    @Override
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    @Override
    public String getLabel() {
        return label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }
}
