package sk.catheaven.model.aluOperations.operations;

import sk.catheaven.model.Data;
import sk.catheaven.model.aluOperations.Operation;

public class ADD extends Operation {
    @Override
    public Data perform(Data inputA, Data inputB) {
        Data result = inputA.newInstance();
        result.setData(inputA.getData() + inputB.getData());
        return result;
    }
}
