package sk.catheaven.model.cpu.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sk.catheaven.core.Data;
import sk.catheaven.core.cpu.Operation;
import sk.catheaven.model.aluOperations.operations.BNEQ;
import sk.catheaven.model.cpu.ComponentImpl;
import sk.catheaven.service.IOHandler;

import java.util.Map;

/**
 * Component used for mathematical operations. Operations are defined by the <i>aluOp</i> 
 * control signal. ALU producer <i>zeroResult</i>, if the result of any operation was zero.
 * @author catlord
 */
public class ALU extends ComponentImpl {
	private static Logger log = LogManager.getLogger();
	
	private Map<Integer, Operation> operations;		// maps numbers to specific mathematical operations
	private Data aluOp;				// aluOp control signal
	private Data zeroResultSignal;	// zeroResult signal informing about result being zero
	
	@Override
	public void execute() {
		zeroResultSignal.setData(0);
		Operation operation = operations.get(aluOp.getData());
		
		if(operation == null){
			log.warn("Unknown operation code {}", aluOp.getData());
			return;
		}
		
		Data inputA = IOHandler.getInputA(getInputs());
		Data inputB = IOHandler.getInputB(getInputs());
		
		Data output = IOHandler.getSingleValue(getOutputs());
		output.setData(
			operation.perform(inputA, inputB)
		);
		
		// apart from bneq operation, set zero result signal if the result was actually zero
		if (operation.getClass() == BNEQ.class) {
			if (output.getData() != 0)
				zeroResultSignal.setData(1);
		}
		else if (output.getData() == 0)
			zeroResultSignal.setData(1);
			
	}
	
	
	public Map<Integer, Operation> getOperations() {
		return operations;
	}
	
	public void setOperations(Map<Integer, Operation> operations) {
		this.operations = operations;
	}
	
	public Data getAluOp() {
		return aluOp;
	}
	
	public void setAluOp(Data aluOp) {
		this.aluOp = aluOp;
		getInputs().put("aluOp", this.aluOp);
	}
	
	public Data getZeroResultSignal() {
		return zeroResultSignal;
	}
	
	public void setZeroResultSignal(Data zeroResultSignal) {
		this.zeroResultSignal = zeroResultSignal;
		getOutputs().put("zeroResultSignal", this.zeroResultSignal);
	}
}