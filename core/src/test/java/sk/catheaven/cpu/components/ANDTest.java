/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.cpu.components;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.Data;
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.components.AND;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author catlord
 */
public class ANDTest extends CPUContainer {
	private static AND and;
	private static Data inputA, inputB;
	
	@BeforeAll
	static void prepareComponent() {
		and = (AND) cpu.getComponents().get("&");
		inputA = new Data();
		inputB = new Data();
	}
	
	@Test
	public void testResult0() {
		inputA.setData(22637124);
		inputB.setData(32123);
		
		assertTrue(and.setInput("branchSignal", inputA));
		assertTrue(and.setInput("zeroResultSignal", inputB));
		and.execute();
		assertEquals(0, and.getOutput(Component.IGNORED_LABEL).getData());
	}
	
	@Test
	public void testResult1() {
		inputA.setData(31);
		inputB.setData(63);
		
		assertTrue(and.setInput("branchSignal", inputA));
		assertTrue(and.setInput("zeroResultSignal", inputB));
		and.execute();
		assertEquals(1, and.getOutput(Component.IGNORED_LABEL).getData());
	}
}
