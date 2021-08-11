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
import sk.catheaven.model.cpu.Component;
import sk.catheaven.model.cpu.components.SignExtend;
import sk.catheaven.utils.DataFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author catlord
 */
@DisplayName("Sign extender should be able to")
public class SignExtendTest extends CPUContainer {
	private static SignExtend signExtend;
	private static Data data;
	
	@BeforeAll
	static void prepareComponent() {
		signExtend = (SignExtend) cpu.getComponents().get("SE");
		data = new Data(16);
	}
	/**
	 * Testing border cases - when the left-most bit is one, all the extended bits must be 1's as well.
	 * If that bit is zero, expansion happens with all 0's.
	 */
	@Test
	@DisplayName("pad the number with 0's from the left when left-most bit is 0")
	public void extendedWithZero() {
		data.setData((-1 >>> 17));		// left-most bit 0, other bits 1, but writing 1 gives 32 bit number, so shift 17 right to erase the left-most 1
		signExtend.setInput(Component.IGNORED_LABEL, data);
		signExtend.execute(); assertEquals("0000 0000 0000 0000 0111 1111 1111 1111", 
											DataFormatter.getBinary(signExtend.getOutput(Component.IGNORED_LABEL)));
		
		
		data.setData(69);				// test for a random common number
		signExtend.setInput(Component.IGNORED_LABEL, data);
		signExtend.execute(); assertEquals("0000 0000 0000 0000 0000 0000 0100 0101", 
											DataFormatter.getBinary(signExtend.getOutput(Component.IGNORED_LABEL)));
	}
	
	@Test
	@DisplayName("pad the number with 1's from the left when left-most bit is 1")
	public void extendedWithOne() {
		data.setData(-1);				// all ones
		signExtend.setInput(Component.IGNORED_LABEL, data);
		signExtend.execute(); assertEquals("1111 1111 1111 1111 1111 1111 1111 1111", 
											DataFormatter.getBinary(signExtend.getOutput(Component.IGNORED_LABEL)));
		
		data.setData((1 << 15));		// left-most bit 1, other bits 0
		signExtend.setInput(Component.IGNORED_LABEL, data);
		signExtend.execute(); assertEquals("1111 1111 1111 1111 1000 0000 0000 0000", 
											DataFormatter.getBinary(signExtend.getOutput(Component.IGNORED_LABEL)));
	}
}
