package sk.catheaven.model.aluOperations.operations;

import sk.catheaven.model.Data;
import sk.catheaven.model.aluOperations.Operation;

public class DIVU extends Operation {
    
    @Override
    public Data perform(Data inputA, Data inputB) {
        Data result = new Data();
        
        if (inputB.getData() != 0)
            result.setData(
                    (int) (Integer.toUnsignedLong(inputA.getData()) / Integer.toUnsignedLong(inputB.getData()))
            );
        return result;
    }
}
