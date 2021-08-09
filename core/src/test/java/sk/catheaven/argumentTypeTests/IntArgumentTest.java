/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.argumentTypeTests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sk.catheaven.model.instructions.argumentTypes.IntArgumentType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author catlord
 */
public class IntArgumentTest {
	private static IntArgumentType argumentType;
	
	@BeforeAll
	static void setArgumentType() {
		argumentType = new IntArgumentType();
	}
	
	
	@Test
	public void passWhenValid() {
		assertTrue(argumentType.isValidArgument("1"));
		assertTrue(argumentType.isValidArgument("2"));
		assertTrue(argumentType.isValidArgument("3"));
		assertTrue(argumentType.isValidArgument("094"));
		assertTrue(argumentType.isValidArgument("10"));
		assertTrue(argumentType.isValidArgument("18"));
		assertTrue(argumentType.isValidArgument("123"));
		assertTrue(argumentType.isValidArgument("1234"));
		assertTrue(argumentType.isValidArgument("12345"));
		assertTrue(argumentType.isValidArgument("123456"));
		assertTrue(argumentType.isValidArgument("1234567"));
	}
	
	
	@Test
	public void failWhenInvalid() {
		assertFalse(argumentType.isValidArgument("a"));
		assertFalse(argumentType.isValidArgument("1a"));
		assertFalse(argumentType.isValidArgument("a123"));
		assertFalse(argumentType.isValidArgument("1f3fd128"));
		assertFalse(argumentType.isValidArgument("0f"));
		assertFalse(argumentType.isValidArgument("1_2"));
		assertFalse(argumentType.isValidArgument("71.2"));
		assertFalse(argumentType.isValidArgument("7,17"));
		assertFalse(argumentType.isValidArgument("l91236"));
		assertFalse(argumentType.isValidArgument("912o"));
		assertFalse(argumentType.isValidArgument("111111111111111111111111111111111111111111111"));
		assertFalse(argumentType.isValidArgument("`"));
		assertFalse(argumentType.isValidArgument("incorrectNumber"));
		assertFalse(argumentType.isValidArgument("^ff3"));
		assertFalse(argumentType.isValidArgument("125*2318"));
		assertFalse(argumentType.isValidArgument("09)217"));
		assertFalse(argumentType.isValidArgument("21(2515)"));
		assertFalse(argumentType.isValidArgument("1!"));
		assertFalse(argumentType.isValidArgument("210@5"));
		assertFalse(argumentType.isValidArgument("7:"));
	}
}
