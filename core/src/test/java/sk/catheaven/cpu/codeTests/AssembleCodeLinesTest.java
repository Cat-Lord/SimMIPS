/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.cpu.codeTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.Tuple;
import sk.catheaven.model.instructions.AssembledInstruction;
import sk.catheaven.utils.DataFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author catlord
 */
@DisplayName("Assembler should")
public class AssembleCodeLinesTest extends CPUContainer {

	@Nested
	@DisplayName("successfully assemble user-written lines of code")
	public class CorrectlyFormattedInstruction {
		
		private void instructionShouldSucceed(String instructionLine, String expectedResult) {
			List<AssembledInstruction> assembledInstructions = assembler.assembleCode(instructionLine);
			assertNotNull(assembledInstructions);
			assertFalse(assembledInstructions.isEmpty());
			
			AssembledInstruction ai = assembledInstructions.get(0);
			assertNotNull(ai, assemblerErrorSupplier());
			assertNotNull(ai.getIcode(), assemblerErrorSupplier());
			assertEquals(expectedResult.toUpperCase(Locale.ROOT),
						 DataFormatter.getHex(ai.getIcode()).toUpperCase(Locale.ROOT),
						 assemblerErrorSupplier()
				);
		}
		
		@Test
		public void correctlyFormattedInstructions() {
			for (Tuple<String, String> correctInstruction : getCorrectlyFormattedInstructions())
				instructionShouldSucceed(correctInstruction.getLeft(), correctInstruction.getRight());
		}
		
		@Test
		public void assembleCorrectlyWithWeirdInlineSpacing() {
			// testing with spacing (spaces in various places, yo)
			instructionShouldSucceed("add r0     , r0,  r0", "00000020");
			instructionShouldSucceed("		add        r0     , r0,  r0	", "00000020");
			instructionShouldSucceed("add r0,r0			,  r0", "00000020");
			instructionShouldSucceed("add			 r0, r0   ,			 r0", "00000020");
			instructionShouldSucceed("add			 r0, r0   ,			 r0 ;veav aev a vawe ad !2 f3 ion26 @\"<>:3", "00000020");
			instructionShouldSucceed("add			 r0, r0   ,			 r0			;veav aev a vawe ad !2 f3 ion26 @\"<>:3", "00000020");
			instructionShouldSucceed("add			 r0, r0   ,			 r0		   ;  add r1, r1 ,r12dd veav aev a vawe ad !2 f3 ion26 @\"<>:3", "00000020");
		}
		
		@Test
		public void assembleCorrectlyLinesWithImmediateValue() {
			instructionShouldSucceed("addi r1, r0, 0", 		"20010000");
			instructionShouldSucceed("andi r1, r0, 00000",  "30010000");
			instructionShouldSucceed("li r5, 5",			"90050005");
			instructionShouldSucceed("lui r1, 65535", 		"9401ffff");            // imm == ffff
			instructionShouldSucceed("ori r1, r0, 0000", 	"34010000");
			instructionShouldSucceed("subi r1, r0, 5", 		"24010005");
			instructionShouldSucceed("xori r1, r0, 000", 	"38010000");
		}
		
		private List<Tuple<String, String>> getCorrectlyFormattedInstructions() {
			List<Tuple<String, String>> correctlyFormattedInstructions = new ArrayList<>();
			correctlyFormattedInstructions.add(new Tuple<>("add r0, r0, r0", "00000020"));
			correctlyFormattedInstructions.add(new Tuple<>("and r1,r0,r0", "00000824"));
			correctlyFormattedInstructions.add(new Tuple<>("div r1,r0,r0", "0000081a"));
			correctlyFormattedInstructions.add(new Tuple<>("divu r1,r0,r0", "0000081b"));
			correctlyFormattedInstructions.add(new Tuple<>("mul r1,r0,r0", "00000818"));
			correctlyFormattedInstructions.add(new Tuple<>("mulu r1,r0,r0", "00000819"));
			correctlyFormattedInstructions.add(new Tuple<>("nor r1,r0,r0", "00000827"));
			correctlyFormattedInstructions.add(new Tuple<>("or r1,r0,r0", 	"00000825"));
			correctlyFormattedInstructions.add(new Tuple<>("sllv r1,r0,r0", "00000801"));
			correctlyFormattedInstructions.add(new Tuple<>("srlv r1,r0,r0", "00000802"));
			correctlyFormattedInstructions.add(new Tuple<>("sub r1,r0,r0", "00000822"));
			correctlyFormattedInstructions.add(new Tuple<>("xor r1,r0,r0", "00000826"));
			return correctlyFormattedInstructions;
		}
	}
	
	
	@Nested
	@DisplayName("fail on incorrectly written code lines")
	public class IncorrectlyFormattedInstruction {

		@Test
		public void instructionsShouldFail() {
			for (String incorrectInstruction : getIncorrectInstructions()) {
				List<AssembledInstruction> assembledInstructions = assembler.assembleCode(incorrectInstruction);
				
				assertTrue(assembledInstructions.isEmpty());
				if (incorrectInstruction.isBlank() == false)			// blank lines are ignored without giving errors
					assertFalse(assembler.getSyntaxErrors().isEmpty(), "`" + incorrectInstruction + "`");
				
				assembler.getSyntaxErrors().clear();
			}
		}
		
		private List<String> getIncorrectInstructions() {
			List<String> incorrectInstructions = new ArrayList<>();
			incorrectInstructions.add("");
			incorrectInstructions.add("add");
			incorrectInstructions.add("add add, add, add");
			incorrectInstructions.add("add r1");
			incorrectInstructions.add("add r1,");
			incorrectInstructions.add("add r1, r2");
			incorrectInstructions.add("add r1, r2, ");
			incorrectInstructions.add("add r1,; r2, r1");
			incorrectInstructions.add("add r1, r2, ; r1");
			incorrectInstructions.add(" a;dd r1, r2, ; r1");
			incorrectInstructions.add("add e1, r2, r3");
			incorrectInstructions.add("add r1, r2, r3, r4");
			incorrectInstructions.add("add r, 1, 6r");
			incorrectInstructions.add("addd r1, r2, r2");
			incorrectInstructions.add("addi r6, r2");
			incorrectInstructions.add("addi r4, 51326");
			incorrectInstructions.add("addi 56215, 4214");
			incorrectInstructions.add("				addi	 r5,			 3f142h");
			incorrectInstructions.add("l	w r1, r1");
			incorrectInstructions.add("lw 1. 1)r1_");
			incorrectInstructions.add("sw 6r, 1(tg)");
			incorrectInstructions.add("sw r1, 12		095()");
			incorrectInstructions.add("sw , 52(r1)");
			incorrectInstructions.add("sw r84, 907g(r632)");
			incorrectInstructions.add("lw r2, 6136(r1b2)");
			incorrectInstructions.add("lw r2, 6136  (b2)");
			incorrectInstructions.add("lw r2, 6136(  r1b2   )");
			incorrectInstructions.add("lw r2, 6136, (r1b2)");
			incorrectInstructions.add("lw r2, 6136(r1b2)(r2)");
			incorrectInstructions.add("li r1. 21 152");
			incorrectInstructions.add("beq r5, r1, 1");
			incorrectInstructions.add("beq r6, r1, incorrect label");
			incorrectInstructions.add("bNeQ r1r6, r1, flabel");
			incorrectInstructions.add("bNeQ r1r6, r1, flabel:lavel<>2?");
			return incorrectInstructions;
		}
	}
}
