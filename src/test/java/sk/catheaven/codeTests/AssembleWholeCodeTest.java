/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.codeTests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.hardware.CPU;
import sk.catheaven.instructionEssentials.AssembledInstruction;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.instructionEssentials.Data;
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
		List<AssembledInstruction> isList;
		
		try{
			isList = assembler.assembleCode(code);
		} catch(SyntaxException e){ System.out.println(e.getMessage()); fail("Exception shouldn't have been cought !"); return; }
		
		// checking label addresses
		Map<String, Data> labels = assembler.getLabels();

		assertEquals("00000010", labels.get("cat").getHex());
		assertEquals("0000002c", labels.get("next").getHex());
		assertEquals("00000034", labels.get("super_label_version_million").getHex());
		assertEquals("0000005c", labels.get("ending").getHex());

		// checking each instruction translation
		// expected values are from MipSIM (previous version)
		assertEquals("25010006", isList.get(0).getIcode().getHex());
		assertEquals("90060006", isList.get(1).getIcode().getHex());
		assertEquals("02e28818", isList.get(2).getIcode().getHex());
		assertEquals("00263001", isList.get(3).getIcode().getHex());
		assertEquals("003e0822", isList.get(4).getIcode().getHex());
		assertEquals("00230820", isList.get(5).getIcode().getHex());
		assertEquals("9007009a", isList.get(6).getIcode().getHex());		// li instruction input value is 10-base
		assertEquals("14e1fff0", isList.get(7).getIcode().getHex());
		assertEquals("1021ffec", isList.get(8).getIcode().getHex());
		assertEquals("10000004", isList.get(9).getIcode().getHex());
		assertEquals("900103b7", isList.get(10).getIcode().getHex());
		assertEquals("8cc6007b", isList.get(11).getIcode().getHex());		// same for offset in lw
		assertEquals("10000000", isList.get(12).getIcode().getHex());
		assertEquals("00213019", isList.get(13).getIcode().getHex());
		assertEquals("00210819", isList.get(14).getIcode().getHex());
		
		assertEquals("48000000", isList.get(15).getIcode().getHex());
		assertEquals("48000000", isList.get(16).getIcode().getHex());
		assertEquals("48000000", isList.get(17).getIcode().getHex());
		assertEquals("10000010", isList.get(18).getIcode().getHex());
		
		assertEquals("48000000", isList.get(19).getIcode().getHex());
		assertEquals("48000000", isList.get(20).getIcode().getHex());
		assertEquals("48000000", isList.get(21).getIcode().getHex());
		assertEquals("48000000", isList.get(22).getIcode().getHex());
		assertEquals("00270820", isList.get(23).getIcode().getHex());
		assertEquals("1000ffac", isList.get(24).getIcode().getHex());
	}
	
	private String loadCode(){
		return "\n\n" +
				"subi r1, r8, 6\n" +
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
