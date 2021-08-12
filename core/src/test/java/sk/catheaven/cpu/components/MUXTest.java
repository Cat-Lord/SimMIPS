package sk.catheaven.cpu.components;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.components.MUX;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author catlord
 */
@DisplayName("MUX should")
public class MUXTest extends CPUContainer {
	static private MUX mux;
	static private Data inputA;
	static private Data inputB;
	static private Data signal;
	
	private final String inputALabel = "newAddress";
	private final String inputBLabel = "branchAddress";
	private final String signalLabel = "branchSignal";
	
	@BeforeAll
	static void prepareComponent() {
		mux = (MUX) cpu.getComponents().get("BRANCH_MUX");
		signal = new Data(1);
		inputA = new Data();
		inputB = new Data();
	}
	
	@BeforeEach
	public void resetComponent() {
		for (Data input : mux.getInputs().values())
			input.setData(0);
		
		mux.getOutput(Component.IGNORED_LABEL).setData(0);
	}
	
	@Test
	@DisplayName("return the first input when signal is NOT active")
	public void signalInactive(){
		signal.setData(0);
		inputA.setData(69);
		inputB.setData(512);
		
		// activate signal
		mux.setInput(signalLabel, signal);
		mux.setInput(inputBLabel, inputB);
		
		mux.setInput(inputALabel, inputA);
		mux.execute();
		assertEquals(inputA.getData(), mux.getOutput(Component.IGNORED_LABEL).getData());
		
		inputA.setData(111);
		mux.setInput(inputALabel, inputA);
		mux.execute();
		assertEquals(inputA.getData(), mux.getOutput(Component.IGNORED_LABEL).getData());
		
		inputA.setData(17903);
		mux.setInput(inputALabel, inputA);
		mux.execute();
		assertEquals(inputA.getData(), mux.getOutput(Component.IGNORED_LABEL).getData());
		
		inputA.setData(91221);
		mux.setInput(inputALabel, inputA);
		mux.execute();
		assertEquals(inputA.getData(), mux.getOutput(Component.IGNORED_LABEL).getData());
	}
	
	@Test
	@DisplayName("return the second input when signal is active")
	public void signalActive() {
		signal.setData(1);
		inputA.setData(1000);
		inputB.setData(9864);
		
		// activate signal
		mux.setInput(signalLabel, signal);
		mux.setInput(inputALabel, inputA);
		
		
		mux.setInput(inputBLabel, inputB);
		mux.execute();
		assertEquals(inputB.getData(), mux.getOutput(Component.IGNORED_LABEL).getData());
		
		inputB.setData(800);
		mux.setInput(inputBLabel, inputB);
		mux.execute();
		assertEquals(inputB.getData(), mux.getOutput(Component.IGNORED_LABEL).getData());
		
		inputB.setData(755);
		mux.setInput(inputBLabel, inputB);
		mux.execute();
		assertEquals(inputB.getData(), mux.getOutput(Component.IGNORED_LABEL).getData());
		
		inputB.setData(120);
		mux.setInput(inputBLabel, inputB);
		mux.execute();
		assertEquals(inputB.getData(), mux.getOutput(Component.IGNORED_LABEL).getData());
		
		inputB.setData(2);
		mux.setInput(inputBLabel, inputB);
		mux.execute();
		assertEquals(inputB.getData(), mux.getOutput(Component.IGNORED_LABEL).getData());
	}
	
}