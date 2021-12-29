package sk.catheaven.model.aluOperations.operations;

import sk.catheaven.core.Data;
import sk.catheaven.model.DataImpl;
import sk.catheaven.model.aluOperations.OperationImpl;

public class DIVU extends OperationImpl {
    
    @Override
    public Data perform(Data inputA, Data inputB) {
        Data result = new DataImpl();
        
        if (inputB.getData() != 0)
            result.setData(
                    (int) (Integer.toUnsignedLong(inputA.getData()) / Integer.toUnsignedLong(inputB.getData()))
            );
        return result;
    }
}
