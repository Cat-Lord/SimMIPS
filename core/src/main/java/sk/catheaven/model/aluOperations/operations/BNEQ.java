package sk.catheaven.model.aluOperations.operations;

import sk.catheaven.core.Data;
import sk.catheaven.model.aluOperations.OperationImpl;

/**
 * BNEQ: Branch not equal
 * Performs a subtraction operation and if result is NOT zero, that means the numbers
 * were not equal and thus we allow branching.
 */
public class BNEQ extends OperationImpl {
    @Override
    public Data perform(Data inputA, Data inputB) {
        Data result = inputA.newInstance();
        result.setData( inputA.getData() - inputB.getData() );
        return result;
    }
}