/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.catheaven.instructionTests;

import java.io.IOException;
import java.net.URISyntaxException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import sk.catheaven.exceptions.SyntaxException;
import sk.catheaven.hardware.CPU;
import sk.catheaven.instructionEssentials.AssembledInstruction;
import sk.catheaven.run.Loader;

/**
 *
 * @author catlord
 */
public class AssembledInstructionTest {
	CPU cpu;
	
	public AssembledInstructionTest() {
	}
	
	
	@BeforeClass
	public static void setUpClass() throws IOException, URISyntaxException {
	}
	
	@Before
	public void setUp() throws IOException, URISyntaxException {
		Loader l = new Loader("sk/catheaven/data/layout.json", "sk/catheaven/data/cpu.json");
		cpu = l.getCPU();
	}
	
	@Test
	public void test(){
		
		//
		// testing for success
		//
		try{
			AssembledInstruction ai = cpu.assembleInstruction("add r0, r0, r0");
			assertEquals("00000020", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("and r1,r0,r0");
			assertEquals("00000824", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("div r1,r0,r0");
			assertEquals("0000081a", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("divu r1,r0,r0");
			assertEquals("0000081b", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("mul r1,r0,r0");
			assertEquals("00000818", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("mulu r1,r0,r0");
			assertEquals("00000819", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("nor r1,r0,r0");
			assertEquals("00000827", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("or r1,r0,r0");
			assertEquals("00000825", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("sllv r1,r0,r0");
			assertEquals("00000801", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("srlv r1,r0,r0");
			assertEquals("00000802", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("sub r1,r0,r0");
			assertEquals("00000822", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("xor r1,r0,r0");
			assertEquals("00000826", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		// instructions with imm value
		try{
			AssembledInstruction ai = cpu.assembleInstruction("addi r1, r0, 0");
			assertEquals("20010000", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("andi r1, r0, 00000");
			assertEquals("30010000", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("li r5, 5");
			assertEquals("90050005", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("lui r1, 65535");		// ffff
			assertEquals("9401ffff", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("ori r1, r0, 0000");
			assertEquals("34010000", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("subi r1, r0, 5");
			assertEquals("24010005", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		try{
			AssembledInstruction ai = cpu.assembleInstruction("xori r1, r0, 000");
			assertEquals("38010000", ai.getData().getHex());
		} catch(SyntaxException e) { System.err.println(e.getMessage()); fail("Exception cought !"); }
		
		
		//
		// testing for exceptions
		//
		try{
			cpu.assembleInstruction("");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("add");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("add add, add, add");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("add r1");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("add r1,");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("add r1, r2");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("add r1, r2, ");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("add e1, r2, r3");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("add r1, r2, r3, r4");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("add r, 1, 6r");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("addd r1, r2, r2");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("addi r6, r2");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("addi r4, r7, 51326");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("addi 56215, 4214");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("addi r5, 3f142h");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("lw r1, r1");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("lw 1. 1)r1_");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("sw 6r, 1(tg)");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("sw r1, 12095()");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("sw , 52(r1)");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("sw r84, 907g(r632)");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("lw r2, 6136(r1b2)");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("lw r2, 6136  (b2)");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("lw r2, 6136(  r1b2   )");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("lw r2, 6136, (r1b2)");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
		
		try{
			cpu.assembleInstruction("lw r2, 6136(r1b2)(r2)");
			fail("Should've cought exception !");
		} catch(SyntaxException e) { System.out.println("Success, found exception !"); System.err.println(e.getMessage()); }
	}
	
	
}
