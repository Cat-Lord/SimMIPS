/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.model.components;

import java.util.Map;

/**
 * Component used for mathematical operations. Operations are defined by the <i>aluOp</i> 
 * control signal. ALU producer <i>zeroResult</i>, if the result of any operation was zero.
 * @author catlord
 */
public class ALU extends Component {
	private Map<Integer, String> operations;		// maps numbers to specific operations
	
	public Map<Integer, String> getOperations() {
		return operations;
	}
	
	public void setOperations(Map<Integer, String> operations) {
		this.operations = operations;
	}
	
	//	private Tuple<String, Data> aluOp;			// aluOp control signal
//	private Tuple<String, Data> zeroResult;		// zeroResult signal informing about result being zero

}