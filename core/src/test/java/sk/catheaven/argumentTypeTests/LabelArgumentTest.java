/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.argumentTypeTests;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import sk.catheaven.model.instructions.argumentTypes.LabelArgumentType;

/**
 *
 * @author catlord
 */
public class LabelArgumentTest {
	private static LabelArgumentType argumentType;
	
	@BeforeAll
	static void setArgumentType() {
		argumentType = new LabelArgumentType();
	}
	
	@Test
	public void passWhenValid() {
		assertTrue(argumentType.isValidArgument("correctLabel"));
		assertTrue(argumentType.isValidArgument("correctLabel24680"));
		assertTrue(argumentType.isValidArgument("correctLabel_15_AAAAAA"));
		assertTrue(argumentType.isValidArgument("correctLabel_correctlabel_123_label_correct"));
		assertTrue(argumentType.isValidArgument("l"));
		assertTrue(argumentType.isValidArgument("it"));
	}
	
	@Test
	public void failWhenInvalid() {
		assertFalse(argumentType.isValidArgument("[incorrectLabel"));
		assertFalse(argumentType.isValidArgument("incorrectLabel["));
		assertFalse(argumentType.isValidArgument("incorrectLabel(ss)"));
		assertFalse(argumentType.isValidArgument("incorrectLabel;"));
		assertFalse(argumentType.isValidArgument("incorrectLabel`"));
		assertFalse(argumentType.isValidArgument("incorrectLabel'"));
		assertFalse(argumentType.isValidArgument("incorrectLabel\\"));
		assertFalse(argumentType.isValidArgument("incorrectLabel<"));
		assertFalse(argumentType.isValidArgument("incorrect<3Label"));
		assertFalse(argumentType.isValidArgument("incorrect?Label"));
		assertFalse(argumentType.isValidArgument("incorrect-Label"));
		assertFalse(argumentType.isValidArgument("1"));
		assertFalse(argumentType.isValidArgument("90165"));
		assertFalse(argumentType.isValidArgument("?"));
	}
}
