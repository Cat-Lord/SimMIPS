/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.codeTests;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.hardware.CPU;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.run.Loader;

/**
 *
 * @author catlord
 */
public class AssembleWholeCodeTest {
	Assembler assembler;
	
	public AssembleWholeCodeTest() {
	}
	
	@Before
	public void setUp() throws IOException, URISyntaxException {
		Loader l = new Loader("sk/catheaven/data/layout.json", "sk/catheaven/data/cpu.json");
		CPU cpu = l.getCPU();
		assembler = cpu.getAssembler();
	}
	
	@Test
	public void test() {
		String code = loadCode();		// just to not cover the entire screen with strings
		
		try{
			assembler.AssembleCode(code);
		} catch(SyntaxException e){ System.out.println(e.getMessage()); fail("Exception shouldn't have been cought !"); }
		
	}
	
	private String loadCode(){
		return "\n\nsubi r1, r8, 6\n" +
				"li r6, 6\n" +
				"mul r17, r23,r2\n" +
				"sllv r6, r1, r6\n" +
				"\n" +
				"cat:                 sub r1, r1 ,r30\n" +
				"add r1, r1, r3\n" +
				"li r7, 154\n" +
				"bneq r1, r7, cat\n" +
				"beq r1, r1, cat\n" +
				"beq r0, r0, next\n" +
				"\n" +
				"li r1, 951\n" +
				"next: lw r6, 123(r6)\n" +
				"beq r0, r0, super_label_version_million\n" +
				"\n" +
				"super_label_version_million: \n" +
				"; now a little comment for this awesome example\n" +
				"mulu r6, r1, r1\n" +
				"mulu r1, r1, r1\n" +
				"nop\n" +
				"nop\n" +
				"nop\n" +
				"beq r0, r0, ending\n" +
				"nop\n" +
				"nop\n" +
				"nop\n" +
				"nop\n" +
				"ending:add r1, r1, r7\n" +
				"beq r0, r0, cat";
	}
}
