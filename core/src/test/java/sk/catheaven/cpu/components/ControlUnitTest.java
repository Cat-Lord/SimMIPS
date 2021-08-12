package sk.catheaven.cpu.components;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.components.ControlUnit;
import sk.catheaven.utils.DataFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author catlord
 */
// TODO - this needs to get bigger -> testing for different opCodes and aluOP values as well as signal extraction
public class ControlUnitTest extends CPUContainer {
	private static ControlUnit cu;
	
	@BeforeAll
	static void prepareComponent() {
		cu = (ControlUnit) cpu.getComponents().get("CU");
	}
	
	@Test
	public void test(){
		Data data = new Data(); // initiate empty data
		cu.setInput(Component.IGNORED_LABEL, data);
		cu.execute();
		
		assertEquals("100 0000 0011", DataFormatter.getBinary(cu.getOutput(Component.IGNORED_LABEL)));
	}
	
}