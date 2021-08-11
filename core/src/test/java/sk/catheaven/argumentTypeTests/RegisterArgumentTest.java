/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.argumentTypeTests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import sk.catheaven.model.instructions.argumentTypes.RegisterArgumentType;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Testing register argument type parsing.
 * @author catlord
 */
public class RegisterArgumentTest {
	
	private static RegisterArgumentType argumentType;
	
	@BeforeAll
	static void setArgumentType() {
		argumentType = new RegisterArgumentType();
	}
	
	@Test
	public void passWhenValid() {
		for (int i = 0; i < 1000; i++) {
			assertTrue(argumentType.isValidArgument("r"+ i));
			assertTrue(argumentType.isValidArgument("R"+ i));
		}
	}
	
	@Test
	public void failWhenInvalid() {
		assertFalse(argumentType.isValidArgument("2"));
		assertFalse(argumentType.isValidArgument("r"));
		assertFalse(argumentType.isValidArgument("rr2"));
		assertFalse(argumentType.isValidArgument("i3"));
		assertFalse(argumentType.isValidArgument("reg3"));
		assertFalse(argumentType.isValidArgument("3r2"));
		assertFalse(argumentType.isValidArgument("2r2"));
		assertFalse(argumentType.isValidArgument("d7"));
	}
}
