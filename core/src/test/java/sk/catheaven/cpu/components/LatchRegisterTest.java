package sk.catheaven.cpu.components;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.components.LatchRegister;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author catlord
 */
@DisplayName("Latch register should")
public class LatchRegisterTest extends CPUContainer {
	private static LatchRegister memWb;
	private static LatchRegister idEx;
	private static LatchRegister ifId;
	private static Data data;
	
	@BeforeAll
	static void prepareComponents() {
		memWb = (LatchRegister) cpu.getComponents().get("MEM_WB");
		// here just check the creation, it's almost the same as "MEM_WB"
		LatchRegister exMem = (LatchRegister) cpu.getComponents().get("EX_MEM");
		idEx = (LatchRegister) cpu.getComponents().get("ID_EX");
		ifId = (LatchRegister) cpu.getComponents().get("IF_ID");
		
		data = new Data();
	}
	
	@Test
	@DisplayName("simply forwarded multiple stored values")
	public void memoryWriteBackLatch() {
		final String controlSignalslabel =  "controlSignals";
		final String memDatalabel =  "memData";
		final String aluResultlabel =  "aluResult";
		final String destReglabel =  "destReg";
		
		data.setData(123);
		memWb.setInput(controlSignalslabel, data);
		
		data.setData(11);
		memWb.setInput(memDatalabel, data);
		
		data.setData(821);
		memWb.setInput(aluResultlabel, data);
		
		// remember -> this input is 5 bits wide, so data will get truncated
		data.setData(15035);
		memWb.setInput(destReglabel, data);
	
		memWb.execute();
		
		assertEquals(123 ,memWb.getOutput(controlSignalslabel).getData());
		assertEquals(11,  memWb.getOutput(memDatalabel).getData());
		assertEquals(821, memWb.getOutput(aluResultlabel).getData());
		assertEquals(27,  memWb.getOutput(destReglabel).getData());
	}
	
	@Test
	@DisplayName("simply forwar single stored value")
	public void decodeAndExecuteLatch() {
		data.setData(69);
		idEx.setInput("reg1Value", data);
		idEx.execute();
		assertEquals(69,idEx.getOutput("aluInputA").getData());
	}
	
	@Test
	@DisplayName("extract necessary information from inputs and provide new outputs")
	public void fetchAndDecodeLatch() {
		data.setData(0x20916931); // instruction is: ADDI $17,$4,6931, imm value is 26 929
		ifId.setInput("iCode", data);
		ifId.execute();
		
		assertEquals(4, ifId.getOutput("rs").getData());
		assertEquals(17, ifId.getOutput("rt").getData());
		assertEquals(13, ifId.getOutput("rd").getData());
		assertEquals(26929,ifId.getOutput("immediateValue").getData());
	}
}
