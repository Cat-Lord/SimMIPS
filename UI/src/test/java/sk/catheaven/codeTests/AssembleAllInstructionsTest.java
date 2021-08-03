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
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.hardware.CPU;
import sk.catheaven.instructionEssentials.AssembledInstruction;
import sk.catheaven.instructionEssentials.Assembler;
import sk.catheaven.main.Loader;

/**
 * Tests all instructions with random values defined previously. Instruction 
 * codes are tested against code obtained from MipSIM.
 * @author catlord
 */
public class AssembleAllInstructionsTest {
	private Assembler assembler;
	
	public AssembleAllInstructionsTest() {
	}
	
	@Before
	public void setUp() throws IOException, URISyntaxException {
		Loader l = new Loader("design/instructions.json", "design/cpu.json");
		CPU cpu = l.getCPU();
		assembler = cpu.getAssembler();
	}
	
	@Test
	public void test(){
		String code = getCode();
		List<String> expectedICodes = getExpectedICodes();
		
		List<AssembledInstruction> isList;
		
		try{
			isList = assembler.assembleCode(code);
		} catch(SyntaxException e){ System.out.println(e.getMessage()); fail("Exception shouldn't have been cought !"); return; }

		for(int i = 0; i < isList.size(); i++){
			System.out.println(i + " : " + expectedICodes.get(i));
			assertEquals(expectedICodes.get(i), isList.get(i).getIcode().getHex());
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
		return "ADD r1,r0,r0\n"
				+ "ADDI r1,r0,0000\n"
				+ "AND r1,r0,r0\n"
				+ "ANDI r1,r0,0000\n"
				+ "BEQ r0,r5,nope\n"
				+ "BNEQ r0,r0,nope\n"
				+ "DIV r1,r0,r0\n"
				+ "DIVU r1,r0,r0\n"
				+ "LI r5,0005\n"
				+ "LUI r1,65535\n"
				+ "LW r1,0000(r0)\n"
				+ "MUL r1,r0,r0\n"
				+ "MULU r1,r0,r0\n"
				+ "NOP \n"
				+ "NOR r1,r0,r0\n"
				+ "OR r1,r0,r0\n"
				+ "ORI r1,r0,0000\n"
				+ "SLLV r1,r0,r0\n"
				+ "nope:SRLV r1,r0,r0\n"
				+ "SUB r1,r0,r0\n"
				+ "SUBI r1,r0,0005\n"
				+ "SW r1,0000(r0)\n"
				+ "XOR r1,r0,r0\n"
				+ "XORI r1,r0,0000";
	}
}
