package sk.catheaven.cpu.codeTests;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.Data;
import sk.catheaven.model.Tuple;
import sk.catheaven.model.instructions.AssembledInstruction;
import sk.catheaven.utils.DataFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author catlord
 */
@DisplayName("Assembler should")
public class AssembleLabeledUserCodeTest extends CPUContainer {
	
	@Test
	@DisplayName("correctly parse instructions to instruction codes (iCodes) and labels to correct addresses")
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

	@Test
	@DisplayName("correctly point out line numbers with errors on randomly generated")
	public void displayCorrectLineNumbersWithErrors() {
		for (int i = 0; i < 100; i++) {
			Tuple<String, List<Integer>> codeWithErrors = loadCodeWithErrors();
			List<Integer> errorLineNumbers = codeWithErrors.getRight();
			List<AssembledInstruction> assembledInstructions = assembler.assembleCode(codeWithErrors.getLeft());
			
			assertFalse(assembler.getSyntaxErrors().getLineErrors().isEmpty(), assemblerErrorSupplier());
			assertEquals(errorLineNumbers.size(), assembler.getSyntaxErrors().size(), assemblerErrorSupplier());
			
			System.out.println("Expecting errors on lines:");
			for (Integer errorLineIndex : errorLineNumbers)
				System.out.println("\t" + errorLineIndex);
			
			System.out.println("Assembler found errors on lines:");
			System.out.println(assemblerErrorSupplier().get());
			
			for (Tuple<Integer, String> lineError : assembler.getSyntaxErrors().getLineErrors()) {
				assertTrue(errorLineNumbers.contains(lineError.getLeft()));
			}
			
			assembler.getSyntaxErrors().clear();
		}
	}
	
	private Tuple<String, List<Integer>> loadCodeWithErrors() {
		String[] codeLines = createCodeLines();
		List<Integer> errorLineNumbers = new ArrayList<>();
		StringBuilder wholeCode = new StringBuilder();
		int currentLine = 0;		// keep track of the line the generated code is currently on
		
		// ensure AT LEAST some errors
		while (errorLineNumbers.size() < 5) {
			int randomLineIndex = (int) (Math.random() * codeLines.length);		// get random code line
			
			if (Math.random() <= 0.11) {
				wholeCode.append("ERROR ").append(codeLines[randomLineIndex]).append("\n");		// modify line and add error
				errorLineNumbers.add(currentLine);
			}
			else {
				if (Math.random() < 0.25)
					wholeCode.append("\n");        // append empty line
				else
					wholeCode.append(codeLines[randomLineIndex]).append("\n"); // append line without errors
			}
			
			currentLine++;
		}
		errorLineNumbers = errorLineNumbers.stream().sorted().collect(Collectors.toList());
		System.out.println("CODE:\n`"+ wholeCode + "`");
		return new Tuple<>(wholeCode.toString(), errorLineNumbers);
	}
	
	private static String[] createCodeLines() {
		List<String> code = new ArrayList<>();
		code.add("subi r1, r8, 6");
		code.add("li  r6, 6");
		code.add(" mul r17, r23,    r2");
		code.add("sllv r6,    r1, r6");
		code.add("	sub r1, r1 ,r30");
		code.add("add r1, r1, r3");
		code.add("li r7, 154");
		code.add("sub r1, r1 ,r30");
		code.add("sub r1, r6 ,r13");
		code.add("li r1, 951");
		code.add("lw r6, 123(r6)");
		code.add("mulu r6, r1, r1");
		code.add("mulu r1 ,  r1, r1");
		code.add("nop");
		code.add("nop");
		code.add("nop");
		code.add(" mulu r6,  r1,  r1");
		code.add("nop         ");
		code.add("                 nop ");
		code.add("      nop");
		code.add("nop ");
		code.add("add r1, r1, r7");
		code.add("mulu r6, r1, r1");
		return code.toArray(new String[0]);
	}
}
