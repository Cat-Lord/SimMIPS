package sk.catheaven.cpu.codeTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
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
 * Tests all instructions with random values defined previously. Instruction 
 * codes are tested against code obtained from MipSIM.
 * @author catlord
 */
@DisplayName("Assembler should")
public class AssembleAllInstructionsTest extends CPUContainer {
	
	@Test
	@DisplayName("correctly assemble every instruction in the supplied instruction set")
	public void test(){
		String code = getCode();
		List<String> expectedICodes = getExpectedICodes();
		
		List<AssembledInstruction> assembledInstructions = assembler.assembleCode(code);
		
		assertNotNull(assembledInstructions);
		assertTrue(assembler.getSyntaxErrors().isEmpty());
		assertFalse(assembledInstructions.isEmpty());

		for(int i = 0; i < assembledInstructions.size(); i++){
			System.out.println(i + ": " + expectedICodes.get(i) + " | Actual: " +
					DataFormatter.getHex(assembledInstructions.get(i).getIcode()));
			assertEquals(expectedICodes.get(i).toUpperCase(Locale.ROOT),
						 DataFormatter.getHex(assembledInstructions.get(i).getIcode()));
		}
	}
	
	private List<String> getExpectedICodes(){
		List<String> iCodes = new ArrayList<>();
		iCodes.add("00000820");
		iCodes.add("20010000");
		iCodes.add("00000824");
		iCodes.add("30010000");
		iCodes.add("10a00034");
		iCodes.add("14000030");
		iCodes.add("0000081a");
		iCodes.add("0000081b");
		iCodes.add("90050005");
		iCodes.add("9401ffff");
		iCodes.add("8c010000");
		iCodes.add("00000818");
		iCodes.add("00000819");
		iCodes.add("48000000");
		iCodes.add("00000827");
		iCodes.add("00000825");
		iCodes.add("34010000");
		iCodes.add("00000801");
		iCodes.add("00000802");
		iCodes.add("00000822");
		iCodes.add("24010005");
		iCodes.add("ac010000");
		iCodes.add("00000826");
		iCodes.add("38010000");
		
		return iCodes;
	}
	
	private String getCode(){
		return """
				ADD r1,r0,r0
				ADDI r1,r0,0000
				AND r1,r0,r0
				ANDI r1,r0,0000
				BEQ r0,r5,nope
				BNEQ r0,r0,nope
				DIV r1,r0,r0
				DIVU r1,r0,r0
				LI r5,0005
				LUI r1,65535
				LW r1,0000(r0)
				MUL r1,r0,r0
				MULU r1,r0,r0
				NOP\s
				NOR r1,r0,r0
				OR r1,r0,r0
				ORI r1,r0,0000
				SLLV r1,r0,r0
				nope:SRLV r1,r0,r0
				SUB r1,r0,r0
				SUBI r1,r0,0005
				SW r1,0000(r0)
				XOR r1,r0,r0
				XORI r1,r0,0000""";
	}
}
