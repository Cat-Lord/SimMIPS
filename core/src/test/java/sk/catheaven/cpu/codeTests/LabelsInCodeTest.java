/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.cpu.codeTests;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import sk.catheaven.cpu.CPUContainer;
import sk.catheaven.model.Tuple;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests labels in code. Tries to assemble successfully correct code
 * with labels correctly and also catch errors while using code with
 * incorrect labels (either incorrect usage or label format).
 * @author catlord
 */
public class LabelsInCodeTest extends CPUContainer {
	
	@Test
	public void testCorrectCodes() {
		for (String code : getCorrectCodes()) {
			assembler.assembleCode(code);
			assertTrue(assembler.getSyntaxErrors().isEmpty(), () -> {
				StringBuilder errors = new StringBuilder("\n");
				for (Tuple<Integer, String> error : assembler.getSyntaxErrors().getLineErrors())
					errors.append("\t").append(error.getLeft()).append(": ").append(error.getRight()).append("\n");
				
				for (String error : assembler.getSyntaxErrors().getMessageErrors())
					errors.append("\t").append(error).append("\n");
				return errors.toString();
			});
		}
	}
	
	@Test
	public void testIncorrectCodes() {
		for (String code : getIncorrectCodes()) {
			assembler.assembleCode(code);
			assertFalse(assembler.getSyntaxErrors().isEmpty(), "Code was expected to fail ! `" + code + "`");
		}
	}
	
	private List<String> getCorrectCodes() {
		List<String> codes = new ArrayList<>();
		codes.add("cat: addi r1, r1, 1\n\n"
				+ "beq r0, r0, cat");
		codes.add("cat			 : addi r1, r1, 1\n\n"
				+ "beq r0, r0, cat\n"
				+ "roberto : \n"
				+ "li r9, 928");
		codes.add("cat: beq r0, r0, cat");
		codes.add("add r6, r1, r0\n\n"
				+ "	cc: li r16, 130430\n"
				+ "d0g_com_32_haha: xori r9, r1, 4 \n\n\n\n\n\n\tandi r8,r1,0\n\n\n\n\n");
		
		codes.add("add r6, r1, r0\n\n"
				+ "	cc:    li r16, 130430\n"
				+ "d0g_com_32_haha: xori r9, r1, 4 \n\n\n\n\n\n\tandi r8,r1,0\n\n\n\n\n");
		codes.add("add r6, r1, r0\n\n"
				+ "	cc:		"
				+ "li r16, 130430\n"
				+ "d0g_com_32_haha: xori r9, r1, 4 \n\n\n\n\n\n\tandi r8,r1,0\n\n\n\n\n");
		
		codes.add("add r6, r1, r0\n\n"
				+ "	 	cc:		li	r16, 130430\n"
				+ "	 d0g_com_32_haha:	  xori r9, r1, 4 \n\n\n\n\n\n\tandi r8,r1,0\n\n\n\n\n");
		return codes;
	}
	private List<String> getIncorrectCodes() {
		List<String> codes = new ArrayList<>();
		
		codes.add("cat:");
		codes.add("cat  : : mul r8, r12, r26");
		codes.add("add r1, r2, r3\n"
				+ "cat:");
		codes.add("add r1, r2, r3\n"
				+ "cat:");
		codes.add("add r1, r2, r3\n"
				+ "cat: li  r9, 1268\n"
				+ "beq r0, r0, dog");
		
		codes.add("add r1, r2, r3\n"
				+ "cat: li r9, 1268\n"
				+ "beq r0, r10, cat\n"
				+ "dog:;comment for confusement");
		
		codes.add("add r1, r2, r3\n"
				+ "cat: li  r9, 1268\n"
				+ "beq r0, r10, cat\n"
				+ "dog:\n"
				+ "; another comment for \n"
				+ "; confusement");
		
		codes.add("add r1, r2, r3\n"
				+ "cat: li  r9, 1268\n"
				+ "beq r0, r10, cat\n"
				+ "dog:		;;;;;;;;;comment for confusement");
		
		codes.add("add r1, r2, r3\n"
				+ "cat: li  r9, 1268\n"
				+ "beq r0, r10, cat\n"
				+ "dog:		;;;;;;;;;comment for confusement\n"
				+ "\n"
				+ "\n"
				+ "deg2: ;and a comment on top of that !\n\n\n\n\n\n\t\n\n\n\n\n");
		
		codes.add("dog1:d0g2:dog3::dogg3");
		
		codes.add("add r1, r2, r3\n"
				+ "cat: li  r9, 1268\n"
				+ "beq r0, r10, cat\n"
				+ "dog:		;;;;;;;;;comment for confusement\n"
				+ "\n"
				+ "\n"
				+ "deg2: ;and a comment on top of that !");
		
		codes.add("add r1, r2, r3\n"
				+ "cat: li  r9, 1268\n"
				+ "beq r0, r10, cat\n"
				+ "dog:		;;;;;;;;;comment for confusement\n"
				+ "\n"
				+ "\n"
				+ "add r2, r56, r21 ;f efa ev vav A_EV v.cv.a !2e 2e1561;235\n"
				+ "deg2: ; label without instruction following !!!\n"
				+ "\n"
				+ "\n"
				+ "\n"
				+ "; THATS s1mply unn4actept4ble !@#!%*$%^(:)*_534672");
		return codes;
	}
}
