package sk.catheaven.core.cpu;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import sk.catheaven.core.Data;
import sk.catheaven.model.aluOperations.OperationImpl;

@JsonDeserialize(as = OperationImpl.class)
public interface Operation {
    Data perform(Data inputA, Data inputB);

    int getCode();

    String getLabel();
}
