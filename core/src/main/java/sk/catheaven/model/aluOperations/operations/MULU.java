package sk.catheaven.model.aluOperations.operations;

import sk.catheaven.core.Data;
import sk.catheaven.model.DataImpl;
import sk.catheaven.model.aluOperations.OperationImpl;

/**
 * MULU: Multiply unsigned
 * todo - test
 */
public class MULU extends OperationImpl {
    
    @Override
    public Data perform(Data inputA, Data inputB) {
        Data result = new DataImpl();
        result.setData(
            (int) (Integer.toUnsignedLong(inputA.getData()) * Integer.toUnsignedLong(inputB.getData()))
        );
        return result;
    }
}