/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.cpu.codeTests;


import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.Data;
import sk.catheaven.service.AssembledInstruction;
import sk.catheaven.utils.DataFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author catlord
 */
public class AssembleWholeCodeTest extends CPUContainer {
	
	@Test
	public void assembleCodeAndTestInstructionAndLabelValidity() {
		List<AssembledInstruction> instructionsList = assembler.assembleCode(loadCode());
		
		assertNotNull(instructionsList);
		assertFalse(instructionsList.isEmpty());
		assertTrue(assembler.getSyntaxErrors().isEmpty());
		
		// checking label addresses
		Map<String, Data> labels = assembler.getLabels();

		assertEquals("00000010", DataFormatter.getHex(labels.get("cat")));
		assertEquals("0000002C", DataFormatter.getHex(labels.get("next")));
		assertEquals("00000034", DataFormatter.getHex(labels.get("super_label_version_million")));
		assertEquals("0000005C", DataFormatter.getHex(labels.get("ending")));
		
		List<String> expectedIcodes = getExpectedIcodesList();
		for (int i = 0; i < expectedIcodes.size(); i++) {
			
			System.out.println(
					i + ": " + expectedIcodes.get(i).toUpperCase(Locale.ROOT) +
					" | Actual: " + DataFormatter.getHex(instructionsList.get(i).getIcode())
			);
			
			assertEquals(
					expectedIcodes.get(i).toUpperCase(Locale.ROOT),
					DataFormatter.getHex(instructionsList.get(i).getIcode())
			);
		}
		
	}
	
	private List<String> getExpectedIcodesList() {
		List<String> expectedIcodes = new ArrayList<>();
		// expected values are from MipSIM (previous version)
		expectedIcodes.add("25010006");
		expectedIcodes.add("90060006");
		expectedIcodes.add("02e28818");
		expectedIcodes.add("00263001");
		expectedIcodes.add("003e0822");
		expectedIcodes.add("00230820");
		expectedIcodes.add("9007009a"); 	// li instruction input value is 10-base
		expectedIcodes.add("14e1fff0");
		expectedIcodes.add("1021ffec");
		expectedIcodes.add("10000004");
		expectedIcodes.add("900103b7");
		expectedIcodes.add("8cc6007b"); 	// same for offset in lw
		expectedIcodes.add("10000000");
		expectedIcodes.add("00213019");
		expectedIcodes.add("00210819");
		
		expectedIcodes.add("48000000");
		expectedIcodes.add("48000000");
		expectedIcodes.add("48000000");
		expectedIcodes.add("10000010");
		
		expectedIcodes.add("48000000");
		expectedIcodes.add("48000000");
		expectedIcodes.add("48000000");
		expectedIcodes.add("48000000");
		expectedIcodes.add("00270820");
		expectedIcodes.add("1000ffac");
		return expectedIcodes;
	}
	
	private String loadCode(){
		return """
				
				
				subi r1, r8, 6
				li r6, 6
				mul r17, r23,r2
				sllv r6, r1, r6
				
				cat:                 sub r1, r1 ,r30
				add r1, r1, r3
				li r7, 154
				bneq r1, r7, cat
				beq r1, r1, cat
				beq r0, r0, next
				
				li r1, 951
				next: lw r6, 123(r6)
				beq r0, r0, super_label_version_million
				
				super_label_version_million:\s
				; now a little comment for this awesome example
				mulu r6, r1, r1
				mulu r1, r1, r1
				nop
				nop
				nop
				beq r0, r0, ending
				nop
				nop
				nop
				nop
				ending:add r1, r1, r7
				beq r0, r0, cat""";
	}
}
