/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.argumentTypeTests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import sk.catheaven.model.instructions.argumentTypes.DataArgumentType;
/**
 *
 * @author catlord
 */
public class DataArgumentTest {
	private static DataArgumentType argumentType;
	
	@BeforeAll
	static void setArgumentType() {
		argumentType = new DataArgumentType();
	}
	
	@Test
	public void passWhenValid() {
		assertTrue(argumentType.isValidArgument("361(r8)"));
		assertTrue(argumentType.isValidArgument("0000(r0)"));
		assertTrue(argumentType.isValidArgument("62(r27)"));
		assertTrue(argumentType.isValidArgument("0026(r52)"));
		assertTrue(argumentType.isValidArgument("361(r8025)"));
		assertTrue(argumentType.isValidArgument("123123(r8)"));
		assertTrue(argumentType.isValidArgument("0032(r1)"));
		assertTrue(argumentType.isValidArgument("361(r2)"));
		assertTrue(argumentType.isValidArgument("361(r3)"));
		assertTrue(argumentType.isValidArgument("361(r4)"));
		assertTrue(argumentType.isValidArgument("361(r5)"));
		assertTrue(argumentType.isValidArgument("5911(r32)"));
	}
	
	@Test
	public void failWhenInvalid() {
		assertFalse(argumentType.isValidArgument("1"));
		assertFalse(argumentType.isValidArgument("rendom(teXt)"));
		assertFalse(argumentType.isValidArgument("(r1)"));
		assertFalse(argumentType.isValidArgument("f(r2)"));
		assertFalse(argumentType.isValidArgument("123()"));
		assertFalse(argumentType.isValidArgument("123(r1"));
		assertFalse(argumentType.isValidArgument("6(r)"));
		assertFalse(argumentType.isValidArgument("fff(r8)"));
		assertFalse(argumentType.isValidArgument(""));
		assertFalse(argumentType.isValidArgument("2a3(r81)"));
		assertFalse(argumentType.isValidArgument("3(e81)"));
		assertFalse(argumentType.isValidArgument("02(95)"));
		assertFalse(argumentType.isValidArgument("r32(r32)"));
	}
}
