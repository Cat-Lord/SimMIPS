package sk.catheaven.cpu.components;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.components.RegBank;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author catlord
 */
public class RegBankTest extends CPUContainer {
	private static RegBank regBank;
	
	@BeforeAll
	static void prepareComponent() {
		regBank = (RegBank) cpu.getComponents().get("Registers");
	}
	
	@Test
	public void storeAndRetrieveValue(){
		Data signal = new Data(1);
		signal.setData(1);
		
		Data dataToStore = new Data(32);
		dataToStore.setData(69);
		Data destReg = new Data(5);
		destReg.setData(16);
		
		assertTrue(regBank.setInput("regWriteSignal", signal));
		assertTrue(regBank.setInput("destRegValue", dataToStore));
		assertTrue(regBank.setInput("destReg", destReg));				// set destination register and rt the same
		assertTrue(regBank.setInput("rt", destReg));					// to immediately request data after writing it
		
		regBank.execute();
		assertEquals(dataToStore.getData(), regBank.getOutput("reg2Value").getData());
	}
}
