package sk.catheaven.model.aluOperations.operations;

import sk.catheaven.core.Data;
import sk.catheaven.model.aluOperations.OperationImpl;

public class AND extends OperationImpl {
        
        @Override
        public Data perform(Data inputA, Data inputB) {
            Data result = inputA.newInstance();
            result.setData(inputA.getData() & inputB.getData());
            return result;
        }
}
