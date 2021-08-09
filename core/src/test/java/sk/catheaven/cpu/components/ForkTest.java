/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.cpu.components;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.Data;
import sk.catheaven.model.Tuple;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.components.Fork;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author catlord
 */
@DisplayName("Fork should be able to")
public class ForkTest extends CPUContainer {
	private static Data testData;
	
	@BeforeAll
	static void prepareTestInput() {
		testData = new Data(15);
	}
	
	@Test
	@DisplayName("forward output without change")
	public void fork1Test() {
		final var addressOutput = "address";
		final var bitSize = 15;
		
		Fork fork = createFork(bitSize, new Tuple[] {
									new Tuple(addressOutput, new Data(bitSize))
								});
		
		testData.setData(69);
		assertTrue(fork.setInput(Component.IGNORED_LABEL, testData));
		
		fork.execute();
		
		for (int i = 0; i < 10; i++) {
			Data d = fork.getOutput(addressOutput);
			assertEquals(bitSize, d.getBitSize());
			assertEquals(testData.getData(), d.getData());
		}
	}
	
	@Test
	@DisplayName("split the input into 2 separate outputs")
	public void fork2Test() {
		final var bitSize = 15;
		final var extraBitSize = 3;
		
		final String addressOutput = "address";
		final String extraOutput = "extra";
		
		Fork fork = createFork(bitSize, new Tuple[] {
									new Tuple(addressOutput, new Data(bitSize)),
									new Tuple(extraOutput, new Data(extraBitSize))
								});
		
		testData.setData(12345);
		assertTrue(() -> fork.setInput(Component.IGNORED_LABEL, testData));
		fork.execute();
		
		for (int i = 0; i < 20; i++) {
			assertEquals(testData.getData(), fork.getOutput(addressOutput).getData());
			assertEquals(1, fork.getOutput(extraOutput).getData());
		}
	}
	
	@Test
	public void forkWithRangeTest() {
		final var bitSize = 10;
		
		final var addressOutput = "address";
		final var extraOutput  = "extra";
		final var signalOutput = "signal";
		
		Data rangedOutputExtra = new Data(bitSize);
		rangedOutputExtra.setRange(new Tuple<>("2","6"));
		
		Data rangedOutputSignal = new Data(bitSize);
		rangedOutputSignal.setRange(new Tuple<>("0","9"));
		
		Fork forkWithRange = createFork(bitSize, new Tuple[] {
											new Tuple(addressOutput, new Data(bitSize)),
											new Tuple(extraOutput, rangedOutputExtra),
											new Tuple(signalOutput, rangedOutputSignal)
										});
		
		// in binary: 01 1011 1010
		testData.setData(442);
		assertTrue(forkWithRange.setInput(Component.IGNORED_LABEL, testData));
		forkWithRange.execute();
		
		assertEquals(testData.getData(), forkWithRange.getOutput(addressOutput).getData());
		assertEquals(11, forkWithRange.getOutput(extraOutput).getData());
		assertEquals(0, forkWithRange.getOutput(signalOutput).getData());
		
		testData.setData(1022);
		forkWithRange.setInput(Component.IGNORED_LABEL, testData);
		forkWithRange.execute();
		
		assertEquals(testData.getData(), forkWithRange.getOutput(addressOutput).getData());
		assertEquals(15, forkWithRange.getOutput(extraOutput).getData());
		assertEquals(1, forkWithRange.getOutput(signalOutput).getData());
	}
	/**
	 * Testing a fork which has the ability to reduce the input. Key process Example:
	 * 	  Input: 11 bits
	 * Output A: 5 bits		- can be for example further split into more signals later
	 * Output B: 1 bit		- can represent for example control signal in datapath
	 */
	@Test
	public void descendingForkTest(){
		final var bitSize = 11;
		
		final var leftOutput 	= "left";
		final var signalOutput 	= "signal";
		Data rangedSignalOutput = new Data(bitSize);
		rangedSignalOutput.setRange(new Tuple<>("0", "10"));
		
		Fork descFork = createFork(bitSize, new Tuple[] {
										new Tuple(leftOutput, new Data(10)),
										new Tuple(signalOutput, rangedSignalOutput)
									});
		
		testData.setData(1027);
		descFork.setInput(Component.IGNORED_LABEL, testData);
		descFork.execute();
		
		assertEquals(3, descFork.getOutput(leftOutput).getData());
		assertEquals(1, descFork.getOutput(signalOutput).getData());
		
		testData.setData(1859);
		descFork.setInput(Component.IGNORED_LABEL, testData);
		descFork.execute();
		
		assertEquals(835, descFork.getOutput(leftOutput).getData());
		assertEquals(1, descFork.getOutput(signalOutput).getData());
		
		testData.setData(835);
		descFork.setInput(Component.IGNORED_LABEL, testData);
		descFork.execute();
		
		assertEquals(835, descFork.getOutput(leftOutput).getData());
		assertEquals(0, descFork.getOutput(signalOutput).getData());
	}
	
	/**
	 * Utility method to create fork components for testing.
	 * @param inputSize Size of input in bits. The label is not important, since fork is a single-input component
	 * @param outputsArray Array of outputs. Defined as tuple with label (left value) and output data (right value)
	 * @return Fork object
	 */
	private Fork createFork(int inputSize, Tuple<String, Data>[] outputsArray) {
		final String inputLabel = "input";
		Fork fork = new Fork();
		
		fork.getInputs().put(inputLabel, new Data(inputSize));
		for (Tuple<String, Data> output: outputsArray)
			fork.getOutputs().put(output.getLeft(), output.getRight());
		
		
		return fork;
	}
}
