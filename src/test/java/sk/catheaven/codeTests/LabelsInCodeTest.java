/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.codeTests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.hardware.CPU;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.run.Loader;

/**
 * Tests labels in code. Tries to find successful code while using
 * labels correctly and also exceptions while using labels incorrectly.
 * @author catlord
 */
public class LabelsInCodeTest {
	private Assembler assembler;
	private List<String> correctCodes;
	private List<String> incorrectCodes;
	
	public LabelsInCodeTest() {
	}
	
	@Before
	public void set() throws IOException, URISyntaxException{
		Loader l = new Loader("sk/catheaven/data/layout.json", "sk/catheaven/data/cpu.json");
		CPU cpu = l.getCPU();
		assembler = cpu.getAssembler();
		
		correctCodes = new ArrayList<>();
		incorrectCodes = new ArrayList<>();
		
		// ----------------------------
		correctCodes.add("cat: addi r1, r1, 1\n\n"
				+ "beq r0, r0, cat");
		correctCodes.add("cat			 : addi r1, r1, 1\n\n"
				+ "beq r0, r0, cat\n"
				+ "roberto : \n"
				+ "li r9, 928");
		correctCodes.add("cat: beq r0, r0, cat");
		correctCodes.add("add r6, r1, r0\n\n"
				+ "	cc: li r16, 130430\n"
				+ "d0g_com_32_haha: xori r9, r1, 4 \n\n\n\n\n\n\tandi r8,r1,0\n\n\n\n\n");
		
		correctCodes.add("add r6, r1, r0\n\n"
				+ "	cc:    li r16, 130430\n"
				+ "d0g_com_32_haha: xori r9, r1, 4 \n\n\n\n\n\n\tandi r8,r1,0\n\n\n\n\n");
		correctCodes.add("add r6, r1, r0\n\n"
				+ "	cc:		"
				+ "li r16, 130430\n"
				+ "d0g_com_32_haha: xori r9, r1, 4 \n\n\n\n\n\n\tandi r8,r1,0\n\n\n\n\n");
		
		correctCodes.add("add r6, r1, r0\n\n"
				+ "	 	cc:		li	r16, 130430\n"
				+ "	 d0g_com_32_haha:	  xori r9, r1, 4 \n\n\n\n\n\n\tandi r8,r1,0\n\n\n\n\n");
		
		// ----------------------------
		incorrectCodes.add("cat:");
		incorrectCodes.add("cat  : : mul r8, r12, r26");
		incorrectCodes.add("add r1, r2, r3\n"
						 + "cat:");
		incorrectCodes.add("add r1, r2, r3\n"
						 + "cat:");
		incorrectCodes.add("add r1, r2, r3\n"
						 + "cat: li  r9, 1268\n"
						 + "beq r0, r0, dog");
		
		incorrectCodes.add("add r1, r2, r3\n"
						 + "cat: li r9, 1268\n"
						 + "beq r0, r10, cat\n"
						 + "dog:;comment for confusement");
		
		incorrectCodes.add("add r1, r2, r3\n"
						 + "cat: li  r9, 1268\n"
						 + "beq r0, r10, cat\n"
						 + "dog:\n"
						 + "; another comment for \n"
						 + "; confusement");
		
		incorrectCodes.add("add r1, r2, r3\n"
						 + "cat: li  r9, 1268\n"
						 + "beq r0, r10, cat\n"
						 + "dog:		;;;;;;;;;comment for confusement");
		
		incorrectCodes.add("add r1, r2, r3\n"
						 + "cat: li  r9, 1268\n"
						 + "beq r0, r10, cat\n"
						 + "dog:		;;;;;;;;;comment for confusement\n"
						 + "\n"
						 + "\n"
						 + "deg2: ;and a comment on top of that !\n\n\n\n\n\n\t\n\n\n\n\n");
		
		incorrectCodes.add("dog1:d0g2:dog3::dogg3");
		
		incorrectCodes.add("add r1, r2, r3\n"
						 + "cat: li  r9, 1268\n"
						 + "beq r0, r10, cat\n"
						 + "dog:		;;;;;;;;;comment for confusement\n"
						 + "\n"
						 + "\n"
						 + "deg2: ;and a comment on top of that !");
		
		incorrectCodes.add("add r1, r2, r3\n"
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
	}
	
	@Test
	public void test(){
		System.out.println("=====================Correct codes incoming============================");
		for(String code : correctCodes){
			try {
				assembler.assembleCode(code);
				System.out.println("----------------------------------------");
			} catch (SyntaxException e) { 
				System.out.println(e.getMessage());
				fail("Exception shouldn't have been cought ! Respective code: \n" + code); 
			}
		}
		
		System.out.println("\n=====================Incorrect codes incoming============================\n");
		
		for(String code : incorrectCodes){
			try {
				assembler.assembleCode(code);
				fail("Exception should have been cought ! Respective code: \n" + code);
			} catch(SyntaxException e) { System.out.println("Success, exception has been cought ! --"); System.out.println(e.getMessage()); System.out.println("----------------------------------------"); }
		}
	}
}
