package sk.catheaven.model.aluOperations.operations;

import sk.catheaven.model.Data;
import sk.catheaven.model.aluOperations.Operation;

/**
 * MULU: Multiply unsigned
 * todo - test
 */
public class MULU extends Operation {
    
    @Override
    public Data perform(Data inputA, Data inputB) {
        Data result = new Data();
        result.setData(
            (int) (Integer.toUnsignedLong(inputA.getData()) * Integer.toUnsignedLong(inputB.getData()))
        );
        return result;
    }
}